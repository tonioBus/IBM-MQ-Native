package com.aquila.mq.normal;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import lombok.extern.slf4j.Slf4j;

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
public class NormalMQ {

    public static int LOOP = 1000;

    public static void main(String args[]) throws MQException, IOException, InterruptedException {
        log.info("test");
        final int openOptions = CMQC.MQOO_BIND_AS_Q_DEF | CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_OUTPUT;
        MQQueueManager mqQueueManager = createMQQueueManager();
        mqQueueManager.close();
        long startOpen = System.currentTimeMillis();
        mqQueueManager = createMQQueueManager();
        final MQGetMessageOptions gmo = new MQGetMessageOptions();
        gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_SYNCPOINT;
        gmo.waitInterval = 10;
        final String queueName = "DEV.QUEUE.1";
        final MQQueue qag = mqQueueManager.accessQueue(queueName, openOptions);
        log.info("Put {} message", LOOP);
        long startPut = System.currentTimeMillis();
        for (int i = 0; i < LOOP; i++) {
            MQMessage rcvMessage = new MQMessage();
            rcvMessage.writeString("Message Content from NormalMQ");
            qag.put(rcvMessage);
        }
        long startGet = System.currentTimeMillis();
        log.info("Get {} message", LOOP);
        for (int i = 0; i < LOOP; i++) {
            MQMessage rcvMessage = new MQMessage();
            qag.get(rcvMessage, gmo);
        }
        long endGet = System.currentTimeMillis();
        log.info("closing queue");
        long endClose = System.currentTimeMillis();
        mqQueueManager.close();
        log.info("startOpen         : {}", startOpen);
        log.info("startPut          : {}", startPut);
        log.info("startGet          : {}", startGet);
        log.info("endGet            : {}", endGet);
        log.info("endClose          : {}", endClose);
        log.info("-------------------------------------------");
        log.info("Delay Open        : {}", startPut - startOpen);
        log.info("Delay Put ({})    : {}", LOOP, startGet - startPut);
        log.info("Delay Get ({})    : {}", LOOP, endGet - startGet);
        log.info("Delay Close       : {}", endClose - endGet);
        log.info("Delay Total       : {}", endClose - startOpen);
    }

    /**
     * Create MQQueueManager
     *
     * @return the MQQueueManager
     * @throws MQException in case of MQ error
     */
    private static MQQueueManager createMQQueueManager() throws MQException {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put(CMQC.HOST_NAME_PROPERTY, "localhost"); // "localhost");
        properties.put(CMQC.PORT_PROPERTY, 1414);
        properties.put(CMQC.CHANNEL_PROPERTY, "DEV.ADMIN.SVRCONN");
        properties.put(CMQC.USER_ID_PROPERTY, "app"); // "admin");
        properties.put(CMQC.PASSWORD_PROPERTY, "passw0rd");
        log.info("========================================");
        log.info("Connexion au Queue Manager via TCP/IP");
        log.info("  Hostname    : {}", properties.get(CMQC.HOST_NAME_PROPERTY));
        log.info("  Port        : {}", properties.get(CMQC.PORT_PROPERTY));
        log.info("  Channel     : {}", properties.get(CMQC.CHANNEL_PROPERTY));
        log.info("  User        : {}", properties.get(CMQC.USER_ID_PROPERTY));
        log.info("  Password    : {}", properties.get(CMQC.PASSWORD_PROPERTY));
//        log.info("  Canal: {}", channelName);
//        log.info("  Connexion: {}", connectionName);
        log.info("========================================");
        return new MQQueueManager("QM1", properties);
    }

}
