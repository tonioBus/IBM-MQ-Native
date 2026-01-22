package com.aquila.mq.jna.lib;

import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * MQMD - MQ Message Descriptor
 * Structure that describes the message
 * Based on cmqc.h
 */
@Structure.FieldOrder({"StrucId", "Version", "Report", "MsgType", "Expiry",
        "Feedback", "Encoding", "CodedCharSetId", "Format", "Priority",
        "Persistence", "MsgId", "CorrelId", "BackoutCount", "ReplyToQ",
        "ReplyToQMgr", "UserIdentifier", "AccountingToken", "ApplIdentityData",
        "PutApplType", "PutApplName", "PutDate", "PutTime", "ApplOriginData",
        "GroupId", "MsgSeqNumber", "Offset", "MsgFlags", "OriginalLength"})
public class MQMD extends Structure {

    // Structure ID
    public static final String MQMD_STRUC_ID = "MD  ";

    // Version constants
    public static final int MQMD_VERSION_1 = 1;
    public static final int MQMD_VERSION_2 = 2;
    public static final int MQMD_CURRENT_VERSION = MQMD_VERSION_2;

    // Message types
    public static final int MQMT_REQUEST = 1;
    public static final int MQMT_REPLY = 2;
    public static final int MQMT_DATAGRAM = 8;
    public static final int MQMT_REPORT = 4;

    // Persistence
    public static final int MQPER_NOT_PERSISTENT = 0;
    public static final int MQPER_PERSISTENT = 1;
    public static final int MQPER_PERSISTENCE_AS_Q_DEF = 2;

    // Format names
    public static final String MQFMT_ADMIN = "MQADMIN ";
    public static final String MQFMT_PCF = "MQPCF   ";
    public static final String MQFMT_STRING = "MQSTR   ";
    public static final String MQFMT_NONE = "        ";

    // Special values
    public static final int MQEI_UNLIMITED = -1;
    public static final int MQPRI_PRIORITY_AS_Q_DEF = -1;
    public static final int MQAT_DEFAULT = -1;

    // Report options
    public static final int MQRO_NONE = 0;

    // Feedback codes
    public static final int MQFB_NONE = 0;

    // Encoding
    public static final int MQENC_NATIVE = 0x00000111;

    // Character Set IDs
    public static final int MQCCSI_Q_MGR = 0;
    public static final int MQCCSI_DEFAULT = 0;

    // Structure fields - Version 1
    public byte[] StrucId = new byte[4];              // Structure identifier
    public int Version = MQMD_VERSION_2;              // Structure version number
    public int Report = MQRO_NONE;                    // Report options
    public int MsgType = MQMT_REQUEST;                // Message type
    public int Expiry = MQEI_UNLIMITED;               // Message lifetime
    public int Feedback = MQFB_NONE;                  // Feedback or reason code
    public int Encoding = MQENC_NATIVE;               // Numeric encoding of message data
    public int CodedCharSetId = MQCCSI_Q_MGR;         // Character set identifier of message data
    public byte[] Format = new byte[8];               // Format name of message data
    public int Priority = MQPRI_PRIORITY_AS_Q_DEF;    // Message priority
    public int Persistence = MQPER_NOT_PERSISTENT;    // Message persistence
    public byte[] MsgId = new byte[24];               // Message identifier
    public byte[] CorrelId = new byte[24];            // Correlation identifier
    public int BackoutCount = 0;                      // Backout counter
    public byte[] ReplyToQ = new byte[48];            // Name of reply queue
    public byte[] ReplyToQMgr = new byte[48];         // Name of reply queue manager
    public byte[] UserIdentifier = new byte[12];      // User identifier
    public byte[] AccountingToken = new byte[32];     // Accounting token
    public byte[] ApplIdentityData = new byte[32];    // Application data relating to identity
    public int PutApplType = MQAT_DEFAULT;            // Type of application that put the message
    public byte[] PutApplName = new byte[28];         // Name of application that put the message
    public byte[] PutDate = new byte[8];              // Date when message was put
    public byte[] PutTime = new byte[8];              // Time when message was put
    public byte[] ApplOriginData = new byte[4];       // Application data relating to origin

    // Version 2 fields
    public byte[] GroupId = new byte[24];             // Group identifier
    public int MsgSeqNumber = 1;                      // Sequence number of logical message within group
    public int Offset = 0;                            // Offset of data in physical message from start
    public int MsgFlags = 0;                          // Message flags
    public int OriginalLength = -1;                   // Length of original message

    public MQMD() {
        super();
        System.arraycopy(MQMD_STRUC_ID.getBytes(StandardCharsets.US_ASCII), 0, StrucId, 0, 4);
        Arrays.fill(Format, (byte) ' ');
        Arrays.fill(MsgId, (byte) 0);
        Arrays.fill(CorrelId, (byte) 0);
        Arrays.fill(ReplyToQ, (byte) ' ');
        Arrays.fill(ReplyToQMgr, (byte) ' ');
        Arrays.fill(UserIdentifier, (byte) ' ');
        Arrays.fill(AccountingToken, (byte) 0);
        Arrays.fill(ApplIdentityData, (byte) ' ');
        Arrays.fill(PutApplName, (byte) ' ');
        Arrays.fill(PutDate, (byte) ' ');
        Arrays.fill(PutTime, (byte) ' ');
        Arrays.fill(ApplOriginData, (byte) ' ');
        Arrays.fill(GroupId, (byte) 0);
    }

