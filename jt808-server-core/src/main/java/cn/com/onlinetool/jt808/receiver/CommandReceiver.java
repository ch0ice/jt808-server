package cn.com.onlinetool.jt808.receiver;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.config.ChannelHandlerContextManager;
import cn.com.onlinetool.jt808.handler.BaseHandler;
import com.rabbitmq.client.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送下行指令时要找到客户端连接所在的服务端节点，
 * 采用广播模式，多消费者消费同一rabbit交换机，根据判断连接在哪个服务端节点 来消费消息，避免多节点交互
 */
@RabbitListener(bindings = @QueueBinding(
        value = @Queue,
        exchange = @Exchange(value = "commandDown",type = ExchangeTypes.FANOUT)))
@Component
@Slf4j
public class CommandReceiver {
    @Autowired
    ChannelHandlerContextManager channelHandlerContextManager;
    @Autowired
    BaseHandler baseHandler;

    @RabbitHandler
    public void handler(Channel rabbitChannel, BasePacket msg){
        String terminalPhone = msg.getHeader().getTerminalPhone();
        ChannelHandlerContext ctx = channelHandlerContextManager.get(terminalPhone);
        log.info("收到 rabbit 消息:{}",msg);
        if(ctx == null){
            log.info("放弃rabbit消息");
            return;
        }
        if(!ctx.channel().isActive()){
            log.warn("非活跃连接");
            return;
        }
        BasePacket.Header header = msg.getHeader();
        header.setFlowId(baseHandler.getSerialNumber(ctx.channel()));
        header.setEncryptionType((byte) 1);
        baseHandler.sendMessage(ctx, msg);
    }
}
