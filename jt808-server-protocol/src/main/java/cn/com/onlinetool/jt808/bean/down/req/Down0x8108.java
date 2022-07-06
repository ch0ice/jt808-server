package cn.com.onlinetool.jt808.bean.down.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 下发终端升级包
 */
@ToString(callSuper = true)
@Data
public class Down0x8108 extends BasePacket implements Serializable {

    private int type;//升级类型 1字节 0:终端，12:道路运输证 IC 卡读卡器，52:北斗 卫星定位模块
    private String manufacturerId;//制造商ID 5字节
    private int softVersionNumLen;//固件版本号长度 1字节
    private String softVersionNum;//固件版本号
    private int packetLen;//升级包长度 4字节
    private byte[] packet;//升级包数据


    public Down0x8108(String terminalPhone){
        this.getHeader().setTerminalPhone(terminalPhone);
        this.getHeader().setMsgId(JT808Constant.DOWN_TERMINAL_UPGRADE_REQ);
    }

    @Override
    public ByteBuf toByteBufMsg() {
        ByteBuf bb = super.toByteBufMsg();
        bb.writeByte(this.type);
        bb.writeBytes(this.manufacturerId.getBytes(JT808Constant.DEFAULT_CHARSET));
        bb.writeByte(this.softVersionNumLen);
        bb.writeBytes(this.softVersionNum.getBytes(JT808Constant.DEFAULT_CHARSET));
        bb.writeInt(this.packetLen);
        bb.writeBytes(this.packet);

        return bb;

    }

    /**
     * 测试固件升级文件
     */
    private void testFile(){
        ByteArrayInputStream bis = new ByteArrayInputStream(this.getPacket());
        try(FileOutputStream fos = new FileOutputStream("/Users/edy/Downloads/banner111.txt");){
            byte[] buffer = new byte[1024*4];
            int n = 0;
            while (-1 != (n = bis.read(buffer))) {
                fos.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}