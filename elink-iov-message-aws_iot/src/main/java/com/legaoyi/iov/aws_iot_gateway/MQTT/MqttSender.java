package com.legaoyi.iov.aws_iot_gateway.MQTT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

import java.util.Map;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import com.legaoyi.iov.aws_iot_gateway.util.CommandLineUtils;

public class MqttSender {
	
	private static final Logger logger = LoggerFactory.getLogger(MqttSender.class);
	
	// When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;
	static MqttClientConnection connection;
	
	static CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("PubSub");

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("BasicPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }
	
	public static void initialisation(){
		/**
         * cmdData is the arguments/input from the command line placed into a single struct for
         * use in this sample. This handles all of the command line parsing, validating, etc.
         * See the Utils/CommandLineUtils for more information.
         */
        

        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    System.out.println("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                System.out.println("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        try {

            /**
             * Create the MQTT connection from the builder
             */
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(cmdData.input_cert, cmdData.input_key);
            if (cmdData.input_ca != "") {
                builder.withCertificateAuthorityFromPath(null, cmdData.input_ca);
            }
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(cmdData.input_clientId)
                .withEndpoint(cmdData.input_endpoint)
                .withPort(cmdData.input_port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);
            if (cmdData.input_proxyHost != "" && cmdData.input_proxyPort > 0) {
                HttpProxyOptions proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(cmdData.input_proxyHost);
                proxyOptions.setPort(cmdData.input_proxyPort);
                builder.withHttpProxyOptions(proxyOptions);
            }
            connection = builder.build();
            builder.close();

            // Connect the MQTT client
            CompletableFuture<Boolean> connected = connection.connect();
            try {
                boolean sessionPresent = connected.get();
                System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }
		} catch (CrtRuntimeException ex) {
            onApplicationFailure(ex);
        }

	}
    
	

    public static void MqttSend(Map<?,?> message) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = objectMapper.writeValueAsString(message);
			CompletableFuture<Integer> published = connection.publish(new MqttMessage(cmdData.input_topic, jsonString.getBytes(), QualityOfService.AT_LEAST_ONCE, false));
			logger.info("Sent {}",jsonString);
			published.get(); // This can throw InterruptedException and ExecutionException
		} catch (Exception e) {
			e.printStackTrace();
		}


        
    }
	
	public static void MqttClose(){
		try {
			CompletableFuture<Void> disconnected = connection.disconnect();
			disconnected.get(); // Handle InterruptedException and ExecutionException

			// Close the connection now that we are completely done with it.
			connection.close();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
			e.printStackTrace(); // Handle InterruptedException
		} catch (ExecutionException e) {
			e.printStackTrace(); // Handle ExecutionException
		}
		
	}
}














/*
CrtResource.waitForNoResources();
        System.out.println("Complete!");
            


// Subscribe to the topic
            CountDownLatch countDownLatch = new CountDownLatch(cmdData.input_count);
            CompletableFuture<Integer> subscribed = connection.subscribe(cmdData.input_topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                System.out.println("MESSAGE: " + payload);
                countDownLatch.countDown();
            });
            subscribed.get();*/