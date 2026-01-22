package com.aquila.mq.jna.lib;

import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.ibm.mq.constants.CMQC.*;

/**
 * PCFAgent - Agent for sending PCF commands to IBM MQ
 * Uses JNA to communicate with the MQ native library
 */
@Slf4j
public class PCFAgent implements AutoCloseable {

    private final int connectionHandle;
    private int adminQueueHandle = -1;
    private int replyQueueHandle = -1;
    private String replyQueueName;

    private static final int DEFAULT_WAIT_INTERVAL = 30000; // 30 seconds
    private static final int BUFFER_SIZE = 65536;

    /**
     * Create a PCF agent for the given connection
     *
     * @param connectionHandle The MQ connection handle from MQCONN/MQCONNX
     */
    public PCFAgent(int connectionHandle) {
        this.connectionHandle = connectionHandle;
    }

    /**
     * Initialize the agent by opening admin and reply queues
     */
    public void connect() throws PCFException {
        log.debug("Opening PCF agent queues");

        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();
        IntByReference hObj = new IntByReference();

        // Open the command queue for output
        MQOD adminOd = new MQOD();
        adminOd.setObjectName(PCFConstants.SYSTEM_ADMIN_COMMAND_QUEUE);
        adminOd.write();

        IBMMQJNA.INSTANCE.MQOPEN(
                connectionHandle,
                adminOd.toBytes(),
                MQOO_OUTPUT | MQOO_FAIL_IF_QUIESCING,
                hObj,
                compCode,
                reason
        );

        if (compCode.getValue() != MQCC_OK) {
            throw new PCFException("Failed to open admin command queue", reason.getValue());
        }
        adminQueueHandle = hObj.getValue();
        log.debug("Admin queue opened, handle: {}", adminQueueHandle);

        // Open a dynamic reply queue
        MQOD replyOd = new MQOD();
        replyOd.setObjectName(PCFConstants.SYSTEM_DEFAULT_MODEL_QUEUE);
        replyOd.setDynamicQName("PCF.REPLY.*");
        replyOd.write();

        // Get byte array - MQOPEN will modify it with the resolved queue name
        byte[] replyOdBytes = replyOd.toBytes();

        IBMMQJNA.INSTANCE.MQOPEN(
                connectionHandle,
                replyOdBytes,
                MQOO_INPUT_EXCLUSIVE | MQOO_FAIL_IF_QUIESCING,
                hObj,
                compCode,
                reason
        );

        if (compCode.getValue() != MQCC_OK) {
            throw new PCFException("Failed to open reply queue", reason.getValue());
        }
        replyQueueHandle = hObj.getValue();

        // Copy modified bytes back to structure and read resolved queue name
        replyOd.getPointer().write(0, replyOdBytes, 0, replyOdBytes.length);
        replyOd.read();
        replyQueueName = replyOd.getResolvedQName();

        if (replyQueueName == null || replyQueueName.trim().isEmpty()) {
            // Fallback: try ObjectName field (where dynamic queue name is resolved)
            replyQueueName = new String(replyOd.ObjectName, java.nio.charset.StandardCharsets.UTF_8).trim();
        }
        if (replyQueueName == null || replyQueueName.trim().isEmpty() || replyQueueName.startsWith("SYSTEM.DEFAULT")) {
            // Second fallback: extract from DynamicQName field
            replyQueueName = new String(replyOd.DynamicQName, java.nio.charset.StandardCharsets.UTF_8).trim();
        }

        log.debug("Reply queue opened: {}, handle: {}", replyQueueName, replyQueueHandle);
    }

    /**
     * Inquire all queues matching the given pattern
     *
     * @param queueNamePattern Queue name pattern (e.g., "*" for all, "DEV.*" for dev queues)
     * @return List of QueueInfo objects
     */
    public List<QueueInfo> inquireQueues(String queueNamePattern) throws PCFException {
        return inquireQueues(queueNamePattern, PCFConstants.MQQT_ALL);
    }

    /**
     * Inquire queues matching the given pattern and type
     *
     * @param queueNamePattern Queue name pattern
     * @param queueType        Queue type (MQQT_LOCAL, MQQT_ALIAS, etc., or MQQT_ALL)
     * @return List of QueueInfo objects
     */
    public List<QueueInfo> inquireQueues(String queueNamePattern, int queueType) throws PCFException {
        ensureConnected();

        log.debug("Inquiring queues with pattern: {}, type: {}", queueNamePattern, queueType);

        // Build the PCF command message
        byte[] message = buildInquireQueueMessage(queueNamePattern, queueType);

        // Send the command
        byte[] correlId = sendCommand(message);

        // Receive and parse responses
        List<QueueInfo> queues = new ArrayList<>();
        boolean lastMessage = false;

        while (!lastMessage) {
            byte[] response = receiveResponse(correlId);
            if (response == null) {
                break;
            }

            if (response.length < MQCFH.MQCFH_SIZE) {
                log.error("Response too small: {} bytes, expected at least {}", response.length, MQCFH.MQCFH_SIZE);
                break;
            }

            // Parse the response header
            MQCFH header = MQCFH.fromBytes(response, 0);

            if (header.CompCode != MQCC_OK) {
                if (header.Reason == MQRC_UNKNOWN_OBJECT_NAME) {
                    // No queues found matching the pattern
                    log.debug("No queues found matching pattern: {}", queueNamePattern);
                    break;
                }
                throw new PCFException("PCF command failed", header.Reason);
            }

            // Parse queue information from parameters
            QueueInfo queue = parseQueueResponse(response, header.ParameterCount);
            if (queue != null) {
                queues.add(queue);
            }

            lastMessage = header.isLast();
        }

        log.debug("Found {} queues", queues.size());
        return queues;
    }

