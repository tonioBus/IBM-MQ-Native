package com.aquila.mq.jna.lib;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public interface IBMMQJNA extends Library {

    // Load the IBM MQ native library
    // Linux: libmqm.so
    // Windows: mqm.dll
    IBMMQJNA INSTANCE = Native.load("mqm", IBMMQJNA.class);

    // Lengths
    int MQ_Q_MGR_NAME_LENGTH = 48;
    int MQ_Q_NAME_LENGTH = 48;
    int MQ_CHANNEL_NAME_LENGTH = 20;
    int MQ_CONN_NAME_LENGTH = 264;

    // ========== IBM MQ Structures ==========

    // Note: For simplicity, we use byte[] for structures
    // In production, JNA Structure classes should be defined

    // ========== IBM MQ Functions ==========

    /**
     * MQCONN - Simple connection to Queue Manager
     *
     * @param pQMgrName Queue Manager name (48 bytes)
     * @param pHconn    Connection handle (output)
     * @param pCompCode Completion code (output)
     * @param pReason   Reason code (output)
     */
    void MQCONN(
            byte[] pQMgrName,
            IntByReference pHconn,
            IntByReference pCompCode,
            IntByReference pReason
    );

    /**
     * <code>
     * void MQENTRY MQCONNX (
     * PMQCHAR   pQMgrName,       // I: Name of queue manager
     * PMQCNO    pConnectOpts,    // IO: Options that control the action of
     * PMQHCONN  pHconn,          // O: Connection handle
     * PMQLONG   pCompCode,       // OC: Completion code
     * PMQLONG   pReason);        // OR: Reason code qualifying CompCode
     * </code>
     * MQCONNX - Extended connection (for TCP/IP client)
     *
     * @param pQMgrName    Queue Manager name
     * @param pConnectOpts Connection options (MQCNO structure)
     * @param pHconn       Connection handle (output)
     * @param pCompCode    Completion code (output)
     * @param pReason      Reason code (output)
     */
    void MQCONNX(
            String pQMgrName,
            MQCNO pConnectOpts,
            IntByReference pHconn,
            IntByReference pCompCode,
            IntByReference pReason
    );

    /**
     * MQDISC - Disconnect
     *
     * @param pHconn    Connection handle
     * @param pCompCode Completion code (output)
     * @param pReason   Reason code (output)
     */
    void MQDISC(
            IntByReference pHconn,
            IntByReference pCompCode,
            IntByReference pReason
    );

    /**
     * MQOPEN - Open an object (queue)
     *
     * @param Hconn     Connection handle
     * @param pObjDesc  Object description (MQOD structure)
     * @param Options   Open options
     * @param pHobj     Object handle (output)
     * @param pCompCode Completion code (output)
     * @param pReason   Reason code (output)
     */
    void MQOPEN(
            int Hconn,
            byte[] pObjDesc,
            int Options,
            IntByReference pHobj,
            IntByReference pCompCode,
            IntByReference pReason
    );

    /**
     * MQCLOSE - Close an object
     *
     * @param Hconn     Connection handle
     * @param pHobj     Object handle
     * @param Options   Close options
     * @param pCompCode Completion code (output)
     * @param pReason   Reason code (output)
     */
    void MQCLOSE(
            int Hconn,
            IntByReference pHobj,
            int Options,
            IntByReference pCompCode,
            IntByReference pReason
    );

    /**
     * MQPUT - Send a message
     *
     * @param Hconn        Connection handle
     * @param Hobj         Object handle
     * @param pMsgDesc     Message descriptor (MQMD structure)
     * @param pPutMsgOpts  Put options (MQPMO structure)
     * @param BufferLength Message length
     * @param pBuffer      Message content
     * @param pCompCode    Completion code (output)
     * @param pReason      Reason code (output)
     */
    void MQPUT(
            int Hconn,
            int Hobj,
            byte[] pMsgDesc,
            byte[] pPutMsgOpts,
            int BufferLength,
            byte[] pBuffer,
            IntByReference pCompCode,
            IntByReference pReason
    );

    /**
     * MQGET - Receive a message
     *
     * @param Hconn        Connection handle
     * @param Hobj         Object handle
     * @param pMsgDesc     Message descriptor (MQMD structure)
     * @param pGetMsgOpts  Get options (MQGMO structure)
     * @param BufferLength Buffer size
     * @param pBuffer      Buffer to receive the message
     * @param pDataLength  Actual message length (output)
     * @param pCompCode    Completion code (output)
     * @param pReason      Reason code (output)
     */
    void MQGET(
            int Hconn,
            int Hobj,
            byte[] pMsgDesc,
            byte[] pGetMsgOpts,
            int BufferLength,
            byte[] pBuffer,
            IntByReference pDataLength,
            IntByReference pCompCode,
            IntByReference pReason
    );
}