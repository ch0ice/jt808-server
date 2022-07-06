package cn.com.onlinetool.jt808.bean.up.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.util.BCD;
import cn.com.onlinetool.jt808.util.ByteArrayUtil;
import cn.com.onlinetool.jt808.util.JT808Util;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

/**
 * 位置信息汇报
 */
@ToString(callSuper = true)
@Data
public class Up0x0200 extends BasePacket {

    public Up0x0200(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private String alarm; //告警信息 4字节
    private String statusField;//状态 4字节
    private float latitude;//纬度 4字节
    private float longitude;//经度 4字节
    private short elevation;//海拔高度 2字节
    private short speed; //速度 2字节
    private short direction; //方向 2字节
    private String time; //时间 6字节BCD
    private Up0x0200Ext up0x0200Ext;//位置附加信息

    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setAlarm(ByteArrayUtil.bytes2bitStr(readBytes(4)));
        this.setStatusField(ByteArrayUtil.bytes2bitStr(readBytes(4)));
        this.setLatitude(JT808Util.byteBufToSixFloat(bb.readUnsignedInt()));
        this.setLongitude(JT808Util.byteBufToSixFloat(bb.readUnsignedInt()));
        this.setElevation(bb.readShort());
        this.setSpeed(bb.readShort());
        this.setDirection(bb.readShort());
        this.setTime(BCD.toBcdTimeString(readBytes(6)));
        if (bb.isReadable()) {
            this.setUp0x0200Ext(new Up0x0200Ext(bb.readBytes(bb.readableBytes())));
        }
    }


    /**
     * @author: choice
     * 扩展附加信息
     */
    @Data
    public static class Up0x0200Ext {
        private Long mileage;//附加信息-标准-里程，DWORD，1/10km，对应车上里程表读数
        private Integer oilMass;//附加信息-标准-油量，WORD，1/10L，对应车上油量表读数
        private Integer speed;//附加信息-标准-行驶记录功能获取的速度，WORD，1/10km/h
        private Integer alarmId;//附加信息-标准-需要人工确认报警事件的 ID，WORD，从 1 开始计数
        private Short wirelessSignal;//附加信息-标准-无线通信网络信号强度 BYTE
        private Short gnssSATNum;//附加信息-标准-GNSS 定位卫星数 BYTE
        private Up0x0200Ext0x11 up0x0200Ext0x11;//附加信息-标准-超速报警附加信息见表 28
        private Up0x0200Ext0x12 up0x0200Ext0x12;//附加信息-标准-进出区域/路线报警附加信息见表 29
        private Up0x0200Ext0x13 up0x0200Ext0x13;//附加信息-标准-路段行驶时间不足/过长报警附加信息见表 30
        private String extStatus;//附加信息-标准-扩展车辆信号状态位，定义见表 31
        private String ioStatus;//附加信息-标准-IO 状态位，定义见表 32
        private String analogQuantity;//模拟量，bit0-15，AD0;bit16-31，AD1。
        private Up0x0200Ext0x64 up0x0200Ext0x64;//附加信息-企业扩展-高级驾驶赋值系统报警信息
        private Up0x0200Ext0x67 up0x0200Ext0x67;//附加信息-企业扩展-盲区检测系统报警信息见表
        private byte[] up0x0200Ext0xE0;//后续自定义信息长度

        public Up0x0200Ext(ByteBuf bb) {
            this.parseUp0x0200Ext(bb, 0, 0);
        }

