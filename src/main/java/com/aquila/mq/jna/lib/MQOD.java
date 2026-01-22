package com.aquila.mq.jna.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * MQOD - MQ Object Descriptor
 * Structure to specify the object to open
 * Based on cmqc.h
 */
@Structure.FieldOrder({"StrucId", "Version", "ObjectType", "ObjectName",
        "ObjectQMgrName", "DynamicQName", "AlternateUserId", "RecsPresent",
        "KnownDestCount", "UnknownDestCount", "InvalidDestCount",
        "ObjectRecOffset", "ResponseRecOffset", "ObjectRecPtr",
        "ResponseRecPtr", "AlternateSecurityId", "ResolvedQName",
        "ResolvedQMgrName", "ObjectString", "SelectionString", "ResObjectString",
        "ResolvedType"})
public class MQOD extends Structure {

    // Structure ID
    public static final String MQOD_STRUC_ID = "OD  ";

    // Version constants
    public static final int MQOD_VERSION_1 = 1;
    public static final int MQOD_VERSION_2 = 2;
    public static final int MQOD_VERSION_3 = 3;
    public static final int MQOD_VERSION_4 = 4;
    public static final int MQOD_CURRENT_VERSION = MQOD_VERSION_4;

    // Object types
    public static final int MQOT_Q = 1;
    public static final int MQOT_NAMELIST = 2;
    public static final int MQOT_PROCESS = 3;
    public static final int MQOT_Q_MGR = 5;
    public static final int MQOT_CHANNEL = 6;
    public static final int MQOT_AUTH_INFO = 7;
    public static final int MQOT_TOPIC = 8;

    // Structure fields - Version 1
    public byte[] StrucId = new byte[4];              // Structure identifier
    public int Version = MQOD_VERSION_4;              // Structure version number
    public int ObjectType = MQOT_Q;                   // Object type
    public byte[] ObjectName = new byte[48];          // Object name
    public byte[] ObjectQMgrName = new byte[48];      // Object queue manager name
    public byte[] DynamicQName = new byte[48];        // Dynamic queue name
    public byte[] AlternateUserId = new byte[12];     // Alternate user identifier

    // Version 2 fields
    public int RecsPresent = 0;                       // Number of object records present
    public int KnownDestCount = 0;                    // Number of local queues opened successfully
    public int UnknownDestCount = 0;                  // Number of remote queues opened
    public int InvalidDestCount = 0;                  // Number of queues that failed to open
    public int ObjectRecOffset = 0;                   // Offset of first object record
    public int ResponseRecOffset = 0;                 // Offset of first response record
    public Pointer ObjectRecPtr = null;               // Address of first object record
    public Pointer ResponseRecPtr = null;             // Address of first response record

    // Version 3 fields
    public byte[] AlternateSecurityId = new byte[40]; // Alternate security identifier
    public byte[] ResolvedQName = new byte[48];       // Resolved queue name
    public byte[] ResolvedQMgrName = new byte[48];    // Resolved queue manager name

    // Version 4 fields
    public MQCharV ObjectString = new MQCharV();      // Object long name
    public MQCharV SelectionString = new MQCharV();   // Selection string
    public MQCharV ResObjectString = new MQCharV();   // Resolved long object name
    public int ResolvedType = 0;                      // Resolved object type

    /**
     * MQCHARV structure for variable-length strings
     */
    @Structure.FieldOrder({"VSPtr", "VSOffset", "VSBufSize", "VSLength", "VSCCSID"})
    public static class MQCharV extends Structure {
        public Pointer VSPtr = null;
        public int VSOffset = 0;
        public int VSBufSize = 0;
        public int VSLength = 0;
        public int VSCCSID = -3;  // MQCCSI_APPL

        public MQCharV() {
            super();
        }
    }

    public MQOD() {
        super();
        System.arraycopy(MQOD_STRUC_ID.getBytes(StandardCharsets.US_ASCII), 0, StrucId, 0, 4);
        Arrays.fill(ObjectName, (byte) ' ');
        Arrays.fill(ObjectQMgrName, (byte) ' ');
        Arrays.fill(DynamicQName, (byte) ' ');
        Arrays.fill(AlternateUserId, (byte) ' ');
        Arrays.fill(AlternateSecurityId, (byte) 0);
        Arrays.fill(ResolvedQName, (byte) ' ');
        Arrays.fill(ResolvedQMgrName, (byte) ' ');

        // Set default dynamic queue name pattern
        setDynamicQName("AMQ.*");
    }

    /**
     * Set the object name (queue name)
     */
    public void setObjectName(String name) {
        Arrays.fill(ObjectName, (byte) ' ');
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(nameBytes, 0, ObjectName, 0, Math.min(nameBytes.length, ObjectName.length));
    }

    /**
     * Set the dynamic queue name pattern
     */
    public void setDynamicQName(String name) {
        Arrays.fill(DynamicQName, (byte) ' ');
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(nameBytes, 0, DynamicQName, 0, Math.min(nameBytes.length, DynamicQName.length));
    }

    /**
     * Get the resolved queue name
     */
    public String getResolvedQName() {
        return new String(ResolvedQName, StandardCharsets.UTF_8).trim();
    }

    /**
     * Convert to byte array for MQOPEN
     */
    public byte[] toBytes() {
        write();
        return getPointer().getByteArray(0, size());
    }
}
