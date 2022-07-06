package cn.com.onlinetool.jt808.handler;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.down.req.Down0x8003;
import cn.com.onlinetool.jt808.bean.up.res.Up0x8001;
import cn.com.onlinetool.jt808.config.ChannelHandlerContextManager;
import cn.com.onlinetool.jt808.config.PackHandlerManagement;
import cn.com.onlinetool.jt808.service.CacheService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.com.onlinetool.jt808.consts.JT808Constant.PACKAGE_MAX_LENGTH;

/**
 *
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class BaseHandler extends SimpleChannelInboundHandler<BasePacket> {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ChannelHandlerContextManager channelHandlerContextManager;
    @Autowired
    private PackHandlerManagement packHandlerManagement;

    /**
     * 消息流水号
     */
    private static final AttributeKey<Integer> SERIAL_NUMBER = AttributeKey.newInstance("serialNumber");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BasePacket msg) {

        String terminalPhone = msg.getHeader().getTerminalPhone();

        //维护channel和terminalNumber的关系
        if (channelHandlerContextManager.get(terminalPhone) == null) {
            channelHandlerContextManager.add(terminalPhone, ctx);
        }

        //处理接口消息包, 得到回送的消息包
        BasePacket sendDataPacket = getBasePacket(ctx, msg);

        //发送数据包
        sendMessage(ctx, sendDataPacket);
    }

    private BasePacket getBasePacket(ChannelHandlerContext ctx, BasePacket msg) {
        BasePacket.Header header = msg.getHeader();
        String terminalNumber = header.getTerminalPhone();
        boolean allBasePacket = false;
        BasePacket sendDataPacket = null;
        //分包处理
        if (header.isSubPackage()) {
            //总包数
            int totalPackage = header.getTotalPackage();
            //第几包
            int packageNumber = header.getPackageNumber();
            //map的key -> 终端手机号; value -> (包序号, 原始数据包)
            if (!cacheService.containsPackages(terminalNumber)) {
                ConcurrentHashMap<Integer, BasePacket> buf = new ConcurrentHashMap<>(totalPackage);
                cacheService.setPackages(terminalNumber, buf);
            }
            //如果是中间的数据包, 放入堆内存中缓存起来
            if (packageNumber <= totalPackage) {
                Map<Integer, BasePacket> packages = cacheService.getPackages(terminalNumber);
                packages.put(packageNumber, msg);
                log.info("[分包数据] 终端手机号: {}, 消息ID: {}, 接收到第{}个数据包, 总共{}数据包", terminalNumber, Integer.toHexString(header.getMsgId()), packageNumber, totalPackage);
            }
            //如果接收到的数据包是最后一个数据包, 需要校验是否接收到所有数据包
            if (packageNumber == totalPackage) {
                //如果接收到了所有的数据包, 进行数据包的合并
                if (cacheService.getPackages(terminalNumber).size() == totalPackage) {
                    Map<Integer, BasePacket> packages = cacheService.getPackages(terminalNumber);
                    //根据包序号重新进行升序排序
                    List<ByteBuf> collect = packages.values().stream().sorted(Comparator.comparing(tempData -> tempData.getHeader().getPackageNumber())).map(BasePacket::getMsgBody).collect(Collectors.toList());
                    CompositeByteBuf allDataByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer(collect.size());
                    //将数据包进行合并
                    collect.forEach(tempData -> allDataByteBuf.addComponent(true, tempData));

                    //重新设置数据
                    msg.setMsgBody(allDataByteBuf);
                    allBasePacket = true;
                    log.info("[分包数据] 终端手机号: {}, 消息ID: {}, 接收到完整的数据包", terminalNumber, Integer.toHexString(header.getMsgId()));


                }
                //如果没有接收到所有的数据包, 需要终端补传相应的数据包
                //构建服务器补传分包请求(第一包消息流水号、重传总包数、重传包ID列表)
                else {
                    ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer();
                    Map<Integer, BasePacket> packages = cacheService.getPackages(terminalNumber);
                    BasePacket firstDataPacket = packages.get(1);
                    //如果第一包数据包不存在, 直接报错
                    if (firstDataPacket == null) {
                        return Up0x8001.failure(msg, getSerialNumber(ctx.channel()));
                    } else {
                        //第一包消息的流水号
                        buffer.writeShort(firstDataPacket.getHeader().getFlowId());

                        //补传数量
                        int number = 0;
                        List<Integer> idList = new ArrayList<>();
                        for (int i = 1; i <= totalPackage; i++) {
                            if (!packages.containsKey(i)) {
                                number++;
                                idList.add(i);
                            }
                        }
                        //补传数量
                        buffer.writeShort(number);
                        //重传包ID列表
                        idList.forEach(buffer::writeShort);

                        //补传分包请求
                        log.warn("[补传分包请求] 终端手机号: {}, 消息ID: {}, 服务端发送补传分包请求", terminalNumber, Integer.toHexString(header.getMsgId()));
                        Down0x8003 data0x8003 = new Down0x8003();
                        data0x8003.setMsgBody(buffer);
                        sendDataPacket = data0x8003.build(msg, getSerialNumber(ctx.channel()));
                    }
                }
            }
        }

        //如果没有分包或者是分包的数据是完整的
        if (!header.isSubPackage() || allBasePacket) {
            //根据消息ID, 调用相关的处理器进行处理
            PackHandler packHandler = packHandlerManagement.getPackHandler(header.getMsgId());
            if(packHandler == null){
                log.warn("不支持的消息类型：{}",header.getMsgId());
                sendDataPacket = Up0x8001.unsupported(msg, getSerialNumber(ctx.channel()));
            }else {
                sendDataPacket = packHandler.handle(msg, getSerialNumber(ctx.channel()));
            }
        }

        //如果为null, 默认使用通用应答处理
        if (sendDataPacket == null) {
            sendDataPacket = Up0x8001.failure(msg, getSerialNumber(ctx.channel()));
        }
        return sendDataPacket;
    }

    /**
     * 发送数据包
     *
     * @param ctx
     * @param sendDataPacket
     */
    public void sendMessage(ChannelHandlerContext ctx, BasePacket sendDataPacket) {
        //检验是否需要分包
        if (sendDataPacket.getMsgBody() != null && sendDataPacket.getMsgBody().readableBytes() > PACKAGE_MAX_LENGTH) {
            //分包发送数据
            sentSubPackage(sendDataPacket, ctx);
        } else {
            //如果不需要分包直接发送即可
            write(ctx, sendDataPacket);
        }
    }

    /**
     * 分包发送数据
     *
     * @param sendDataPacket
     * @param ctx
     */
    private void sentSubPackage(BasePacket sendDataPacket, ChannelHandlerContext ctx) {
        ByteBuf byteBuf = sendDataPacket.getMsgBody();
        int length = byteBuf.readableBytes();
        //消息总包数
        int totalPackage = (length + PACKAGE_MAX_LENGTH) / PACKAGE_MAX_LENGTH;
        BasePacket.Header header = sendDataPacket.getHeader();
        //消息ID
        int msgId = header.getMsgId();
        //终端手机号
        String terminalNumber = header.getTerminalPhone();
        for (int i = 1; i <= totalPackage; i++) {
            BasePacket tempDataPacket = new BasePacket();
            BasePacket.Header tempHeader = tempDataPacket.getHeader();
            tempHeader.setMsgId(msgId);
            tempHeader.setMsgBodyProps(0);
            tempHeader.setSubPackage(true);
            tempHeader.setEncryptionType(header.getEncryptionType());
            tempHeader.setVersion2019(header.isVersion2019());
            if (header.isVersion2019()) {
                tempHeader.setProtocolVersion(header.getProtocolVersion());
            }
            //设置终端手机号
            tempHeader.setTerminalPhone(terminalNumber);
            //设置消息流水号
            tempHeader.setFlowId(getSerialNumber(ctx.channel()));
            //设置消息包封装项
            //设置消息总包数
            tempHeader.setTotalPackage(totalPackage);
            //设置包序号
            tempHeader.setPackageNumber(i);
            //设置消息体
            ByteBuf msgBody;
            if (i == totalPackage) {
                msgBody = byteBuf.readBytes(byteBuf.readableBytes());
            } else {
                msgBody = byteBuf.readBytes(PACKAGE_MAX_LENGTH);
            }
            tempDataPacket.setMsgBody(msgBody);
            //TODO 需要将发送的分包数据缓存起来, 这里最好使用弱引用。当jvm虚拟机要发生OOM的时候进行内存回收虚引用的分包数据
            //为什么不用强引用, 使用强引用会导致内存泄漏
            //为什么不用软引用, 分包数据不是很重要, 弱引用的生命周期更短, 能够更快得被垃圾回收
            this.write(ctx, tempDataPacket);
        }
    }

    /**
     * 递增获取流水号
     *
     * @return
     */
    public Integer getSerialNumber(Channel channel) {
        Attribute<Integer> flowIdAttr = channel.attr(SERIAL_NUMBER);
        Integer flowId = flowIdAttr.get();
        if (flowId == null) {
            flowId = 0;
        } else {
            flowId++;
        }
        flowIdAttr.set(flowId);
        return flowId;
    }

    public void write(ChannelHandlerContext ctx, BasePacket msg) {
        ctx.writeAndFlush(msg).addListener(future -> {
            if (!future.isSuccess()) {
                log.error("发送失败", future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String handlerName = ctx.handler().toString();
        String executorName = ctx.executor().toString();
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.error("[发生异常] 异常信息: {}, 远程地址: {}, 处理器: {}, 执行器: {}", cause.getMessage(), socketAddress.toString(), handlerName, executorName);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //此实例项目只设置了读取超时时间,可以通过state分别做处理,一般服务端在这里关闭连接节省资源，客户端发送心跳维持连接
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("客户端: {}读取超时, 关闭连接", ctx.channel().remoteAddress());
                //移除ChannelGroup中的Channel
                channelHandlerContextManager.remove(ctx);
                ctx.close();
            } else if (state == IdleState.WRITER_IDLE) {
                log.warn("客户端: {}写入超时", ctx.channel().remoteAddress());
            } else if (state == IdleState.ALL_IDLE) {
                log.warn("客户端: {}读取写入超时", ctx.channel().remoteAddress());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
