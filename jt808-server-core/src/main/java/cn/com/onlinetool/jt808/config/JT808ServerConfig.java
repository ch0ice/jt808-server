package cn.com.onlinetool.jt808.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Slf4j
@Component
@EnableConfigurationProperties(NettyProperties.class)
public class JT808ServerConfig {

    @Autowired
    private NettyProperties nettyProperties;

    @Autowired
    @Qualifier("bossGroup")
    private NioEventLoopGroup bossGroup;

    @Autowired
    @Qualifier("workerGroup")
    private NioEventLoopGroup workerGroup;

    @Autowired
    @Qualifier("businessGroup")
    private EventExecutorGroup businessGroup;

    @Autowired
    private JT808ChannelInitializer jt808ChannelInitializer;

    /**
     * 启动Server
     *
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childHandler(jt808ChannelInitializer);
//        serverBootstrap.option(ChannelOption.SO_BACKLOG, nettyProperties.getBackLog());
//        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, nettyProperties.getTcpNoDelay());
//        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, nettyProperties.getKeepalive());

        //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
        // 标识当服务器请求处理线程全满时，如果 syns queue 和 accept queue 二者长度相加 > backlog，TCP内核会拒绝新的连接
        // 临时存放已完成三次握手请求的队列最大长度，默认50
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // 禁用延时算法nagle，这个算法受TCP延迟确认影响, 会导致相继两次向连接发送请求包,读数据时会有一个最多达500毫秒的延时.
        serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
        //在空闲套接字上发送探测，以验证套接字是否仍处于活动状态。
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        //内存泄漏检测 开发推荐PARANOID 线上SIMPLE
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
        log.info("系统初始化完成, 即将启动");
        //注册一个回调函数
        serverBootstrap.bind(nettyProperties.getServerPort()).sync().addListener(future -> {
            if (future.isSuccess()) {
                log.info("TCP服务启动完毕, port={}", nettyProperties.getServerPort());
            }
        });
    }

    /**
     * 销毁资源
     */
    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        businessGroup.shutdownGracefully().syncUninterruptibly();
        log.info("服务器关闭成功");
    }


}
