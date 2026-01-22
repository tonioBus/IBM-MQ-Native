package com.aquila.mq.jna.lib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.aquila.mq.jna.lib.PCFConstants.MQCFT_INTEGER;

/**
 * MQCFIN - PCF Integer Parameter Structure
 * Based on cmqcfc.h
 * Note: PCF structures do NOT have a StrucId field - they start with Type.
 */
public class MQCFIN {

    // Size: Type(4) + StrucLength(4) + Parameter(4) + Value(4) = 16 bytes
    public static final int MQCFIN_SIZE = 16;

    public int Type = MQCFT_INTEGER;        // Structure type
    public int StrucLength = MQCFIN_SIZE;   // Structure length
    public int Parameter;                    // Parameter identifier
    public int Value;                        // Parameter value

    public MQCFIN() {
    }

    /**
     * Create an integer parameter
     */
    public static MQCFIN create(int parameter, int value) {
        MQCFIN param = new MQCFIN();
        param.Parameter = parameter;
        param.Value = value;
        return param;
    }

    /**
     * Convert to byte array for sending
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(MQCFIN_SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(Type);
        buffer.putInt(StrucLength);
        buffer.putInt(Parameter);
        buffer.putInt(Value);
        return buffer.array();
    }

    /**
     * Parse from byte array
     */
    public static MQCFIN fromBytes(byte[] data, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, MQCFIN_SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);

        MQCFIN param = new MQCFIN();
        param.Type = buffer.getInt();
        param.StrucLength = buffer.getInt();
        param.Parameter = buffer.getInt();
        param.Value = buffer.getInt();

        return param;
    }

    @Override
    public String toString() {
        return String.format("MQCFIN[Parameter=%d, Value=%d]", Parameter, Value);
    }
}
