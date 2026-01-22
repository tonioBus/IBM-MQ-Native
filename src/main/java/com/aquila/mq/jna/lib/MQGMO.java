package com.aquila.mq.jna.lib;

import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * MQGMO - MQ Get Message Options
 * Structure that specifies options for getting messages
 * Based on cmqc.h
 */
@Structure.FieldOrder({"StrucId", "Version", "Options", "WaitInterval",
        "Signal1", "Signal2", "ResolvedQName", "MatchOptions", "GroupStatus",
        "SegmentStatus", "Segmentation", "Reserved1", "MsgToken", "ReturnedLength",
        "Reserved2", "MsgHandle"})
public class MQGMO extends Structure {

    // Structure ID
    public static final String MQGMO_STRUC_ID = "GMO ";

    // Version constants
    public static final int MQGMO_VERSION_1 = 1;
    public static final int MQGMO_VERSION_2 = 2;
    public static final int MQGMO_VERSION_3 = 3;
    public static final int MQGMO_VERSION_4 = 4;
    public static final int MQGMO_CURRENT_VERSION = MQGMO_VERSION_4;

    // Get options
    public static final int MQGMO_NONE = 0x00000000;
    public static final int MQGMO_WAIT = 0x00000001;
    public static final int MQGMO_NO_WAIT = 0x00000000;
    public static final int MQGMO_SYNCPOINT = 0x00000002;
    public static final int MQGMO_NO_SYNCPOINT = 0x00000004;
    public static final int MQGMO_SET_SIGNAL = 0x00000008;
    public static final int MQGMO_BROWSE_FIRST = 0x00000010;
    public static final int MQGMO_BROWSE_NEXT = 0x00000020;
    public static final int MQGMO_ACCEPT_TRUNCATED_MSG = 0x00000040;
    public static final int MQGMO_MARK_SKIP_BACKOUT = 0x00000080;
    public static final int MQGMO_MSG_UNDER_CURSOR = 0x00000100;
    public static final int MQGMO_LOCK = 0x00000200;
    public static final int MQGMO_UNLOCK = 0x00000400;
    public static final int MQGMO_BROWSE_MSG_UNDER_CURSOR = 0x00000800;
    public static final int MQGMO_SYNCPOINT_IF_PERSISTENT = 0x00001000;
    public static final int MQGMO_FAIL_IF_QUIESCING = 0x00002000;
    public static final int MQGMO_CONVERT = 0x00004000;
    public static final int MQGMO_LOGICAL_ORDER = 0x00008000;
    public static final int MQGMO_COMPLETE_MSG = 0x00010000;
    public static final int MQGMO_ALL_MSGS_AVAILABLE = 0x00020000;
    public static final int MQGMO_ALL_SEGMENTS_AVAILABLE = 0x00040000;
    public static final int MQGMO_MARK_BROWSE_HANDLE = 0x00100000;
    public static final int MQGMO_MARK_BROWSE_CO_OP = 0x00200000;
    public static final int MQGMO_UNMARK_BROWSE_CO_OP = 0x00400000;
    public static final int MQGMO_UNMARK_BROWSE_HANDLE = 0x00800000;
    public static final int MQGMO_UNMARKED_BROWSE_MSG = 0x01000000;
    public static final int MQGMO_PROPERTIES_FORCE_MQRFH2 = 0x02000000;
    public static final int MQGMO_NO_PROPERTIES = 0x04000000;
    public static final int MQGMO_PROPERTIES_IN_HANDLE = 0x08000000;
    public static final int MQGMO_PROPERTIES_COMPATIBILITY = 0x10000000;
    public static final int MQGMO_PROPERTIES_AS_Q_DEF = 0x00000000;

