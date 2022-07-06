package cn.com.onlinetool.jt808.consts;

import java.nio.charset.Charset;

public interface JT808Constant {
    //默认字符集为GBK
    Charset DEFAULT_CHARSET = Charset.forName("GBK");

    //消息分隔符
    byte PKG_DELIMITER = 0x7e;
    //消息分隔符(首尾标志位7e)
    String HEAD_TAIL_FLAGS = Integer.toHexString(PKG_DELIMITER);
    //最小包头长度(2013年协议为12位, 2019年协议为16位)
    int MIN_PACKET_HEADER_NUMBER = 12;
    //最大数据包的长度, 如果超过需要分包发送
    int PACKAGE_MAX_LENGTH = 1024;


    // 终端消息分类
    int DOWN_COMMON_TERMINAL_RES = 0x0001; //终端通用应答
    int DOWN_LOCATION_FIND_REQ = 0x8201; //位置信息查询参数
    int DOWN_LOCATION_FIND_RES = 0x0201; //位置查询应答
    int DOWN_PARAM_SET_REQ = 0x8103;//终端参数设置
    int DOWN_PARAM_FIND_REQ = 0x8104;//终端参数查询
    int DOWN_PARAM_DEST_FIND_REQ = 0x8106;//查询指定终端参数
    int DOWN_PARAM_DEST_FIND_RES = 0x0104;//查询终端参数应答
    int DOWN_PROPERTY_FIND_REQ = 0x8107;//查询终端属性请求
    int DOWN_PROPERTY_FIND_RES = 0x0107;//查询终端属性应答
    int DOWN_TERMINAL_UPGRADE_REQ = 0x8108;//下发终端升级包
    int DOWN_TERMINAL_UPGRADE_RES = 0x0108;//终端升级结果通知

    int UP_COMMON_PLATFORM_RES = 0x8001;//通用应答
    int UP_REGISTER_RES = 0x8100;//注册应答
    int UP_HEARTBEAT = 0x0002; //心跳
    int UP_REGISTER = 0x0100; //注册
    int UP_LOGOUT = 0x0003;//注销
    int UP_AUTH = 0x0102;//鉴权
    int UP_LOCATION = 0x0200;//位置
    int UP_LOCATION_BATCH = 0x0704;//位置批量上报
    // 附加信息 - 标准附加
    int UP_LOCATION_EXT_0x01 = 0x01;//里程，DWORD，1/10km，对应车上里程表读数
    int UP_LOCATION_EXT_0x02 = 0x02;//油量，WORD，1/10L，对应车上油量表读数
    int UP_LOCATION_EXT_0x03 = 0x03;//行驶记录功能获取的速度，WORD，1/10km/h
    int UP_LOCATION_EXT_0x04 = 0x04;//需要人工确认报警事件的 ID，WORD，从 1 开始计数
    int UP_LOCATION_EXT_0x11 = 0x11;//超速报警附加信息见表 28
    int UP_LOCATION_EXT_0x12 = 0x12;//出区域/路线报警附加信息见表 29
    int UP_LOCATION_EXT_0x13 = 0x13;//路段行驶时间不足/过长报警附加信息见表 30
    int UP_LOCATION_EXT_0x25 = 0x25;//扩展车辆信号状态位，定义见表 31
    int UP_LOCATION_EXT_0x2A = 0x2A;//IO 状态位，定义见表 32
    int UP_LOCATION_EXT_0x2B = 0x2B;//模拟量，bit0-15，AD0;bit16-31，AD1。
    int UP_LOCATION_EXT_0x30 = 0x30;//BYTE，无线通信网络信号强度
    int UP_LOCATION_EXT_0x31 = 0x31;//BYTE，GNSS 定位卫星数
    int UP_LOCATION_EXT_0xE0 = 0xE0;//后续自定义信息长度
    //附加信息 - 企业扩展
    int UP_LOCATION_EXT_0x64 = 0x64;//高级驾驶赋值系统报警信息
    int UP_LOCATION_EXT_0x67 = 0x67;//盲区检测系统报警信息

    int UP_TERMINAL_RSA = 0x0A00;//终端 RSA 公钥消息


    String AUTH_CODE = "Beytai";


}
