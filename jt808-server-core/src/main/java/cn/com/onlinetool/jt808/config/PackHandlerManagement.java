package cn.com.onlinetool.jt808.config;

import cn.com.onlinetool.jt808.handler.NoSupportHandler;
import cn.com.onlinetool.jt808.handler.PackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PackHandlerManagement implements ApplicationContextAware {

    /**
     * 所有实现的包处理器
     */
    private static Map<Integer, PackHandler> PACK_HANDLER_MAP;

    /**
     * 不支持的协议消息
     */
    @Autowired
    private NoSupportHandler noSupportHandler;

    /**
     * 唤醒时 初始化 packHandlerMap
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 仅一次性初始化完成
        if (PACK_HANDLER_MAP == null) {
            PACK_HANDLER_MAP = new ConcurrentHashMap<>();
            Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(JT808Pack.class);
            if (!CollectionUtils.isEmpty(handlers)) {
                handlers.values().forEach(tempHandler -> {
                    boolean result = tempHandler.getClass().isAnnotationPresent(JT808Pack.class);
                    if (result) {
                        JT808Pack annotation = tempHandler.getClass().getAnnotation(JT808Pack.class);
                        PACK_HANDLER_MAP.put(annotation.msgId(), (PackHandler) tempHandler);
                    }
                });
            }
        }
    }

    public PackHandler getPackHandler(int msgId) {
        return PACK_HANDLER_MAP == null ? noSupportHandler : PACK_HANDLER_MAP.get(msgId);
    }

}