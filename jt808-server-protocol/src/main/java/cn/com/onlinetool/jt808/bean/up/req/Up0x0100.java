package cn.com.onlinetool.jt808.bean.up.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.util.JT808Util;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

/**
 * 终端注册
 */
@ToString(callSuper = true)
@Data
public class Up0x0100 extends BasePacket {

    private short provinceId;//省域ID 2字节
    private short cityId;//市县ID 2字节
    private String manufacturerId;//制造商ID 5字节
    private String terminalType;//终端型号 8字节
    private String terminalId;//终端ID 7字节
    private byte licensePlateColor;//车牌颜色 1字节
    private String licensePlate;//车牌号 剩余字节

    public Up0x0100(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setProvinceId(bb.readShort());
        this.setCityId(bb.readShort());
        boolean version2019 = this.header.isVersion2019();
        if (version2019) {
            this.setManufacturerId(JT808Util.delZeorStr(readString(11)));
            this.setTerminalType(JT808Util.delZeorStr(readString(30)));
            this.setTerminalId(JT808Util.delZeorStr(readString(30)));
        } else {
            this.setManufacturerId(JT808Util.delZeorStr(readString(5)));
            this.setTerminalType(JT808Util.delZeorStr(readString(20)));
            this.setTerminalId(JT808Util.delZeorStr(readString(7)));
        }

        this.setLicensePlateColor(bb.readByte());
        this.setLicensePlate(JT808Util.delZeorStr(readString(bb.readableBytes())));
    }
}