        private void parseUp0x0200Ext(ByteBuf bb, int msgId, int msgLen) {
            msgId = bb.readUnsignedByte();
            msgLen = bb.readUnsignedByte();
            //扩展消息处理
            switch (msgId) {
                //附加信息 - 标准
                case JT808Constant.UP_LOCATION_EXT_0x01:
                    this.mileage = bb.readUnsignedInt();
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x02:
                    this.oilMass = bb.readUnsignedShort();
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x03:
                    this.speed = bb.readUnsignedShort();
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x04:
                    this.alarmId = bb.readUnsignedShort();
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x11:
                    this.up0x0200Ext0x11 = new Up0x0200Ext0x11(bb.readBytes(msgLen));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x12:
                    this.up0x0200Ext0x12 = new Up0x0200Ext0x12(bb.readBytes(msgLen));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x13:
                    this.up0x0200Ext0x13 = new Up0x0200Ext0x13(bb.readBytes(msgLen));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x25:
                    this.extStatus = ByteArrayUtil.bytes2bitStr(ByteArrayUtil.readBytes(bb, 4));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x2A:
                    this.ioStatus = ByteArrayUtil.bytes2bitStr(ByteArrayUtil.readBytes(bb, 2));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x2B:
                    this.analogQuantity = ByteArrayUtil.bytes2bitStr(ByteArrayUtil.readBytes(bb, 4));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x30:
                    this.setWirelessSignal((short) bb.readByte());
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x31:
                    this.setGnssSATNum((short) bb.readByte());
                    break;
                //附加信息 - 企业扩展
                case JT808Constant.UP_LOCATION_EXT_0x64:
                    this.up0x0200Ext0x64 = new Up0x0200Ext0x64(bb.readBytes(msgLen));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0x67:
                    this.up0x0200Ext0x67 = new Up0x0200Ext0x67(bb.readBytes(msgLen));
                    break;
                case JT808Constant.UP_LOCATION_EXT_0xE0:
                    this.up0x0200Ext0xE0 = ByteArrayUtil.readBytes(bb, msgLen);
                    break;
                default:
                    bb.readBytes(msgLen);
            }
            if (bb.isReadable()) {
                this.parseUp0x0200Ext(bb, msgId, msgLen);
            }
        }

    }


    /**
     * @author: choice
     * 附加信息-标准-超速报警附加信息见表 28
     */
    @Data
    public static class Up0x0200Ext0x11 {
        public Up0x0200Ext0x11(ByteBuf bb) {
            this.type = bb.readByte();
            if (type != 0) {
                this.roadId = bb.readUnsignedInt();
            }
        }

        private short type;//位置类型 0:无特定位置; 1:圆形区域; 2:矩形区域; 3:多边形区域; 4:路段
        private Long roadId;//区域或路段ID，若位置类型为 0，无该字段
    }

    /**
     * @author: choice
     * 附加信息-标准-进出区域/路线报警附加信息见表 29
     */
    @Data
    public static class Up0x0200Ext0x12 {
        public Up0x0200Ext0x12(ByteBuf bb) {
            this.type = bb.readUnsignedByte();
            this.roadId = bb.readUnsignedInt();
            this.direction = bb.readUnsignedByte();
        }

        private short type;//位置类型 0:无特定位置; 1:圆形区域; 2:矩形区域; 3:多边形区域; 4:路段
        private long roadId;//区域或路段ID，若位置类型为 0，无该字段
        private short direction;//方向 0:进; 1:出
    }

    /**
     * @author: choice
     * 附加信息-标准-路段行驶时间不足/过长报警附加信息见表 30
     */
    @Data
    public static class Up0x0200Ext0x13 {
        public Up0x0200Ext0x13(ByteBuf bb) {
            this.roadId = bb.readUnsignedInt();
            this.tripTime = bb.readUnsignedShort();
            this.result = bb.readUnsignedByte();
        }

        private long roadId;//路段ID DWORD
        private int tripTime;//路段行驶时间 WORD 单位为秒(s)
        private short result;//结果 0:不足;1:过长
    }

    /**
     * @author: choice
     * 附加信息-标准-扩展车辆信号状态位，定义见表 31
     */
    @Data
    public static class Up0x0200Ext0x25 {
        public Up0x0200Ext0x25(ByteBuf bb) {
            this.roadId = bb.readUnsignedInt();
            this.tripTime = bb.readUnsignedShort();
            this.result = bb.readUnsignedByte();
        }

        private long roadId;//路段ID DWORD
        private int tripTime;//路段行驶时间 WORD 单位为秒(s)
        private short result;//结果 0:不足;1:过长
    }

    /**
     * @author: choice
     * 附加信息-标准-IO 状态位，定义见表 32
     */
    @Data
    public static class Up0x0200Ext0x2A {
        public Up0x0200Ext0x2A(ByteBuf bb) {
            this.roadId = bb.readUnsignedInt();
            this.tripTime = bb.readUnsignedShort();
            this.result = bb.readUnsignedByte();
        }

        private long roadId;//路段ID DWORD
        private int tripTime;//路段行驶时间 WORD 单位为秒(s)
        private short result;//结果 0:不足;1:过长
    }


