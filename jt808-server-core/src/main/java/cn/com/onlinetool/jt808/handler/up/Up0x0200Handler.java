package cn.com.onlinetool.jt808.handler.up;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.up.req.Up0x0200;
import cn.com.onlinetool.jt808.bean.up.res.Up0x8001;
import cn.com.onlinetool.jt808.config.JT808Pack;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.handler.PackHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 位置信息汇报
 */

@Slf4j
@JT808Pack(msgId = JT808Constant.UP_LOCATION)
public class Up0x0200Handler implements PackHandler {

    @Override
    public BasePacket handle(BasePacket msg, int flowId) {
        Up0x0200 up0x0200 = new Up0x0200(msg.getMsgBody());
        log.info(up0x0200.toString());
        return Up0x8001.success(msg, flowId);
    }

}