    /**
     * Build an INQUIRE_Q PCF command message
     */
    private byte[] buildInquireQueueMessage(String queueNamePattern, int queueType) throws PCFException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Create PCF header
            MQCFH header = MQCFH.createCommand(PCFConstants.MQCMD_INQUIRE_Q, 2);
            baos.write(header.toBytes());

            // Add queue name parameter
            MQCFST queueNameParam = MQCFST.create(PCFConstants.MQCA_Q_NAME, queueNamePattern);
            baos.write(queueNameParam.toBytes());

            // Add queue type parameter
            MQCFIN queueTypeParam = MQCFIN.create(PCFConstants.MQIA_Q_TYPE, queueType);
            baos.write(queueTypeParam.toBytes());

            return baos.toByteArray();
        } catch (IOException e) {
            throw new PCFException("Failed to build PCF message", e);
        }
    }

    /**
     * Send a PCF command and return the correlation ID
     */
    private byte[] sendCommand(byte[] message) throws PCFException {
        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();

        // Create minimal message descriptor byte array
        byte[] mdBytes = MQMD.createMinimalMD(replyQueueName);

        // Create minimal put message options byte array
        int pmoOptions = MQPMO.MQPMO_NO_SYNCPOINT | MQPMO.MQPMO_NEW_MSG_ID | MQPMO.MQPMO_FAIL_IF_QUIESCING;
        byte[] pmoBytes = MQPMO.createMinimalPMO(pmoOptions);

        // Send the message
        IBMMQJNA.INSTANCE.MQPUT(
                connectionHandle,
                adminQueueHandle,
                mdBytes,
                pmoBytes,
                message.length,
                message,
                compCode,
                reason
        );

        if (compCode.getValue() != MQCC_OK) {
            throw new PCFException("Failed to send PCF command", reason.getValue());
        }

        // Read back the assigned message ID from MQMD to use as correlation ID
        // MsgId is at offset 48 in MQMD V1 (after StrucId[4]+Version[4]+Report[4]+MsgType[4]+
        // Expiry[4]+Feedback[4]+Encoding[4]+CodedCharSetId[4]+Format[8]+Priority[4]+Persistence[4] = 48)
        byte[] correlId = new byte[24];
        System.arraycopy(mdBytes, 48, correlId, 0, 24);

        log.trace("Command sent, MsgId extracted");
        return correlId;
    }

    /**
     * Receive a PCF response matching the correlation ID
     */
    private byte[] receiveResponse(byte[] correlId) throws PCFException {
        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();
        IntByReference dataLength = new IntByReference();

        // Create minimal message descriptor with CorrelId set
        // CorrelId is at offset 72 in MQMD V1 (offset 48 for MsgId + 24 bytes)
        byte[] mdBytes = MQMD.createMinimalMD(null);
        System.arraycopy(correlId, 0, mdBytes, 72, 24);

        // Create minimal get message options
        int gmoOptions = MQGMO.MQGMO_WAIT | MQGMO.MQGMO_NO_SYNCPOINT | MQGMO.MQGMO_CONVERT | MQGMO.MQGMO_FAIL_IF_QUIESCING;
        byte[] gmoBytes = MQGMO.createMinimalGMO(gmoOptions, DEFAULT_WAIT_INTERVAL, MQGMO.MQMO_MATCH_CORREL_ID);

        // Receive buffer
        byte[] buffer = new byte[BUFFER_SIZE];

        // Get the message
        IBMMQJNA.INSTANCE.MQGET(
                connectionHandle,
                replyQueueHandle,
                mdBytes,
                gmoBytes,
                buffer.length,
                buffer,
                dataLength,
                compCode,
                reason
        );

        if (compCode.getValue() == MQCC_FAILED) {
            if (reason.getValue() == MQRC_NO_MSG_AVAILABLE) {
                return null;
            }
            throw new PCFException("Failed to receive PCF response", reason.getValue());
        }

        // Return only the actual message data
        byte[] result = new byte[dataLength.getValue()];
        System.arraycopy(buffer, 0, result, 0, dataLength.getValue());
        return result;
    }

    /**
     * Parse queue information from a PCF response
     */
    private QueueInfo parseQueueResponse(byte[] response, int parameterCount) {
        QueueInfo queue = new QueueInfo();
        int offset = MQCFH.MQCFH_SIZE;

        for (int i = 0; i < parameterCount && offset + 8 <= response.length; i++) {
            // Read the structure type (first field at offset+0) and length (at offset+4)
            ByteBuffer headerBuffer = ByteBuffer.wrap(response, offset, 8);
            headerBuffer.order(ByteOrder.BIG_ENDIAN);  // PCF uses big-endian
            int structType = headerBuffer.getInt();
            int strucLength = headerBuffer.getInt();

            // Validate structure length
            if (strucLength <= 0 || offset + strucLength > response.length) {
                log.warn("Invalid structure length {} at offset {}, stopping parse", strucLength, offset);
                break;
            }

            switch (structType) {
                case PCFConstants.MQCFT_STRING:
                    MQCFST strParam = MQCFST.fromBytes(response, offset);
                    setQueueStringAttribute(queue, strParam.Parameter, strParam.getStringValue());
                    offset += strParam.StrucLength;
                    break;

                case PCFConstants.MQCFT_INTEGER:
                    MQCFIN intParam = MQCFIN.fromBytes(response, offset);
                    setQueueIntAttribute(queue, intParam.Parameter, intParam.Value);
                    offset += MQCFIN.MQCFIN_SIZE;
                    break;

                case PCFConstants.MQCFT_INTEGER_LIST:
                case PCFConstants.MQCFT_STRING_LIST:
                default:
                    // Skip other structure types using the length from header
                    offset += strucLength;
                    break;
            }
        }

        return queue.getName() != null ? queue : null;
    }

    /**
     * Set a string attribute on a QueueInfo object
     */
    private void setQueueStringAttribute(QueueInfo queue, int parameter, String value) {
        switch (parameter) {
            case PCFConstants.MQCA_Q_NAME:
                queue.setName(value);
                break;
            case PCFConstants.MQCA_Q_DESC:
                queue.setDescription(value);
                break;
            case PCFConstants.MQCA_BASE_Q_NAME:
                queue.setBaseQName(value);
                break;
            case PCFConstants.MQCA_REMOTE_Q_NAME:
                queue.setRemoteQName(value);
                break;
            case PCFConstants.MQCA_REMOTE_Q_MGR_NAME:
                queue.setRemoteQMgrName(value);
                break;
            case PCFConstants.MQCA_CLUSTER_NAME:
                queue.setClusterName(value);
                break;
        }
    }

    /**
     * Set an integer attribute on a QueueInfo object
     */
    private void setQueueIntAttribute(QueueInfo queue, int parameter, int value) {
        switch (parameter) {
            case PCFConstants.MQIA_Q_TYPE:
                queue.setType(value);
                break;
            case PCFConstants.MQIA_CURRENT_Q_DEPTH:
                queue.setCurrentDepth(value);
                break;
            case PCFConstants.MQIA_MAX_Q_DEPTH:
                queue.setMaxDepth(value);
                break;
            case PCFConstants.MQIA_MAX_MSG_LENGTH:
                queue.setMaxMsgLength(value);
                break;
            case PCFConstants.MQIA_OPEN_INPUT_COUNT:
                queue.setOpenInputCount(value);
                break;
            case PCFConstants.MQIA_OPEN_OUTPUT_COUNT:
                queue.setOpenOutputCount(value);
                break;
        }
    }

    /**
     * Ensure the agent is connected
     */
    private void ensureConnected() throws PCFException {
        if (adminQueueHandle < 0 || replyQueueHandle < 0) {
            throw new PCFException("PCF agent not connected. Call connect() first.");
        }
    }

    /**
     * Close the agent and release resources
     */
    @Override
    public void close() {
        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();

        if (replyQueueHandle >= 0) {
            IntByReference hObj = new IntByReference(replyQueueHandle);
            IBMMQJNA.INSTANCE.MQCLOSE(connectionHandle, hObj, MQCO_NONE, compCode, reason);
            replyQueueHandle = -1;
            log.debug("Reply queue closed");
        }

        if (adminQueueHandle >= 0) {
            IntByReference hObj = new IntByReference(adminQueueHandle);
            IBMMQJNA.INSTANCE.MQCLOSE(connectionHandle, hObj, MQCO_NONE, compCode, reason);
            adminQueueHandle = -1;
            log.debug("Admin queue closed");
        }
    }

    /**
     * Exception class for PCF errors
     */
    public static class PCFException extends Exception {
        private final int reasonCode;

        public PCFException(String message) {
            super(message);
            this.reasonCode = 0;
        }

        public PCFException(String message, int reasonCode) {
            super(message + " (Reason: " + reasonCode + ")");
            this.reasonCode = reasonCode;
        }

        public PCFException(String message, Throwable cause) {
            super(message, cause);
            this.reasonCode = 0;
        }

        public int getReasonCode() {
            return reasonCode;
        }
    }
}
