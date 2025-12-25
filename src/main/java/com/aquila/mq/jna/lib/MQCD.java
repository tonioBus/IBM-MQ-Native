package com.aquila.mq.jna.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * MQCD - MQ Channel Definition
 * Structure to define connection parameters for IBM MQ channels
 * Based on MQCD structure from cmqxc.h
 * <p>
 * This structure supports versions 1-12 of the MQCD structure.
 * Fields are ordered to match the C structure definition exactly.
 */
@Structure.FieldOrder({"ChannelName", "Version", "ChannelType", "TransportType",
        "Desc", "QMgrName", "XmitQName", "ShortConnectionName",
        "MCAName", "ModeName", "TpName", "BatchSize", "DiscInterval",
        "ShortRetryCount", "ShortRetryInterval", "LongRetryCount",
        "LongRetryInterval", "SecurityExit", "MsgExit", "SendExit",
        "ReceiveExit", "SeqNumberWrap", "MaxMsgLength", "PutAuthority",
        "DataConversion", "SecurityUserData", "MsgUserData", "SendUserData",
        "ReceiveUserData", "UserIdentifier", "Password", "MCAUserIdentifier",
        "MCAType", "ConnectionName", "RemoteUserIdentifier", "RemotePassword",
        "MsgRetryExit", "MsgRetryUserData", "MsgRetryCount", "MsgRetryInterval",
        "HeartBeatInterval", "BatchInterval", "NonPersistentMsgSpeed",
        "StrucLength", "ExitNameLength", "ExitDataLength", "MsgExitsDefined",
        "SendExitsDefined", "ReceiveExitsDefined", "MsgExitPtr", "MsgUserDataPtr",
        "SendExitPtr", "SendUserDataPtr", "ReceiveExitPtr", "ReceiveUserDataPtr",
        "ClusterPtr", "ClustersDefined", "NetworkPriority", "LongMCAUserIdLength",
        "LongRemoteUserIdLength", "LongMCAUserIdPtr", "LongRemoteUserIdPtr",
        "MCASecurityId", "RemoteSecurityId", "SSLCipherSpec", "SSLPeerNamePtr",
        "SSLPeerNameLength", "SSLClientAuth", "KeepAliveInterval", "LocalAddress",
        "BatchHeartbeat", "HdrCompList", "MsgCompList", "CLWLChannelRank",
        "CLWLChannelPriority", "CLWLChannelWeight", "ChannelMonitoring",
        "ChannelStatistics", "SharingConversations", "PropertyControl",
        "MaxInstances", "MaxInstancesPerClient", "ClientChannelWeight",
        "ConnectionAffinity", "BatchDataLimit", "UseDLQ", "DefReconnect",
        "CertificateLabel", "SPLProtection"})
public class MQCD extends Structure {

    // Structure Version Numbers
    public static final int MQCD_VERSION_1 = 1;
    public static final int MQCD_VERSION_2 = 2;
    public static final int MQCD_VERSION_3 = 3;
    public static final int MQCD_VERSION_4 = 4;
    public static final int MQCD_VERSION_5 = 5;
    public static final int MQCD_VERSION_6 = 6;
    public static final int MQCD_VERSION_7 = 7;
    public static final int MQCD_VERSION_8 = 8;
    public static final int MQCD_VERSION_9 = 9;
    public static final int MQCD_VERSION_10 = 10;
    public static final int MQCD_VERSION_11 = 11;
    public static final int MQCD_VERSION_12 = 12;
    public static final int MQCD_CURRENT_VERSION = MQCD_VERSION_12;

    // Channel types
    public static final int MQCHT_SENDER = 1;
    public static final int MQCHT_SERVER = 2;
    public static final int MQCHT_RECEIVER = 3;
    public static final int MQCHT_REQUESTER = 4;
    public static final int MQCHT_CLNTCONN = 6;  // Client connection
    public static final int MQCHT_SVRCONN = 7;   // Server connection

    // Transport types
    public static final int MQXPT_TCP = 2;

