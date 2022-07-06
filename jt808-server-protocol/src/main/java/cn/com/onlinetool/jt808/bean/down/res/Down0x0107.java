package cn.com.onlinetool.jt808.bean.down.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.util.BCD;
import cn.com.onlinetool.jt808.util.ByteArrayUtil;
import cn.com.onlinetool.jt808.util.JT808Util;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author: choice
 * 查询终端属性应答
 */
@Data
public class Down0x0107 extends BasePacket {
    public Down0x0107(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    /**
     * 终端类型 2字节
     * bit0，0:不适用客运车辆，1:适用客运车辆;
     * bit1，0:不适用危险品车辆，1:适用危险品车辆;
     * bit2，0:不适用普通货运车辆，1:适用普通货运车辆;
     * bit3，0:不适用出租车辆，1:适用出租车辆;
     * bit6，0:不支持硬盘录像，1:支持硬盘录像;
     * bit7，0:一体机，1:分体机。
     */
    private String type;
    private String manufacturerId;//制造商ID 5字节
    private String terminalType;//终端型号 20字节
    private String terminalId;//终端id 7字节
    private String iccId;//终端 SIM 卡 ICCID 10字节
    private int hardVersionNumLen;//终端硬件版本号长度 1字节
    private String hardVersionNum;//终端硬件版本号
    private int softVersionNumLen;//终端固件版本号长度 1字节
    private String softVersionNum;//终端固件版本号
    private String gnssProperty;//GNSS模块属性 1字节
    private String commProperty;//通信模块属性 1字节

    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setType(ByteArrayUtil.byte2bitStr(bb.readByte()));
        this.setManufacturerId(readString(5));
        this.setTerminalType(JT808Util.delZeorStr(readString(20)));
        this.setTerminalId(JT808Util.delZeorStr(readString(7)));
        this.setIccId(BCD.bcdToString(readBytes(10)));
        this.setHardVersionNumLen(bb.readByte());
        this.setHardVersionNum(readString(this.getHardVersionNumLen()));
        this.setSoftVersionNumLen(bb.readByte());
        this.setSoftVersionNum(readString(this.getHardVersionNumLen()));
        this.setGnssProperty(ByteArrayUtil.byte2bitStr(bb.readByte()));
        this.setCommProperty(ByteArrayUtil.byte2bitStr(bb.readByte()));
    }
}