    // Match options
    public static final int MQMO_MATCH_MSG_ID = 0x00000001;
    public static final int MQMO_MATCH_CORREL_ID = 0x00000002;
    public static final int MQMO_MATCH_GROUP_ID = 0x00000004;
    public static final int MQMO_MATCH_MSG_SEQ_NUMBER = 0x00000008;
    public static final int MQMO_MATCH_OFFSET = 0x00000010;
    public static final int MQMO_MATCH_MSG_TOKEN = 0x00000020;
    public static final int MQMO_NONE = 0x00000000;

    // Wait interval
    public static final int MQWI_UNLIMITED = -1;

    // Structure fields - Version 1
    public byte[] StrucId = new byte[4];              // Structure identifier
    public int Version = MQGMO_VERSION_4;             // Structure version number
    public int Options = MQGMO_NONE;                  // Options that control the action
    public int WaitInterval = 0;                      // Wait interval
    public int Signal1 = 0;                           // Signal
    public int Signal2 = 0;                           // Signal identifier

    // Version 1 fields (continued)
    public byte[] ResolvedQName = new byte[48];       // Resolved name of destination queue

    // Version 2 fields
    public int MatchOptions = MQMO_MATCH_MSG_ID | MQMO_MATCH_CORREL_ID; // Match options
    public byte GroupStatus = (byte) ' ';             // Flag indicating whether message is in a group
    public byte SegmentStatus = (byte) ' ';           // Flag indicating whether message is a segment
    public byte Segmentation = (byte) ' ';            // Flag indicating whether further segmentation allowed
    public byte Reserved1 = (byte) ' ';               // Reserved

    // Version 3 fields
    public byte[] MsgToken = new byte[16];            // Message token
    public int ReturnedLength = -1;                   // Length of message data returned

    // Version 4 fields
    public int Reserved2 = 0;                         // Reserved
    public long MsgHandle = 0;                        // Message handle

    public MQGMO() {
        super();
        System.arraycopy(MQGMO_STRUC_ID.getBytes(StandardCharsets.US_ASCII), 0, StrucId, 0, 4);
        Arrays.fill(ResolvedQName, (byte) ' ');
        Arrays.fill(MsgToken, (byte) 0);
    }

    /**
     * Create options for PCF response
     */
    public static MQGMO createForPCF(int waitInterval) {
        MQGMO gmo = new MQGMO();
        gmo.Options = MQGMO_WAIT | MQGMO_NO_SYNCPOINT | MQGMO_CONVERT | MQGMO_FAIL_IF_QUIESCING;
        gmo.WaitInterval = waitInterval;
        gmo.MatchOptions = MQMO_MATCH_CORREL_ID;
        return gmo;
    }

    /**
     * Convert to byte array for MQGET
     */
    public byte[] toBytes() {
        write();
        return getPointer().getByteArray(0, size());
    }

    /**
     * Create a minimal Version 2 MQGMO byte array (80 bytes)
     * Version 2 is needed for MatchOptions
     */
    public static byte[] createMinimalGMO(int options, int waitInterval, int matchOptions) {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(80);
        buffer.order(java.nio.ByteOrder.nativeOrder());

        // StrucId "GMO "
        buffer.put(MQGMO_STRUC_ID.getBytes(StandardCharsets.US_ASCII));
        // Version (need V2 for MatchOptions)
        buffer.putInt(MQGMO_VERSION_2);
        // Options
        buffer.putInt(options);
        // WaitInterval
        buffer.putInt(waitInterval);
        // Signal1
        buffer.putInt(0);
        // Signal2
        buffer.putInt(0);
        // ResolvedQName (48 bytes of spaces)
        byte[] spaces48 = new byte[48];
        Arrays.fill(spaces48, (byte) ' ');
        buffer.put(spaces48);
        // MatchOptions (Version 2 field)
        buffer.putInt(matchOptions);
        // GroupStatus, SegmentStatus, Segmentation, Reserved1 (4 bytes)
        buffer.put((byte) ' ');
        buffer.put((byte) ' ');
        buffer.put((byte) ' ');
        buffer.put((byte) ' ');

        return buffer.array();
    }
}
