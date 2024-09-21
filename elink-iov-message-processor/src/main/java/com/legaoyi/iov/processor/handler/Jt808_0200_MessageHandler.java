package com.legaoyi.iov.processor.handler;

import java.util.Map;
import java.util.HashMap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.legaoyi.iov.processor.message.ExchangeMessage;
import com.legaoyi.iov.processor.util.Constants;

/**
 * @author <a href="mailto:shengbo.gao@gmail.com;gaoshengbo@legaoyi.com">gaoshengbo</a>
 * @version 1.0.0
 * @since 2019-08-18
 */
@Component(Constants.ELINK_MESSAGE_STORER_BEAN_PREFIX + "0200" + Constants.ELINK_MESSAGE_STORER_MESSAGE_HANDLER_BEAN_SUFFIX)
public class Jt808_0200_MessageHandler extends MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(Jt808_0100_MessageHandler.class);

	@Autowired
    public Jt808_0200_MessageHandler(@Qualifier("datastreamMessageSendHandler") MessageHandler handler) {
        setSuccessor(handler);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void handle(ExchangeMessage message) throws Exception {
        Map<?, ?> map = (Map<?, ?>) message.getMessage();
		
		Map<?, ?> messageHeader = (Map<?, ?>) map.get(Constants.MAP_KEY_MESSAGE_HEADER);
		String simCode = (String) messageHeader.get(Constants.MAP_KEY_SIM_CODE);
		
        Map<String, Object> messageBody = (Map<String, Object>) map.get(Constants.MAP_KEY_MESSAGE_MESSAGE_BODY);
        logger.info("******位置信息，message={}", message);

        if (getSuccessor() != null) {
            getSuccessor().handle(message);
        }
		
		
		// 响应终端
        Map<String, Object> respMessageHeader = new HashMap<String, Object>();
        respMessageHeader.put(Constants.MAP_KEY_SIM_CODE, simCode);
        Map<String, Object> downMessage = new HashMap<String, Object>();
        downMessage.put(Constants.MAP_KEY_MESSAGE_HEADER, respMessageHeader);
        downMessage.put(Constants.MAP_KEY_MESSAGE_MESSAGE_BODY, messageBody);
        // 注入下发消息处理链
        ExchangeMessage exchangeMessage = new ExchangeMessage(ExchangeMessage.MESSAGEID_PLATFORM_DOWN_MESSAGE, downMessage, null, message.getGatewayId());
        exchangeMessage.setExtAttribute(message.getExtAttribute());
        getSuccessor().handle(exchangeMessage);
    }

}
