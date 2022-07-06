package cn.com.onlinetool.jt808.util;

import io.netty.buffer.ByteBuf;


public class JT808Util {

    /**
     * 根据byteBuf的readerIndex和writerIndex计算校验码
     * 校验码规则：从消息头开始，同后一字节异或，直到校验码前一个字节，占用 1 个字节
     *
     * @param byteBuf
     * @return
     */
    public static byte xorSumBytes(ByteBuf byteBuf) {
        byte sum = byteBuf.getByte(byteBuf.readerIndex());
        for (int i = byteBuf.readerIndex() + 1; i < byteBuf.writerIndex(); i++) {
            sum = (byte) (sum ^ byteBuf.getByte(i));
        }
        return sum;
    }


    public static String delZeorStr(String str) {
        return str.replaceAll("\\u0000", "");
    }

    public static float byteBufToSixFloat(long n) {
        return n * 1.0F / 1000000;
    }

}
