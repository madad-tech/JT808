package com.legaoyi.iov.aws_iot_gateway.handler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.legaoyi.iov.aws_iot_gateway.message.ExchangeMessage;
import com.legaoyi.iov.aws_iot_gateway.util.Constants;
import com.legaoyi.iov.aws_iot_gateway.util.ServerRuntimeContext;

/**
 * @author <a href="mailto:shengbo.gao@gmail.com;gaoshengbo@legaoyi.com">gaoshengbo</a>
 * @version 1.0.0
 * @since 2019-08-18
 */
@Component("deviceUpstreamMessageHandler")
public class DeviceUpstreamMessageHandler extends MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeviceUpstreamMessageHandler.class);

    @Value("${batchSave.threadPool.size}")
    private int threadPoolSize = 5;

    private ExecutorService fixedThreadPool = null;

    @PostConstruct
    public void init() {
        fixedThreadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    public void handle(ExchangeMessage message) throws Exception {
        
            fixedThreadPool.execute(new Runnable() {

                public void run() {
                    try {
                        
						logger.info("Recieved : {}", message);
                        try {
                            MessageHandler messageHandler = ServerRuntimeContext.getBean("GPS", MessageHandler.class);
                            messageHandler.handle(message);
                        } catch (NoSuchBeanDefinitionException e) {

                        } catch (Exception e) {
                            logger.error(" handler message error,message={}", message, e);
                        }
                    } catch (Exception e) {
                        logger.error(" handler message error,message={}", message, e);
                    }
                }
            });
        
    }
}
