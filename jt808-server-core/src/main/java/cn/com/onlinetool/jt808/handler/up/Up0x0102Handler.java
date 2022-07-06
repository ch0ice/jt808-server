package cn.com.onlinetool.jt808.handler.up;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.up.req.Up0x0102;
import cn.com.onlinetool.jt808.bean.up.res.Up0x8001;
import cn.com.onlinetool.jt808.config.JT808Pack;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.handler.PackHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端鉴权
 */

@Slf4j
@JT808Pack(msgId = JT808Constant.UP_AUTH)
public class Up0x0102Handler implements PackHandler {

    @Override
    public BasePacket handle(BasePacket msg, int flowId) {
        Up0x0102 up0x0102 = new Up0x0102(msg.getMsgBody());
        log.info(up0x0102.toString());
        return Up0x8001.success(msg, flowId);
    }
}