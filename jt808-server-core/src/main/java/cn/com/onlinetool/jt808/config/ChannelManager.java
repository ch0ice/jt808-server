package cn.com.onlinetool.jt808.config;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
@Slf4j
@Component
public class ChannelManager {


    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final Map<String, ChannelId> channelIdMap = new ConcurrentHashMap<>();

    public void add(String terminalPhone, Channel channel) {
        boolean added = channelGroup.add(channel);
        if (added) {
            channelIdMap.put(terminalPhone, channel.id());
        }
    }

    public Channel get(String terminalPhone) {
        ChannelId id = channelIdMap.get(terminalPhone);
        if (id == null) {
            return null;
        }
        return channelGroup.find(id);
    }

    public void remove(Channel channel) {
        channelGroup.remove(channel);
    }
}
