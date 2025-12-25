package com.aquila.mq.normal;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Hashtable;

/**
 * <li>
 *     <ul>Queue manager: QM1</ul>
 *     <ul>Queue: DEV.QUEUE.1</ul>
 *     <ul>Channel: DEV.APP.SVRCONN</ul>
 *     <ul>Listener: SYSTEM.LISTENER.TCP.1 on port 1414</ul>
 * </li>
 * user "app", who is a member of the group "mqclient" is permitted to use the channel DEV.APP.SVRCONN
 */
@Slf4j
public class NormalMQTest {

    @Test
    void testSend() throws MQException, IOException, InterruptedException {
        log.info("test");
        final int openOptions = CMQC.MQOO_BIND_AS_Q_DEF | CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_OUTPUT;
        final MQQueueManager mqQueueManager = createMQQueueManager();
        final MQGetMessageOptions gmo = new MQGetMessageOptions();
        gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_SYNCPOINT;
        gmo.waitInterval = 10;
        final String queueName = "DEV.QUEUE.1"; // "COM.AQUILA.TST.001";
        final MQQueue qag = mqQueueManager.accessQueue(queueName, openOptions);
//        qle = mqQueueManager.accessQueue(queueName, CMQC.MQOO_OUTPUT);
        MQMessage rcvMessage = new MQMessage();
        rcvMessage.writeString("Test petit lombric");
        qag.put(rcvMessage);
        qag.get(rcvMessage, gmo);
        int length = rcvMessage.getDataLength();
        log.info("Received message with length: {} -> {}", length, rcvMessage.readStringOfByteLength(length));
    }

    /**
     * Create MQQueueManager
     *
     * @return the MQQueueManager
     * @throws MQException in case of MQ error
     */
    private MQQueueManager createMQQueueManager() throws MQException {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put(CMQC.HOST_NAME_PROPERTY, "localhost"); // "localhost");
        properties.put(CMQC.PORT_PROPERTY, 1414);
        properties.put(CMQC.CHANNEL_PROPERTY, "DEV.ADMIN.SVRCONN");
        properties.put(CMQC.USER_ID_PROPERTY, "app"); // "admin");
        properties.put(CMQC.PASSWORD_PROPERTY, "passw0rd");
        return new MQQueueManager("QM1", properties);
    }

}
