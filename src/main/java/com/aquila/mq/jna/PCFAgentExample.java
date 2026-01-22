package com.aquila.mq.jna;

import com.aquila.mq.jna.lib.*;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.ibm.mq.constants.CMQC.*;
import static com.ibm.mq.constants.CMQXC.*;

/**
 * Example demonstrating how to use PCFAgent to retrieve all queues from a Queue Manager
 */
@Slf4j
public class PCFAgentExample {

    public static void main(String[] args) {
        log.info("Starting PCF Agent Example");

        // Connection configuration
        String queueManagerName = "QM1";
        String channelName = "DEV.APP.SVRCONN";
        String connectionName = "192.168.1.73(1414)";

        // Prepare the Queue Manager name
        byte[] qmgrName = new byte[IBMMQJNA.MQ_Q_MGR_NAME_LENGTH];
        Arrays.fill(qmgrName, (byte) ' ');
        byte[] qmgrBytes = queueManagerName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(qmgrBytes, 0, qmgrName, 0, Math.min(qmgrBytes.length, qmgrName.length));

        // Create and configure MQCD
        MQCD mqcd = new MQCD();
        mqcd.Version = MQCD_VERSION_10;
        mqcd.setChannelName(channelName);
        mqcd.setConnectionName(connectionName);
        mqcd.setUser("admin");
        mqcd.setPassword("passw0rd");
        mqcd.ChannelType = MQCHT_CLNTCONN;
        mqcd.TransportType = MQXPT_TCP;

        // Create and configure MQCNO
        MQCNO mqcno = new MQCNO();
        mqcno.setClientConnection(mqcd);

        // Output variables
        IntByReference hConn = new IntByReference(MQHC_UNUSABLE_HCONN);
        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();

        log.info("========================================");
        log.info("Connecting to Queue Manager");
        log.info("  Queue Manager: {}", queueManagerName);
        log.info("  Channel: {}", channelName);
        log.info("  Connection: {}", connectionName);
        log.info("========================================");

        try {
            // Connect to Queue Manager
            IBMMQJNA.INSTANCE.MQCONNX(new String(qmgrName), mqcno, hConn, compCode, reason);

            if (compCode.getValue() == MQCC_FAILED) {
                log.error("MQCONNX failed with reason: {}", reason.getValue());
                System.exit(1);
            }

            log.info("Connected successfully! Handle: {}", hConn.getValue());

            // Create PCF Agent and inquire queues
            try (PCFAgent pcfAgent = new PCFAgent(hConn.getValue())) {
                pcfAgent.connect();

                // Get all queues
                log.info("");
                log.info("========================================");
                log.info("Retrieving all queues...");
                log.info("========================================");

                List<QueueInfo> allQueues = pcfAgent.inquireQueues("*");
                log.info("Found {} total queues", allQueues.size());
                log.info("");

                // Display queues grouped by type
                displayQueuesByType(allQueues, PCFConstants.MQQT_LOCAL, "Local Queues");
                displayQueuesByType(allQueues, PCFConstants.MQQT_ALIAS, "Alias Queues");
                displayQueuesByType(allQueues, PCFConstants.MQQT_REMOTE, "Remote Queues");
                displayQueuesByType(allQueues, PCFConstants.MQQT_MODEL, "Model Queues");

                // Example: Get only local queues matching a pattern
                log.info("");
                log.info("========================================");
                log.info("Retrieving DEV.* local queues...");
                log.info("========================================");

                List<QueueInfo> devQueues = pcfAgent.inquireQueues("DEV.*", PCFConstants.MQQT_LOCAL);
                for (QueueInfo queue : devQueues) {
                    log.info("  {}", queue);
                }

            } catch (PCFAgent.PCFException e) {
                log.error("PCF error: {}", e.getMessage());
            }

            // Disconnect
            log.info("");
            log.info("Disconnecting...");
            IBMMQJNA.INSTANCE.MQDISC(hConn, compCode, reason);

            if (compCode.getValue() == MQCC_OK) {
                log.info("Disconnected successfully!");
            } else {
                log.warn("Warning during disconnection, reason: {}", reason.getValue());
            }

        } catch (UnsatisfiedLinkError e) {
            log.error("IBM MQ library not found: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void displayQueuesByType(List<QueueInfo> queues, int type, String title) {
        List<QueueInfo> filtered = queues.stream()
                .filter(q -> q.getType() == type)
                .toList();

        if (!filtered.isEmpty()) {
            log.info("{} ({}):", title, filtered.size());
            for (QueueInfo queue : filtered) {
                log.info("  {}", queue);
            }
            log.info("");
        }
    }
}
