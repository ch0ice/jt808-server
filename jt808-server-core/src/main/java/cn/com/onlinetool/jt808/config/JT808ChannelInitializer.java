package cn.com.onlinetool.jt808.config;

import cn.com.onlinetool.jt808.codec.JT808Decoder;
import cn.com.onlinetool.jt808.codec.JT808Encoder;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import cn.com.onlinetool.jt808.handler.BaseHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author choice
 * netty server 责任链配置
 * @date 2018-12-27 13:14
 */
@Component
public class JT808ChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private NettyProperties nettyProperties;

    @Autowired
    @Qualifier("businessGroup")
    private EventExecutorGroup businessGroup;

    @Autowired
    private BaseHandler baseHandler;


    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        //设置心跳检测，配置为15分钟
        pipeline.addLast(new IdleStateHandler(nettyProperties.getReadTimeout(), 0, 0, TimeUnit.MINUTES));
        //去除首尾处的标识位
        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer(new byte[]{JT808Constant.PKG_DELIMITER})));
        //ByteToMessageDecoder内部对粘包和分包进行了处理, 内部方法对成员变量进行了修改。如果是单例, 在多线程环境下可能有线程安全问题
        //因此ByteToMessageDecoder是有状态的, 不能使用单例
        //@Sharable注解标识Handler可以在多线程中共享, 其实就是无状态的
        //ByteToMessageDecoder在构造方法中调用ensureNotSharable()方法确保是其无状态的
        //Jt808Decoder继承自ByteToMessageDecoder, 所以这里必须使用new的方式, 不能使用Spring的单例
        pipeline.addLast(new JT808Decoder());
        pipeline.addLast(new JT808Encoder());
        //这里使用业务线程组
        pipeline.addLast(businessGroup, baseHandler);
//        pipeline.addLast(baseHandler);
    }
}
