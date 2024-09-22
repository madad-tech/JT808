package com.legaoyi.iov.aws_iot_gateway.handler;

import java.util.Map;
import java.util.HashMap;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.legaoyi.iov.aws_iot_gateway.message.ExchangeMessage;
import com.legaoyi.iov.aws_iot_gateway.util.Constants;
import com.legaoyi.iov.aws_iot_gateway.MQTT.MqttSender;

/**
 * @author <a href="mailto:shengbo.gao@gmail.com;gaoshengbo@legaoyi.com">gaoshengbo</a>
 * @version 1.0.0
 * @since 2019-08-18
 */
@Component("GPS")
public class Jt808_0200_MessageHandler extends MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(Jt808_0200_MessageHandler.class);



    @SuppressWarnings("unchecked")
    @Override
    public void handle(ExchangeMessage message) throws Exception {
        Map<?, ?> map = (Map<?, ?>) message.getMessage();
		
		Map<?, ?> messageHeader = (Map<?, ?>) map.get(Constants.MAP_KEY_MESSAGE_HEADER);
		String simCode = (String) messageHeader.get(Constants.MAP_KEY_SIM_CODE);
		
        Map<String, Object> messageBody = (Map<String, Object>) map.get(Constants.MAP_KEY_MESSAGE_MESSAGE_BODY);
        logger.info("****** message={}", message);
		
		MqttSender.MqttSend(map);

		if (getSuccessor() != null) {
            getSuccessor().handle(message);
        }
    }
	
	

}
