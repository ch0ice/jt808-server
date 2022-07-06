package cn.com.onlinetool.jt808.handler.down;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.down.res.Down0x0107;
import cn.com.onlinetool.jt808.bean.up.res.Up0x8001;
import cn.com.onlinetool.jt808.config.JT808Pack;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.handler.PackHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 查询终端属性应答
 */

@Slf4j
@JT808Pack(msgId = JT808Constant.DOWN_PROPERTY_FIND_RES)
public class Down0x0107Handler implements PackHandler {

    @Override
    public BasePacket handle(BasePacket msg, int flowId) {
        Down0x0107 down0x0107 = new Down0x0107(msg.getMsgBody());
        log.info(down0x0107.toString());
        return Up0x8001.success(msg, flowId);
    }

}