package cn.com.onlinetool.jt808.handler;

import cn.com.onlinetool.jt808.bean.BasePacket;

public interface PackHandler {

    /**
     * 处理终端传入的消息, 然后进行返回
     *
     * @param msg    终端传入的消息
     * @param flowId 消息流水号
     * @return 需要发送给终端的消息(需要构建好消息头header 、 消息体msgBody)
     */
    BasePacket handle(BasePacket msg, int flowId);

}