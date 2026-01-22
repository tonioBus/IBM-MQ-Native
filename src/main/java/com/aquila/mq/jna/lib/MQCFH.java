package com.aquila.mq.jna.lib;

import com.sun.jna.Structure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.aquila.mq.jna.lib.PCFConstants.*;

/**
 * MQCFH - PCF Header Structure
 * Based on cmqcfc.h
 * <p>
 * This structure is the header for all PCF messages.
 * Note: PCF structures do NOT have a StrucId field - they start with Type.
 */
@Structure.FieldOrder({"Type", "StrucLength", "Version", "Command", "MsgSeqNumber",
        "Control", "CompCode", "Reason", "ParameterCount"})
public class MQCFH extends Structure {

    public static final int MQCFH_SIZE = 36; // Size of MQCFH structure (9 * 4 bytes)

    // Structure fields
    public int Type = MQCFT_COMMAND;         // Structure type
    public int StrucLength = MQCFH_SIZE;     // Structure length
    public int Version = MQCFH_VERSION_3;    // Structure version number
    public int Command;                       // Command identifier
    public int MsgSeqNumber = 1;             // Message sequence number
    public int Control = MQCFC_LAST;         // Control options
    public int CompCode = 0;                 // Completion code
    public int Reason = 0;                   // Reason code qualifying CompCode
    public int ParameterCount = 0;           // Count of parameter structures

    public MQCFH() {
        super();
    }

    /**
     * Create a command header
     */
    public static MQCFH createCommand(int command, int parameterCount) {
        MQCFH header = new MQCFH();
        header.Command = command;
        header.ParameterCount = parameterCount;
        return header;
    }

    /**
     * Convert to byte array for sending
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(MQCFH_SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);  // PCF uses network byte order (big-endian)
        buffer.putInt(Type);
        buffer.putInt(StrucLength);
        buffer.putInt(Version);
        buffer.putInt(Command);
        buffer.putInt(MsgSeqNumber);
        buffer.putInt(Control);
        buffer.putInt(CompCode);
        buffer.putInt(Reason);
        buffer.putInt(ParameterCount);
        return buffer.array();
    }

    /**
     * Parse from byte array
     */
    public static MQCFH fromBytes(byte[] data, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, MQCFH_SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);  // PCF uses network byte order (big-endian)

        MQCFH header = new MQCFH();
        header.Type = buffer.getInt();
        header.StrucLength = buffer.getInt();
        header.Version = buffer.getInt();
        header.Command = buffer.getInt();
        header.MsgSeqNumber = buffer.getInt();
        header.Control = buffer.getInt();
        header.CompCode = buffer.getInt();
        header.Reason = buffer.getInt();
        header.ParameterCount = buffer.getInt();

        return header;
    }

    /**
     * Check if this is the last message in a sequence
     */
    public boolean isLast() {
        return Control == MQCFC_LAST;
    }

    /**
     * Check if the command completed successfully
     */
    public boolean isSuccess() {
        return CompCode == 0;
    }

    @Override
    public String toString() {
        return String.format("MQCFH[Type=%d, StrucLength=%d, Version=%d, Command=%d, MsgSeqNumber=%d, Control=%d, " +
                        "CompCode=%d, Reason=%d, ParameterCount=%d]",
                Type, StrucLength, Version, Command, MsgSeqNumber, Control, CompCode, Reason, ParameterCount);
    }
}
