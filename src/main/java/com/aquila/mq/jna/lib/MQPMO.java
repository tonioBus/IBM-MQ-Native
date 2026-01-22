package com.aquila.mq.jna.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * MQPMO - MQ Put Message Options
 * Structure that specifies options for putting messages
 * Based on cmqc.h
 */
@Structure.FieldOrder({"StrucId", "Version", "Options", "Timeout",
        "Context", "KnownDestCount", "UnknownDestCount", "InvalidDestCount",
        "ResolvedQName", "ResolvedQMgrName", "RecsPresent", "PutMsgRecFields",
        "PutMsgRecOffset", "ResponseRecOffset", "PutMsgRecPtr", "ResponseRecPtr",
        "OriginalMsgHandle", "NewMsgHandle", "Action", "PubLevel"})
public class MQPMO extends Structure {

    // Structure ID
    public static final String MQPMO_STRUC_ID = "PMO ";

    // Version constants
    public static final int MQPMO_VERSION_1 = 1;
    public static final int MQPMO_VERSION_2 = 2;
    public static final int MQPMO_VERSION_3 = 3;
    public static final int MQPMO_CURRENT_VERSION = MQPMO_VERSION_3;

    // Put options
    public static final int MQPMO_NONE = 0x00000000;
    public static final int MQPMO_SYNCPOINT = 0x00000002;
    public static final int MQPMO_NO_SYNCPOINT = 0x00000004;
    public static final int MQPMO_DEFAULT_CONTEXT = 0x00000020;
    public static final int MQPMO_NEW_MSG_ID = 0x00000040;
    public static final int MQPMO_NEW_CORREL_ID = 0x00000080;
    public static final int MQPMO_PASS_IDENTITY_CONTEXT = 0x00000100;
    public static final int MQPMO_PASS_ALL_CONTEXT = 0x00000200;
    public static final int MQPMO_SET_IDENTITY_CONTEXT = 0x00000400;
    public static final int MQPMO_SET_ALL_CONTEXT = 0x00000800;
    public static final int MQPMO_ALTERNATE_USER_AUTHORITY = 0x00001000;
    public static final int MQPMO_FAIL_IF_QUIESCING = 0x00002000;
    public static final int MQPMO_NO_CONTEXT = 0x00004000;
    public static final int MQPMO_LOGICAL_ORDER = 0x00008000;
    public static final int MQPMO_ASYNC_RESPONSE = 0x00010000;
    public static final int MQPMO_SYNC_RESPONSE = 0x00020000;
    public static final int MQPMO_RESOLVE_LOCAL_Q = 0x00040000;
    public static final int MQPMO_WARN_IF_NO_SUBS_MATCHED = 0x00080000;
    public static final int MQPMO_RETAIN = 0x00200000;
    public static final int MQPMO_MD_FOR_OUTPUT_ONLY = 0x00800000;
    public static final int MQPMO_SCOPE_QMGR = 0x04000000;
    public static final int MQPMO_SUPPRESS_REPLYTO = 0x08000000;
    public static final int MQPMO_NOT_OWN_SUBS = 0x10000000;
    public static final int MQPMO_RESPONSE_AS_Q_DEF = 0x00000000;
    public static final int MQPMO_RESPONSE_AS_TOPIC_DEF = 0x00000000;

    // Structure fields - Version 1
    public byte[] StrucId = new byte[4];              // Structure identifier
    public int Version = MQPMO_VERSION_1;             // Structure version number
    public int Options = MQPMO_NONE;                  // Options that control the action
    public int Timeout = -1;                          // Reserved
    public int Context = 0;                           // Object handle of input queue
    public int KnownDestCount = 0;                    // Number of messages sent to local queues
    public int UnknownDestCount = 0;                  // Number of messages sent to remote queues
    public int InvalidDestCount = 0;                  // Number of messages not sent

    // Version 1 fields
    public byte[] ResolvedQName = new byte[48];       // Resolved name of destination queue
    public byte[] ResolvedQMgrName = new byte[48];    // Resolved name of destination queue manager

    // Version 2 fields
    public int RecsPresent = 0;                       // Number of put message records
    public int PutMsgRecFields = 0;                   // Flags indicating which MQPMR fields are present
    public int PutMsgRecOffset = 0;                   // Offset of first put message record
    public int ResponseRecOffset = 0;                 // Offset of first response record
    public Pointer PutMsgRecPtr = null;               // Address of first put message record
    public Pointer ResponseRecPtr = null;             // Address of first response record

    // Version 3 fields
    public long OriginalMsgHandle = 0;                // Original message handle
    public long NewMsgHandle = 0;                     // New message handle
    public int Action = 0;                            // Action
    public int PubLevel = 9;                          // Publish level

    public MQPMO() {
        super();
        System.arraycopy(MQPMO_STRUC_ID.getBytes(StandardCharsets.US_ASCII), 0, StrucId, 0, 4);
        Arrays.fill(ResolvedQName, (byte) ' ');
        Arrays.fill(ResolvedQMgrName, (byte) ' ');
    }

    /**
     * Create options for PCF request
     */
    public static MQPMO createForPCF() {
        MQPMO pmo = new MQPMO();
        pmo.Options = MQPMO_NO_SYNCPOINT | MQPMO_NEW_MSG_ID | MQPMO_FAIL_IF_QUIESCING;
        return pmo;
    }

    /**
     * Convert to byte array for MQPUT
     */
    public byte[] toBytes() {
        write();
        return getPointer().getByteArray(0, size());
    }

    /**
     * Create a minimal Version 1 MQPMO byte array (128 bytes)
     * This avoids issues with JNA structure padding
     */
    public static byte[] createMinimalPMO(int options) {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(128);
        buffer.order(java.nio.ByteOrder.nativeOrder());

        // StrucId "PMO "
        buffer.put(MQPMO_STRUC_ID.getBytes(StandardCharsets.US_ASCII));
        // Version
        buffer.putInt(MQPMO_VERSION_1);
        // Options
        buffer.putInt(options);
        // Timeout
        buffer.putInt(-1);
        // Context
        buffer.putInt(0);
        // KnownDestCount
        buffer.putInt(0);
        // UnknownDestCount
        buffer.putInt(0);
        // InvalidDestCount
        buffer.putInt(0);
        // ResolvedQName (48 bytes of spaces)
        byte[] spaces48 = new byte[48];
        Arrays.fill(spaces48, (byte) ' ');
        buffer.put(spaces48);
        // ResolvedQMgrName (48 bytes of spaces)
        buffer.put(spaces48);

        return buffer.array();
    }
}