    /**
     * Set the format field
     */
    public void setFormat(String format) {
        Arrays.fill(Format, (byte) ' ');
        byte[] formatBytes = format.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(formatBytes, 0, Format, 0, Math.min(formatBytes.length, Format.length));
    }

    /**
     * Set the reply-to queue name
     */
    public void setReplyToQ(String queueName) {
        Arrays.fill(ReplyToQ, (byte) ' ');
        byte[] queueBytes = queueName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(queueBytes, 0, ReplyToQ, 0, Math.min(queueBytes.length, ReplyToQ.length));
    }

    /**
     * Get the message ID as hex string
     */
    public String getMsgIdHex() {
        StringBuilder sb = new StringBuilder();
        for (byte b : MsgId) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Reset message ID and correlation ID for receiving new messages
     */
    public void resetForGet() {
        Arrays.fill(MsgId, (byte) 0);
        Arrays.fill(CorrelId, (byte) 0);
    }

    /**
     * Copy correlation ID from message ID (for request/reply)
     */
    public void copyMsgIdToCorrelId() {
        System.arraycopy(MsgId, 0, CorrelId, 0, 24);
    }

    /**
     * Convert to byte array for MQPUT/MQGET
     */
    public byte[] toBytes() {
        write();
        return getPointer().getByteArray(0, size());
    }

    /**
     * Create a PCF admin request message descriptor
     */
    public static MQMD createPCFRequest() {
        MQMD md = new MQMD();
        md.MsgType = MQMT_REQUEST;
        md.setFormat(MQFMT_ADMIN);
        md.Persistence = MQPER_NOT_PERSISTENT;
        return md;
    }

    /**
     * Create a minimal Version 1 MQMD byte array for PCF request (324 bytes)
     */
    public static byte[] createMinimalMD(String replyToQ) {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(324);
        buffer.order(java.nio.ByteOrder.nativeOrder());

        byte[] spaces = new byte[48];
        Arrays.fill(spaces, (byte) ' ');

        // StrucId "MD  "
        buffer.put(MQMD_STRUC_ID.getBytes(StandardCharsets.US_ASCII));
        // Version
        buffer.putInt(MQMD_VERSION_1);
        // Report
        buffer.putInt(MQRO_NONE);
        // MsgType
        buffer.putInt(MQMT_REQUEST);
        // Expiry
        buffer.putInt(MQEI_UNLIMITED);
        // Feedback
        buffer.putInt(MQFB_NONE);
        // Encoding
        buffer.putInt(MQENC_NATIVE);
        // CodedCharSetId
        buffer.putInt(MQCCSI_Q_MGR);
        // Format (8 bytes) - "MQADMIN "
        byte[] format = new byte[8];
        Arrays.fill(format, (byte) ' ');
        System.arraycopy(MQFMT_ADMIN.getBytes(StandardCharsets.US_ASCII), 0, format, 0,
                Math.min(MQFMT_ADMIN.length(), 8));
        buffer.put(format);
        // Priority
        buffer.putInt(MQPRI_PRIORITY_AS_Q_DEF);
        // Persistence
        buffer.putInt(MQPER_NOT_PERSISTENT);
        // MsgId (24 bytes of zeros - will be generated)
        buffer.put(new byte[24]);
        // CorrelId (24 bytes of zeros)
        buffer.put(new byte[24]);
        // BackoutCount
        buffer.putInt(0);
        // ReplyToQ (48 bytes)
        byte[] replyQ = new byte[48];
        Arrays.fill(replyQ, (byte) ' ');
        if (replyToQ != null) {
            byte[] replyBytes = replyToQ.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(replyBytes, 0, replyQ, 0, Math.min(replyBytes.length, 48));
        }
        buffer.put(replyQ);
        // ReplyToQMgr (48 bytes of spaces)
        buffer.put(spaces);
        // UserIdentifier (12 bytes of spaces)
        byte[] spaces12 = new byte[12];
        Arrays.fill(spaces12, (byte) ' ');
        buffer.put(spaces12);
        // AccountingToken (32 bytes of zeros)
        buffer.put(new byte[32]);
        // ApplIdentityData (32 bytes of spaces)
        byte[] spaces32 = new byte[32];
        Arrays.fill(spaces32, (byte) ' ');
        buffer.put(spaces32);
        // PutApplType
        buffer.putInt(MQAT_DEFAULT);
        // PutApplName (28 bytes of spaces)
        byte[] spaces28 = new byte[28];
        Arrays.fill(spaces28, (byte) ' ');
        buffer.put(spaces28);
        // PutDate (8 bytes of spaces)
        byte[] spaces8 = new byte[8];
        Arrays.fill(spaces8, (byte) ' ');
        buffer.put(spaces8);
        // PutTime (8 bytes of spaces)
        buffer.put(spaces8);
        // ApplOriginData (4 bytes of spaces)
        byte[] spaces4 = new byte[4];
        Arrays.fill(spaces4, (byte) ' ');
        buffer.put(spaces4);

        return buffer.array();
    }
}
