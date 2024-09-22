package com.legaoyi.iov.aws_iot_gateway.activemq;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.legaoyi.iov.aws_iot_gateway.handler.GatewayUpstreamMessageHandler;
import com.legaoyi.iov.aws_iot_gateway.handler.MQMessageHandler;
import com.legaoyi.iov.aws_iot_gateway.handler.MessageHandler;
import com.legaoyi.iov.aws_iot_gateway.util.ServerRuntimeContext;

/**
 * @author <a href="mailto:shengbo.gao@gmail.com;gaoshengbo@legaoyi.com">gaoshengbo</a>
 * @version 1.0.0
 * @since 2019-08-18
 */
@Configuration
public class ActiveMqConfig {

    @Autowired
    @Qualifier("serverRuntimeContext")
    private ServerRuntimeContext serverRuntimeContext;
	
	@Value("${spring.activemq.datastream.queue}")
    private String datastreamQueue;

	
	@Bean("datastreamQueue")
    public Queue datastreamQueue() {
        return new ActiveMQQueue(datastreamQueue);
    }

    @Bean("datastreamMessageHandler")
    public MQMessageHandler urgentUpMessageHandler() {
        GatewayUpstreamMessageHandler handler = new GatewayUpstreamMessageHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(ServerRuntimeContext.getBean("deviceStateMessageHandler", MessageHandler.class));
        handlers.add(ServerRuntimeContext.getBean("deviceUpstreamMessageHandler", MessageHandler.class));
        handler.setHandlers(handlers);
        return handler;
    }

}
