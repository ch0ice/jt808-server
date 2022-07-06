package cn.com.onlinetool.jt808.handler.up;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.up.res.Up0x8001;
import cn.com.onlinetool.jt808.config.JT808Pack;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.handler.PackHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端心跳
 */

@Slf4j
@JT808Pack(msgId = JT808Constant.UP_HEARTBEAT)
public class Up0x0002Handler implements PackHandler {

    @Override
    public BasePacket handle(BasePacket msg, int flowId) {
        log.debug("[终端心跳包] 终端手机号: {}", msg.getHeader().getTerminalPhone());
        return Up0x8001.success(msg, flowId);
    }

}
