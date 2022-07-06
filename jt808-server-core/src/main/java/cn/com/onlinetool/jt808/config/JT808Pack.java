package cn.com.onlinetool.jt808.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author 杨顾
 */
@Component
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JT808Pack {

    /**
     * 16进制的消息id
     *
     * @return
     */
    int msgId();

}