    // Structure fields - must match the C structure layout exactly
    // Version 1 fields
    public byte[] ChannelName = new byte[20];           // Channel definition name
    public int Version = MQCD_CURRENT_VERSION;          // Structure version number
    public int ChannelType = MQCHT_CLNTCONN;            // Channel type
    public int TransportType = MQXPT_TCP;               // Transport type
    public byte[] Desc = new byte[64];                  // Channel description
    public byte[] QMgrName = new byte[48];              // Queue-manager name
    public byte[] XmitQName = new byte[48];             // Transmission queue name
    public byte[] ShortConnectionName = new byte[20];   // First 20 bytes of connection name
    public byte[] MCAName = new byte[20];               // MCA name (Reserved)
    public byte[] ModeName = new byte[8];               // LU 6.2 Mode name
    public byte[] TpName = new byte[64];                // LU 6.2 transaction program name
    public int BatchSize = 50;                          // Batch size
    public int DiscInterval = 6000;                     // Disconnect interval
    public int ShortRetryCount = 10;                    // Short retry count
    public int ShortRetryInterval = 60;                 // Short retry wait interval
    public int LongRetryCount = 999999999;              // Long retry count
    public int LongRetryInterval = 1200;                // Long retry wait interval
    public byte[] SecurityExit = new byte[128];         // Channel security exit name
    public byte[] MsgExit = new byte[128];              // Channel message exit name
    public byte[] SendExit = new byte[128];             // Channel send exit name
    public byte[] ReceiveExit = new byte[128];          // Channel receive exit name
    public int SeqNumberWrap = 999999999;               // Highest allowable message sequence number
    public int MaxMsgLength = 4194304;                  // Maximum message length
    public int PutAuthority = 1;                        // Put authority (MQPA_DEFAULT)
    public int DataConversion = 0;                      // Data conversion
    public byte[] SecurityUserData = new byte[32];      // Channel security exit user data
    public byte[] MsgUserData = new byte[32];           // Channel message exit user data
    public byte[] SendUserData = new byte[32];          // Channel send exit user data
    public byte[] ReceiveUserData = new byte[32];       // Channel receive exit user data
    public byte[] UserIdentifier = new byte[12];        // User identifier
    public byte[] Password = new byte[12];              // Password
    public byte[] MCAUserIdentifier = new byte[12];     // First 12 bytes of MCA user identifier
    public int MCAType = 1;                             // Message channel agent type (MQMCAT_PROCESS)
    public byte[] ConnectionName = new byte[264];       // Connection name (host:port)
    public byte[] RemoteUserIdentifier = new byte[12];  // First 12 bytes of user identifier from partner
    public byte[] RemotePassword = new byte[12];        // Password from partner

    // Version 2 fields
    public byte[] MsgRetryExit = new byte[128];         // Channel message retry exit name
    public byte[] MsgRetryUserData = new byte[32];      // Channel message retry exit user data
    public int MsgRetryCount = 10;                      // Number of times MCA will try to put the message
    public int MsgRetryInterval = 1000;                 // Minimum interval in milliseconds for retry

    // Version 3 fields
    public int HeartBeatInterval = 300;                 // Time in seconds between heartbeat flows
    public int BatchInterval = 0;                       // Batch duration
    public int NonPersistentMsgSpeed = 2;               // Speed at which nonpersistent messages are sent (MQNPMS_FAST)
    public int StrucLength = 0;                         // Length of MQCD structure
    public int ExitNameLength = 128;                    // Length of exit name
    public int ExitDataLength = 32;                     // Length of exit user data
    public int MsgExitsDefined = 0;                     // Number of message exits defined
    public int SendExitsDefined = 0;                    // Number of send exits defined
    public int ReceiveExitsDefined = 0;                 // Number of receive exits defined
    public Pointer MsgExitPtr = null;                   // Address of first MsgExit field
    public Pointer MsgUserDataPtr = null;               // Address of first MsgUserData field
    public Pointer SendExitPtr = null;                  // Address of first SendExit field
    public Pointer SendUserDataPtr = null;              // Address of first SendUserData field
    public Pointer ReceiveExitPtr = null;               // Address of first ReceiveExit field
    public Pointer ReceiveUserDataPtr = null;           // Address of first ReceiveUserData field

    // Version 4 fields
    public Pointer ClusterPtr = null;                   // Address of a list of cluster names
    public int ClustersDefined = 0;                     // Number of clusters to which the channel belongs
    public int NetworkPriority = 0;                     // Network priority

    // Version 5 fields
    public int LongMCAUserIdLength = 0;                 // Length of long MCA user identifier
    public int LongRemoteUserIdLength = 0;              // Length of long remote user identifier
    public Pointer LongMCAUserIdPtr = null;             // Address of long MCA user identifier
    public Pointer LongRemoteUserIdPtr = null;          // Address of long remote user identifier
    public byte[] MCASecurityId = new byte[40];         // MCA security identifier (MQBYTE40)
    public byte[] RemoteSecurityId = new byte[40];      // Remote security identifier (MQBYTE40)

    // Version 6 fields
    public byte[] SSLCipherSpec = new byte[32];         // SSL CipherSpec
    public Pointer SSLPeerNamePtr = null;               // Address of SSL peer name
    public int SSLPeerNameLength = 0;                   // Length of SSL peer name
    public int SSLClientAuth = 0;                       // Whether SSL client authentication is required
    public int KeepAliveInterval = -1;                  // Keepalive interval (MQKAI_AUTO)
    public byte[] LocalAddress = new byte[48];          // Local communications address
    public int BatchHeartbeat = 0;                      // Batch heartbeat interval

