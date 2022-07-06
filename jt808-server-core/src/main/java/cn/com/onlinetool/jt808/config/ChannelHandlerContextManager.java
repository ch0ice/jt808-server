package cn.com.onlinetool.jt808.config;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用ChannelGroup管理Channel, 维护terminalPhone -> ChannelId -> Channel 一对一映射关系
 *
 * @author: 杨顾
 * @Description: 维护terminalPhone和Channel的关系
 * @Version: 1.0
 */
@Slf4j
@Component
public class ChannelHandlerContextManager {

    private final Map<String, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<>();

    private final Map<String, ChannelId> channelIdMap = new ConcurrentHashMap<>();

    public void add(String terminalPhone, ChannelHandlerContext ctx) {
        ChannelId channelId = ctx.channel().id();
        channelIdMap.put(terminalPhone, channelId);
        channelHandlerContextMap.put(channelId.asShortText(), ctx);
    }

    public ChannelHandlerContext get(String terminalPhone) {
        if(terminalPhone == null){
            return null;
        }
        ChannelId channelId = channelIdMap.get(terminalPhone);
        if (channelId == null) {
            return null;
        }
        ChannelHandlerContext ctx = channelHandlerContextMap.get(channelId.asShortText());
        if (ctx == null || !ctx.channel().isActive()) {
            return null;
        }
        return ctx;
    }

    public void remove(ChannelHandlerContext ctx) {
        channelHandlerContextMap.remove(ctx.channel().id().asShortText());
    }

}
