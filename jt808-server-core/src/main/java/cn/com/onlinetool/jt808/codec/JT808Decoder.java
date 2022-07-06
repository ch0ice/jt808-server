package cn.com.onlinetool.jt808.codec;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.util.JT808Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.com.onlinetool.jt808.consts.JT808Constant.MIN_PACKET_HEADER_NUMBER;

/**
 */
@Slf4j
public class JT808Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        log.info("<<<<< ip:{},hex:{}", ctx.channel().remoteAddress(), ByteBufUtil.hexDump(in));
        BasePacket msg = decode(in);
        if (msg != null) {
            out.add(msg);
        }
    }

    private BasePacket decode(ByteBuf in) {
        if (in.readableBytes() < MIN_PACKET_HEADER_NUMBER) { //包头最小长度
            return null;
        }
        //转义
        byte[] raw = new byte[in.readableBytes()];
        in.readBytes(raw);
        ByteBuf escape = revert(raw);
        //获取校验码
        byte pkgCheckSum = escape.getByte(escape.writerIndex() - 1);
        //排除校验码
        escape.writerIndex(escape.writerIndex() - 1);
        //计算校验码
        byte calCheckSum = JT808Util.xorSumBytes(escape);

        if (pkgCheckSum != calCheckSum) {
            log.warn("校验码错误,pkgCheckSum:{},calCheckSum:{}", pkgCheckSum, calCheckSum);
            ReferenceCountUtil.safeRelease(escape);
            return null;
        }
        //解码(这里只解析消息头), 具体的消息体解析交由具体的消息处理器解析
        BasePacket packet = new BasePacket(escape);
        packet.parseHeader();
        return packet;
    }

    /**
     * 将接收到的原始转义数据还原
     *
     * @param raw
     * @return
     */
    public ByteBuf revert(byte[] raw) {
        int len = raw.length;
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(len);//DataPacket parse方法回收
        for (int i = 0; i < len; i++) {
            //这里如果最后一位是0x7d会导致index溢出，说明原始报文转义有误
            if (raw[i] == 0x7d && raw[i + 1] == 0x01) {
                buf.writeByte(0x7d);
                i++;
            } else if (raw[i] == 0x7d && raw[i + 1] == 0x02) {
                buf.writeByte(0x7e);
                i++;
            } else {
                buf.writeByte(raw[i]);
            }
        }
        return buf;
    }

}
