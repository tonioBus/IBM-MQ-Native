package com.aquila.mq.jna.lib;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;

/**
 * MQCSP - MQ Connection Security Parameters
 * Structure for authentication with user ID and password
 * Based on cmqc.h
 */
@Structure.FieldOrder({"StrucId", "Version", "AuthenticationType", "Reserved1",
        "CSPUserIdPtr", "CSPUserIdOffset", "CSPUserIdLength", "Reserved2",
        "CSPPasswordPtr", "CSPPasswordOffset", "CSPPasswordLength"})
public class MQCSP extends Structure {

    // Structure ID
    public static final String MQCSP_STRUC_ID = "CSP ";

    // Version constants
    public static final int MQCSP_VERSION_1 = 1;
    public static final int MQCSP_VERSION_2 = 2;
    public static final int MQCSP_CURRENT_VERSION = MQCSP_VERSION_1;

    // Authentication types
    public static final int MQCSP_AUTH_NONE = 0;
    public static final int MQCSP_AUTH_USER_ID_AND_PWD = 1;

    // Structure fields
    public byte[] StrucId = new byte[4];              // Structure identifier "CSP "
    public int Version = MQCSP_VERSION_1;             // Structure version number
    public int AuthenticationType = MQCSP_AUTH_NONE;  // Authentication type
    public byte[] Reserved1 = new byte[4];            // Reserved

    public Pointer CSPUserIdPtr = null;               // Address of user ID
    public int CSPUserIdOffset = 0;                   // Offset of user ID
    public int CSPUserIdLength = 0;                   // Length of user ID

    public byte[] Reserved2 = new byte[8];            // Reserved

    public Pointer CSPPasswordPtr = null;             // Address of password
    public int CSPPasswordOffset = 0;                 // Offset of password
    public int CSPPasswordLength = 0;                 // Length of password

    // Keep references to prevent GC
    private Memory userIdMemory;
    private Memory passwordMemory;

    public MQCSP() {
        super();
        System.arraycopy(MQCSP_STRUC_ID.getBytes(StandardCharsets.US_ASCII), 0, StrucId, 0, 4);
    }

    /**
     * Set user credentials for authentication
     */
    public void setCredentials(String userId, String password) {
        AuthenticationType = MQCSP_AUTH_USER_ID_AND_PWD;

        // Set user ID
        if (userId != null && !userId.isEmpty()) {
            byte[] userIdBytes = userId.getBytes(StandardCharsets.UTF_8);
            userIdMemory = new Memory(userIdBytes.length);
            userIdMemory.write(0, userIdBytes, 0, userIdBytes.length);
            CSPUserIdPtr = userIdMemory;
            CSPUserIdLength = userIdBytes.length;
        }

        // Set password
        if (password != null && !password.isEmpty()) {
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            passwordMemory = new Memory(passwordBytes.length);
            passwordMemory.write(0, passwordBytes, 0, passwordBytes.length);
            CSPPasswordPtr = passwordMemory;
            CSPPasswordLength = passwordBytes.length;
        }
    }

    public static class ByReference extends MQCSP implements Structure.ByReference {
    }
}
