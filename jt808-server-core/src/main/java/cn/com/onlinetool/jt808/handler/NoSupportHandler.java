package cn.com.onlinetool.jt808.handler;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.up.res.Up0x8001;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 不支持的消息
 */
@Slf4j
@Component
public class NoSupportHandler implements PackHandler {

    @Override
    public BasePacket handle(BasePacket msg, int flowId) {
        return Up0x8001.unsupported(msg, flowId);
    }
}