    /**
     * @author: choice
     * 附加信息-企业扩展-高级驾驶赋值系统报警信息
     */
    @Data
    public static class Up0x0200Ext0x64 {

        public Up0x0200Ext0x64(ByteBuf bb) {
            this.alarmId = bb.readInt();
            this.tagStatus = bb.readUnsignedByte();
            this.type = bb.readUnsignedByte();
            this.level = bb.readUnsignedByte();
            this.frontCarSpeed = bb.readUnsignedByte();
            this.frontCarDistance = bb.readUnsignedByte();
            this.offtrackType = bb.readUnsignedByte();
            this.rodeTagType = bb.readUnsignedByte();
            this.rodeTagData = bb.readUnsignedByte();
            this.speed = bb.readUnsignedByte();
            this.elevation = bb.readShort();
            this.latitude = JT808Util.byteBufToSixFloat(bb.readUnsignedInt());
            this.longitude = JT808Util.byteBufToSixFloat(bb.readUnsignedInt());
            this.time = BCD.toBcdTimeString(ByteArrayUtil.readBytes(bb, 6));
            this.status = ByteArrayUtil.bytes2bitStr(ByteArrayUtil.readBytes(bb, 2));
            this.terminalId = JT808Util.delZeorStr(ByteArrayUtil.readStr(bb, 7));
            this.alarmTime = BCD.toBcdTimeString(ByteArrayUtil.readBytes(bb, 6));
            this.alarmNum = bb.readUnsignedByte();
            this.accessoryNum = bb.readUnsignedByte();
            this.reserved = bb.readByte();
        }

        private int alarmId;//报警id 4字节
        private short tagStatus;//标志状态 1字节
        private short type;//报警类型 1字节
        private short level;//报警级别  1字节
        private short frontCarSpeed;//前车车速 1字节
        private short frontCarDistance;//前车/行人距离 1字节
        private short offtrackType;//偏离类型 1字节
        private short rodeTagType;//道路标志 识别类型 1字节
        private short rodeTagData;//道路标志 识别数据 1字节
        private short speed;//车速 1字节
        private short elevation;//海拔高度 2字节
        private float latitude;//纬度 4字节
        private float longitude;//经度 4字节
        private String time; //时间 6字节BCD
        private String status;//车辆状态 2字节
        //报警标识
        private String terminalId;//终端id 7字节
        private String alarmTime;//时间 6字节BCD
        private short alarmNum;//报警序号 1字节
        private short accessoryNum;//附件数量 1字节
        private byte reserved;//预留 1字节

    }


    /**
     * @author: choice
     * 附加信息-企业扩展-盲区检测系统报警信息见表
     */
    @Data
    public static class Up0x0200Ext0x67 {
        public Up0x0200Ext0x67(ByteBuf bb) {
            this.alarmId = bb.readInt();
            this.tagStatus = bb.readUnsignedByte();
            this.type = bb.readUnsignedByte();
            this.speed = bb.readUnsignedByte();
            this.elevation = bb.readUnsignedShort();
            this.latitude = JT808Util.byteBufToSixFloat(bb.readUnsignedInt());
            this.longitude = JT808Util.byteBufToSixFloat(bb.readUnsignedInt());
            this.time = BCD.toBcdTimeString(ByteArrayUtil.readBytes(bb, 6));
            this.status = ByteArrayUtil.bytes2bitStr(ByteArrayUtil.readBytes(bb, 2));
            this.terminalId = JT808Util.delZeorStr(ByteArrayUtil.readStr(bb, 7));
            this.alarmTime = BCD.toBcdTimeString(ByteArrayUtil.readBytes(bb, 6));
            this.alarmNum = bb.readUnsignedByte();
            this.accessoryNum = bb.readUnsignedByte();
            this.reserved = bb.readByte();
        }

        private long alarmId;//报警id 4字节
        private short tagStatus;//标志状态 1字节
        private short type;//报警类型 1字节
        private short speed;//车速 1字节
        private int elevation;//海拔高度 2字节
        private float latitude;//纬度 4字节
        private float longitude;//经度 4字节
        private String time; //时间 6字节BCD
        private String status;//车辆状态 2字节
        //报警标识
        private String terminalId;//终端id 7字节
        private String alarmTime;//时间 6字节BCD
        private short alarmNum;//报警序号 1字节
        private short accessoryNum;//附件数量 1字节
        private byte reserved;//预留 1字节
    }
}
