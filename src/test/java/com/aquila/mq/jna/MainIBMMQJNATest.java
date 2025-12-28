package com.aquila.mq.jna;

import com.aquila.mq.jna.lib.IBMMQJNA;
import com.aquila.mq.jna.lib.MQCD;
import com.aquila.mq.jna.lib.MQCNO;
import com.ibm.mq.constants.CMQC;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.ibm.mq.constants.CMQC.*;
import static com.ibm.mq.constants.CMQXC.*;

@Slf4j
public class MainIBMMQJNATest {

    @Test
    public void testAllOperations() {
        log.info("Starting IBM MQ JNA Test");
        // Connection configuration
        String queueManagerName = "QM1";
        String channelName = "DEV.APP.SVRCONN";  // Default channel in IBM MQ Docker image
        String connectionName = "localhost(1414)";  // Host:port

        // Prepare the Queue Manager name (48 bytes, filled with spaces)
        byte[] qmgrName = new byte[IBMMQJNA.MQ_Q_MGR_NAME_LENGTH];
        Arrays.fill(qmgrName, (byte) ' ');
        byte[] qmgrBytes = queueManagerName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(qmgrBytes, 0, qmgrName, 0, Math.min(qmgrBytes.length, qmgrName.length));

        // Create and configure the MQCD structure (Channel Definition)
        MQCD mqcd = new MQCD();
        mqcd.Version = MQCD_VERSION_10;
        mqcd.setChannelName(channelName);
        mqcd.setConnectionName(connectionName);
        mqcd.setUser("app");
        mqcd.setPassword("passw0rd");
        mqcd.ChannelType = MQCHT_CLNTCONN;
        mqcd.TransportType = MQXPT_TCP;

        // Note: The password is not set in MQCD but in MQCSP
        // For DEV.ADMIN.SVRCONN, authentication may be required

        // Create and configure the MQCNO structure (Connection Options)
        MQCNO mqcno = new MQCNO();
        mqcno.setClientConnection(mqcd);

        // Output variables
        IntByReference hConn = new IntByReference(MQHC_UNUSABLE_HCONN);
        IntByReference compCode = new IntByReference();
        IntByReference reason = new IntByReference();

        // Connection to Queue Manager via TCP/IP
        log.info("========================================");
        log.info("Connection to Queue Manager via TCP/IP");
        log.info("  Queue Manager: {}", queueManagerName);
        log.info("  Channel: {}", channelName);
        log.info("  Connection: {}", connectionName);
        log.info("========================================");

        try {
            IBMMQJNA.INSTANCE.MQCONNX(new String(qmgrName), mqcno, hConn, compCode, reason);

            if (compCode.getValue() == MQCC_FAILED) {
                log.error("ERROR: MQCONNX failed");
                log.error("  Completion Code: {}", compCode.getValue());
                log.error("  Reason Code: {}", reason.getValue());
                printReasonCode(reason.getValue());
                System.exit(1);
            }

            byte[] mqcod = "DEV.QUEUE.1".getBytes(StandardCharsets.UTF_8);
            int openOptions = CMQC.MQGMO_WAIT | CMQC.MQGMO_SYNCPOINT;
            IntByReference pHobj = new IntByReference();
            IBMMQJNA.INSTANCE.MQOPEN(hConn.getValue(), mqcod, openOptions, pHobj, compCode, reason);
            if (compCode.getValue() == MQCC_FAILED) {
                log.error("ERROR: MQOPEN failed");
                log.error("  Completion Code: {}", compCode.getValue());
                log.error("  Reason Code: {}", reason.getValue());
                printReasonCode(reason.getValue());
                System.exit(1);
            }

            log.info("Connected successfully!");
            log.info("  Connection Handle: {}", hConn.getValue());
            log.info("  Completion Code: {}", compCode.getValue());

            // Clean disconnection
            log.info("Disconnecting...");
            IBMMQJNA.INSTANCE.MQDISC(hConn, compCode, reason);

            if (compCode.getValue() == MQCC_OK) {
                log.info("Disconnected successfully!");
            } else {
                log.warn("Warning during disconnection");
                log.warn("  Completion Code: {}", compCode.getValue());
                log.warn("  Reason Code: {}", reason.getValue());
            }

        } catch (UnsatisfiedLinkError e) {
            log.error("========================================");
            log.error("ERROR: IBM MQ library not found!");
            log.error("========================================");
            log.error("The native IBM MQ library (mqm.dll or libmqm.so) is not available.");
            log.error("");
            log.error("To resolve this issue:");
            log.error("1. Install the IBM MQ client:");
            log.error("   Windows: https://www.ibm.com/support/pages/downloading-ibm-mq-clients");
            log.error("   Linux: sudo apt-get install ibmmq-client (or via yum/dnf)");
            log.error("");
            log.error("2. Add the directory containing mqm.dll/libmqm.so to the system PATH");
            log.error("   Windows: C:\\Program Files\\IBM\\MQ\\bin (or bin64)");
            log.error("   Linux: /opt/mqm/lib64");
            log.error("");
            log.error("Detailed error: ", e);
            System.exit(1);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Displays the error message corresponding to the reason code
     */
    private static void printReasonCode(int reason) {
        System.err.println("");
        System.err.println("========================================");
        System.err.println("Error Diagnostic:");
        System.err.println("========================================");
        switch (reason) {
            case MQRC_ENVIRONMENT_ERROR:
                System.err.println("  MQRC_ENVIRONMENT_ERROR (2012)");
                System.err.println("");
                System.err.println("  → IBM MQ environment error");
                System.err.println("  → The MQCD/MQCNO structures are probably misconfigured");
                System.err.println("");
                System.err.println("  Possible causes:");
                System.err.println("    1. Size/padding issue in JNA structures");
                System.err.println("    2. Incompatible structure version");
                System.err.println("    3. 32-bit/64-bit architecture mismatch");
                System.err.println("    4. Structure fields not initialized correctly");
                System.err.println("");
                System.err.println("  Solutions to try:");
                System.err.println("    • Simplify MQCD by using VERSION_1 instead of VERSION_11");
                System.err.println("    • Verify that JVM and MQ client have the same architecture (64-bit)");
                System.err.println("    • Try with DEV.APP.SVRCONN channel instead of DEV.ADMIN.SVRCONN");
                System.err.println("    • Check server logs: podman logs QM1");
                System.err.println("    • Enable JNA debug mode: -Djna.dump_memory=true");
                break;
            case MQRC_NOT_AUTHORIZED:
                System.err.println("  MQRC_NOT_AUTHORIZED (2035)");
                System.err.println("");
                System.err.println("  → Authentication problem");
                System.err.println("  → The DEV.ADMIN.SVRCONN channel probably requires authentication");
                System.err.println("");
                System.err.println("  Solutions:");
                System.err.println("    • Use DEV.APP.SVRCONN (no auth required)");
                System.err.println("    • OR implement MQCSP to provide user/password");
                break;
            case MQRC_Q_MGR_NOT_AVAILABLE:
                System.err.println("  MQRC_Q_MGR_NOT_AVAILABLE (2059)");
                System.err.println("");
                System.err.println("  → The Queue Manager is not accessible");
                System.err.println("");
                System.err.println("  Solutions:");
                System.err.println("    • Verify that QM1 is the correct Queue Manager name");
                System.err.println("    • Verify the channel name: DEV.APP.SVRCONN or DEV.ADMIN.SVRCONN");
                System.err.println("    • Verify the connection: 172.20.26.188(1414)");
                System.err.println("    • Test with telnet: telnet 172.20.26.188 1414");
                break;
            case MQRC_HOST_NOT_AVAILABLE:
                System.err.println("  MQRC_HOST_NOT_AVAILABLE (2538)");
                System.err.println("");
                System.err.println("  → The server is not accessible on the network");
                System.err.println("");
                System.err.println("  Solutions:");
                System.err.println("    • Verify that the container is running: podman ps | grep QM1");
                System.err.println("    • Verify the IP: 172.20.26.188");
                System.err.println("    • Verify the port: 1414");
                System.err.println("    • Test: telnet 172.20.26.188 1414");
                break;
            default:
                System.err.println("  Reason code: " + reason);
                System.err.println("");
                System.err.println("  → Consult the IBM MQ documentation:");
                System.err.println("    https://www.ibm.com/docs/en/ibm-mq/9.3?topic=codes-mqrc-" + reason);
        }
        System.err.println("========================================");
    }
}
