package com.aquila.mq.jna.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;

import static com.ibm.mq.constants.CMQC.MQCNO_CURRENT_VERSION;

/**
 * MQCNO - MQ Connection Options
 * Structure to specify connection options for MQCONNX
 * Based on MQCNO structure from cmqc.h
 *
 * This structure supports versions 1-8 of the MQCNO structure.
 * Fields are ordered to match the C structure definition exactly.
 */
@Structure.FieldOrder({"StrucId", "Version", "Options", "ClientConnOffset",
                        "ClientConnPtr", "ConnTag", "SSLConfigPtr", "SSLConfigOffset",
                        "ConnectionId", "SecurityParmsOffset", "SecurityParmsPtr",
                        "CCDTUrlPtr", "CCDTUrlOffset", "CCDTUrlLength", "Reserved",
                        "ApplName", "Reserved2", "BalanceParmsPtr", "BalanceParmsOffset",
                        "Reserved3"})
public class MQCNO extends Structure {

    // Structure Identifier
    public static final String MQCNO_STRUC_ID = "CNO ";

    // Options that control the action of MQCONNX
    public static final int MQCNO_NONE = 0x00000000;
    public static final int MQCNO_STANDARD_BINDING = 0x00000000;
    public static final int MQCNO_FASTPATH_BINDING = 0x00000001;
    public static final int MQCNO_SERIALIZE_CONN_TAG_Q_MGR = 0x00000002;
    public static final int MQCNO_SERIALIZE_CONN_TAG_QSG = 0x00000004;
    public static final int MQCNO_RESTRICT_CONN_TAG_Q_MGR = 0x00000008;
    public static final int MQCNO_RESTRICT_CONN_TAG_QSG = 0x00000010;
    public static final int MQCNO_HANDLE_SHARE_NONE = 0x00000020;
    public static final int MQCNO_HANDLE_SHARE_BLOCK = 0x00000040;
    public static final int MQCNO_HANDLE_SHARE_NO_BLOCK = 0x00000080;
    public static final int MQCNO_SHARED_BINDING = 0x00000100;
    public static final int MQCNO_ISOLATED_BINDING = 0x00000200;
    public static final int MQCNO_LOCAL_BINDING = 0x00000400;
    public static final int MQCNO_CLIENT_BINDING = 0x00000800;
    public static final int MQCNO_ACCOUNTING_MQI_ENABLED = 0x00001000;
    public static final int MQCNO_ACCOUNTING_MQI_DISABLED = 0x00002000;
    public static final int MQCNO_ACCOUNTING_Q_ENABLED = 0x00004000;
    public static final int MQCNO_ACCOUNTING_Q_DISABLED = 0x00008000;
    public static final int MQCNO_NO_CONV_SHARING = 0x00010000;
    public static final int MQCNO_ALL_CONVS_SHARE = 0x00040000;
    public static final int MQCNO_CD_FOR_OUTPUT_ONLY = 0x00080000;
    public static final int MQCNO_USE_CD_SELECTION = 0x00100000;
    public static final int MQCNO_GENERATE_CONN_TAG = 0x00200000;
    public static final int MQCNO_RECONNECT_AS_DEF = 0x00000000;
    public static final int MQCNO_RECONNECT = 0x01000000;
    public static final int MQCNO_RECONNECT_DISABLED = 0x02000000;
    public static final int MQCNO_RECONNECT_Q_MGR = 0x04000000;
    public static final int MQCNO_ACTIVITY_TRACE_ENABLED = 0x08000000;
    public static final int MQCNO_ACTIVITY_TRACE_DISABLED = 0x10000000;

    // Structure fields - must match the C structure layout exactly
    // Version 1 fields
    public byte[] StrucId = new byte[4];              // Structure identifier (MQCHAR4)
    public int Version = MQCNO_CURRENT_VERSION;       // Structure version number
    public int Options = MQCNO_NONE;                  // Options that control the action of MQCONNX

    // Version 1 fields (continued)
    public int ClientConnOffset = 0;                  // Offset of MQCD structure for client connection
    public Pointer ClientConnPtr = null;              // Address of MQCD structure for client connection (PMQCD)

    // Version 2 fields
    public byte[] ConnTag = new byte[128];            // Queue-manager connection tag (MQBYTE128)

    // Version 3 fields
    public Pointer SSLConfigPtr = null;               // Address of MQSCO structure for client connection (PMQSCO)
    public int SSLConfigOffset = 0;                   // Offset of MQSCO structure for client connection

    // Version 4 fields
    public byte[] ConnectionId = new byte[24];        // Unique Connection Identifier (MQBYTE24)
    public int SecurityParmsOffset = 0;               // Offset of MQCSP structure
    public Pointer SecurityParmsPtr = null;           // Address of MQCSP structure (PMQCSP)

    // Version 5 fields
    public Pointer CCDTUrlPtr = null;                 // Address of CCDT URL string (PMQCHAR)
    public int CCDTUrlOffset = 0;                     // Offset of CCDT URL string
    public int CCDTUrlLength = 0;                     // Length of CCDT URL
    public byte[] Reserved = new byte[8];             // Reserved (MQBYTE8)

    // Version 6 fields
    public byte[] ApplName = new byte[28];            // Application name (MQCHAR28)
    public byte[] Reserved2 = new byte[4];            // Reserved (MQBYTE4)

    // Version 7 fields
    public Pointer BalanceParmsPtr = null;            // Balance Parameter Pointer (PMQBNO)
    public int BalanceParmsOffset = 0;                // Balance Parameter Offset
    public byte[] Reserved3 = new byte[4];            // Reserved (MQBYTE4)

    // Version 8 fields
    // (No new fields in version 8)

    public static class ByReference extends MQCNO implements Structure.ByReference {
    };

    public MQCNO() {
        super();
        // Initialize the StrucId with "CNO "
        System.arraycopy(MQCNO_STRUC_ID.getBytes(), 0, StrucId, 0, 4);

        // Initialize all byte arrays
        Arrays.fill(ConnTag, (byte) 0);
        Arrays.fill(ConnectionId, (byte) 0);
        Arrays.fill(Reserved, (byte) 0);
        Arrays.fill(ApplName, (byte) ' ');
        Arrays.fill(Reserved2, (byte) 0);
        Arrays.fill(Reserved3, (byte) 0);
    }

    /**
     * Configures the structure for a client connection with an MQCD
     *
     * @param mqcd The MQCD structure containing client connection details
     */
    public void setClientConnection(MQCD mqcd) {
        this.Options = MQCNO_CLIENT_BINDING;
        this.ClientConnPtr = mqcd.getPointer();
        mqcd.write();  // Write the structure to memory
    }

    /**
     * Configures for a standard connection (local bindings)
     */
    public void setStandardBinding() {
        this.Options = MQCNO_STANDARD_BINDING;
        this.ClientConnPtr = null;
    }
}