    // Version 7 fields
    public int[] HdrCompList = new int[2];              // Header data compression list (MQLONG[2])
    public int[] MsgCompList = new int[16];             // Message data compression list (MQLONG[16])
    public int CLWLChannelRank = 0;                     // Channel rank
    public int CLWLChannelPriority = 0;                 // Channel priority
    public int CLWLChannelWeight = 50;                  // Channel weight
    public int ChannelMonitoring = 0;                   // Channel monitoring
    public int ChannelStatistics = 0;                   // Channel statistics

    // Version 8 fields
    public int SharingConversations = 10;               // Limit on sharing conversations
    public int PropertyControl = 0;                     // Message property control
    public int MaxInstances = 999999999;                // Limit on SVRCONN channel instances
    public int MaxInstancesPerClient = 999999999;       // Limit on SVRCONN channel instances per client
    public int ClientChannelWeight = 0;                 // Client channel weight
    public int ConnectionAffinity = 1;                  // Connection affinity (MQCAFTY_PREFERRED)

    // Version 9 fields
    public int BatchDataLimit = 5000;                   // Batch data limit
    public int UseDLQ = 2;                              // Use Dead Letter Queue
    public int DefReconnect = 0;                        // Default client reconnect option (MQRCN_NO)

    // Version 10 fields
    public byte[] CertificateLabel = new byte[64];      // Certificate label

    // Version 11 fields
    public int SPLProtection = 0;                       // SPL Protection (MQSPL_PASSTHRU)

    // Version 12 fields
    // (No new fields in version 12)

    public MQCD() {
        super();
        // Initialize all byte arrays with spaces (blank-padded, as per MQ convention)
        Arrays.fill(ChannelName, (byte) ' ');
        Arrays.fill(Desc, (byte) ' ');
        Arrays.fill(QMgrName, (byte) ' ');
        Arrays.fill(XmitQName, (byte) ' ');
        Arrays.fill(ShortConnectionName, (byte) ' ');
        Arrays.fill(MCAName, (byte) ' ');
        Arrays.fill(ModeName, (byte) ' ');
        Arrays.fill(TpName, (byte) ' ');
        Arrays.fill(SecurityExit, (byte) ' ');
        Arrays.fill(MsgExit, (byte) ' ');
        Arrays.fill(SendExit, (byte) ' ');
        Arrays.fill(ReceiveExit, (byte) ' ');
        Arrays.fill(SecurityUserData, (byte) ' ');
        Arrays.fill(MsgUserData, (byte) ' ');
        Arrays.fill(SendUserData, (byte) ' ');
        Arrays.fill(ReceiveUserData, (byte) ' ');
        Arrays.fill(UserIdentifier, (byte) ' ');
        Arrays.fill(Password, (byte) ' ');
        Arrays.fill(MCAUserIdentifier, (byte) ' ');
        Arrays.fill(ConnectionName, (byte) ' ');
        Arrays.fill(RemoteUserIdentifier, (byte) ' ');
        Arrays.fill(RemotePassword, (byte) ' ');
        Arrays.fill(MsgRetryExit, (byte) ' ');
        Arrays.fill(MsgRetryUserData, (byte) ' ');
        Arrays.fill(MCASecurityId, (byte) 0);
        Arrays.fill(RemoteSecurityId, (byte) 0);
        Arrays.fill(SSLCipherSpec, (byte) ' ');
        Arrays.fill(LocalAddress, (byte) ' ');
        Arrays.fill(CertificateLabel, (byte) ' ');

        // Initialize compression lists with MQCOMPRESS_NONE (0)
        Arrays.fill(HdrCompList, 0);
        Arrays.fill(MsgCompList, 0);
    }

    /**
     * Sets the channel name
     *
     * @param name The channel name to set (max 20 characters)
     */
    public void setChannelName(String name) {
        Arrays.fill(ChannelName, (byte) ' ');
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(nameBytes, 0, ChannelName, 0, Math.min(nameBytes.length, ChannelName.length));
    }

    /**
     * Sets the connection name (host:port format)
     *
     * @param connName The connection name to set (max 264 characters)
     */
    public void setConnectionName(String connName) {
        Arrays.fill(ConnectionName, (byte) ' ');
        byte[] connBytes = connName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(connBytes, 0, ConnectionName, 0, Math.min(connBytes.length, ConnectionName.length));
    }

    public void setUser(String user) {
        Arrays.fill(UserIdentifier, (byte) ' ');
        byte[] userBytes = user.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(userBytes, 0, UserIdentifier, 0, Math.min(userBytes.length, UserIdentifier.length));
    }

    public void setPassword(String password) {
        Arrays.fill(Password, (byte) ' ');
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(passwordBytes, 0, Password, 0, Math.min(passwordBytes.length, Password.length));
    }
}