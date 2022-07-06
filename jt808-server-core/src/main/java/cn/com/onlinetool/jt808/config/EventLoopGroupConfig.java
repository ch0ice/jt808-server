package cn.com.onlinetool.jt808.config;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 杨顾
 * @Description: 事件循环组相关的配置
 * @Version: 1.0
 */
@Configuration
public class EventLoopGroupConfig {

    @Autowired
    private NettyProperties nettyProperties;

    /**
     * 负责TCP连接建立操作 绝对不能阻塞
     *
     * @return
     */
    @Bean(name = "bossGroup")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(1);
    }

    /**
     * 负责Socket读写操作 绝对不能阻塞
     *
     * @return
     */
    @Bean(name = "workerGroup")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    /**
     * Handler中出现IO操作(如数据库操作，网络操作)使用这个
     *
     * @return
     */
    @Bean(name = "businessGroup")
    public EventExecutorGroup businessGroup() {
        return new DefaultEventExecutorGroup(nettyProperties.getBusinessThreads());
    }

}
