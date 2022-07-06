package cn.com.onlinetool.jt808.codec;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.util.JT808Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class JT808Encoder extends MessageToByteEncoder<BasePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BasePacket msg, ByteBuf out) {
        //将消息转成成byteBuf
        ByteBuf bb = msg.toByteBufMsg();
        //设置校验码
        bb.writeByte(JT808Util.xorSumBytes(bb));
        log.info(">>>>> ip: {}, hex: {}\n", ctx.channel().remoteAddress(), ByteBufUtil.hexDump(bb));
        //转义待发送的数据
        ByteBuf escape = escape(bb);
        //发送数据
        out.writeBytes(escape);
        ReferenceCountUtil.safeRelease(msg.getMsgBody());
        ReferenceCountUtil.safeRelease(escape);
    }

    /**
     * 转义待发送数据
     *
     * @param raw
     * @return
     */
    private ByteBuf escape(ByteBuf raw) {
        int len = raw.readableBytes();
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(len);
        //拼接首尾的消息分隔符
        buf.writeByte(JT808Constant.PKG_DELIMITER);
        //进行消息的转义
        while (len > 0) {
            byte b = raw.readByte();
            if (b == 0x7e) {
                buf.writeByte(0x7d);
                buf.writeByte(0x02);
            } else if (b == 0x7d) {
                buf.writeByte(0x7d);
                buf.writeByte(0x01);
            } else {
                buf.writeByte(b);
            }
            len--;
        }
        ReferenceCountUtil.safeRelease(raw);
        //拼接首尾的消息分隔符
        buf.writeByte(JT808Constant.PKG_DELIMITER);
        return buf;
    }


}
