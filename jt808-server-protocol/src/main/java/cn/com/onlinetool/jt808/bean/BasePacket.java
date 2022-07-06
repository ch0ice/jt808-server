package cn.com.onlinetool.jt808.bean;

import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.util.BCD;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
public class BasePacket implements Serializable {

    protected Header header = new Header(); //消息头
    protected ByteBuf msgBody; //消息体

    public BasePacket() {
    }

    public BasePacket(ByteBuf payload) {
        this.msgBody = payload;
    }

    public void parseHeader() {
        //1. 解析消息头
        //获取消息ID(2个字节)
        int msgId = this.msgBody.readUnsignedShort();
        //将10进制的msgId转换成16进制
        msgId = Integer.parseInt(Integer.toHexString(msgId), 16);
        this.header.setMsgId(msgId);
        //获取消息体属性(2个字节)
        this.header.setMsgBodyProps(this.msgBody.readUnsignedShort());
        //设置是否加密
        this.header.setEncryptionType((byte) ((this.header.msgBodyProps & 0x1c00) >> 10));
        //设置是否是2019年版本
        this.header.setVersion2019(((this.header.getMsgBodyProps() & 0x4000) >> 14) == 1);
        if (this.header.isVersion2019()) {
            //2019年协议新增协议版本号(1个字节)
            this.header.setProtocolVersion(this.msgBody.readUnsignedByte());
            //2019年协议终端手机号为10个字节
            this.header.setTerminalPhone(BCD.bcdToString(readBytes(10)));
        } else {
            //2011、2013年协议终端手机号为6个字节
            this.header.setTerminalPhone(BCD.bcdToString(readBytes(6)));
        }
        //获取终端的消息流水号(2个字节)
        this.header.setFlowId(this.msgBody.readUnsignedShort());
        //是否有分包
        boolean subPackage = ((this.header.getMsgBodyProps() & 0x2000) >> 13) == 1;
        if (subPackage) {
            this.header.setSubPackage(true);
            //消息总包数(2个字节)
            this.header.setTotalPackage(this.msgBody.readUnsignedShort());
            //包序号(2个字节)
            this.header.setPackageNumber(this.msgBody.readUnsignedShort());
        }
        //2. 验证包体长度
        //消息头中消息体属性消息长度是否等于剩余可读字节
        if (this.header.getMsgBodyLength() != this.msgBody.readableBytes()) {
            throw new RuntimeException("包体长度有误");
        }
    }

//    /**
//     * 请求报文重写
//     */
//    protected void parseBody() {
//        //子类重写该方法
//    }

    /**
     * 响应报文重写 并调用父类
     * @return
     */
    public ByteBuf toByteBufMsg() {
        //在JT808Encoder escape()方法处回收
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        //设置消息ID
        byteBuf.writeShort(this.header.getMsgId());

        //是否分包
        boolean subPackage = this.header.isSubPackage();
        int subPkg = subPackage ? 1 : 0;
        //是否是2019年版本
        boolean version2019 = this.header.isVersion2019();
        int is2019 = version2019 ? 1 : 0;
        //消息体长度
        int msgLength = 0;
        if(this.msgBody != null){
            msgLength = this.msgBody.readableBytes();
        }
        //构建消息体属性(消息体长度、数据加密方式、分包、是否为2019年协议、保留字段)
        short msgBodyProps = (short) (((msgLength & 0x3FF) | (this.header.getEncryptionType() << 10 & 0x1C00) | ((subPkg << 13) & 0x2000) | ((is2019 << 14) & 0xC000)) & 0xffff);
        //设置消息体属性
        byteBuf.writeShort(msgBodyProps);
        if (version2019) {
            byteBuf.writeByte(this.header.getProtocolVersion());
        }
        //需要转换成BCD码, 6个字节或者10个字节
        byteBuf.writeBytes(BCD.toBcdBytes(this.header.getTerminalPhone()));
        //设置消息流水号(2个字节)
        byteBuf.writeShort(this.header.getFlowId());
        //根据是否分包设置总包数和包序号(分别是2个字节)
        if (this.header.isSubPackage()) {
            byteBuf.writeShort(this.header.getTotalPackage());
            byteBuf.writeShort(this.header.getPackageNumber());
        }
        //写入消息体
        if(this.msgBody != null){
            byteBuf.writeBytes(this.msgBody);
        }
        return byteBuf;
    }

    /**
     * 从ByteBuf中read固定长度的数组,相当于ByteBuf.readBytes(byte[] dst)的简单封装
     * @param length
     * @return
     */
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        this.msgBody.readBytes(bytes);
        return bytes;
    }

    /**
     * 从ByteBuf中读出固定长度的数组 ，根据808默认字符集构建字符串
     * @param length
     * @return
     */
    public String readString(int length) {
       return new String(readBytes(length), JT808Constant.DEFAULT_CHARSET);
    }

    @Data
    public static class Header implements Serializable{


        private int msgId;//消息ID 2字节(由于是无符号使用大的数据类型存放)
        private int msgBodyProps;//消息体属性 2字节(由于是无符号使用大的数据类型存放)
        private short protocolVersion;//协议版本号 1字节(2019年协议, 新增字段)(由于是无符号使用大的数据类型存放)
        private boolean isVersion2019;//是否是2019年协议
        private String terminalPhone;//终端手机号 6字节(2019年版协议为10字节) BCD码
        private int flowId;//流水号 2字节(由于是无符号使用大的数据类型存放)
        private int totalPackage;//消息总包数 2字节(由于是无符号使用大的数据类型存放)
        private int packageNumber;//包序号 2字节(由于是无符号使用大的数据类型存放)
        private boolean isSubPackage;//是否分包
        private byte encryptionType;//加密类型 3字节

        //获取包体长度
        public short getMsgBodyLength() {
            return (short) (msgBodyProps & 0x3ff);
        }

        //获取加密类型 3bits
        public byte getEncryptionType() {
            return (byte) ((msgBodyProps & 0x1c00) >> 10);
        }

        //是否分包
        public boolean hasSubPackage() {
            return ((msgBodyProps & 0x2000) >> 13) == 1;
        }
    }
}
