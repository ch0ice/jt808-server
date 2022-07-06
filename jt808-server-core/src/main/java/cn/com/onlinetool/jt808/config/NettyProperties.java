package cn.com.onlinetool.jt808.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    /**
     * 服务端端口号
     */
    private Integer serverPort;

    /**
     * 读超时时间(单位分钟)
     */
    private Integer readTimeout;

    /**
     * 缓冲队列大小
     */
    private Integer backLog;

    /**
     * 提高实时性
     */
    private Boolean tcpNoDelay;

    /**
     * 保持长连接
     */
    private Boolean keepalive;

    /**
     * 业务线程池数量
     */
    private Integer businessThreads;

}
