package com.aquila.mq.jna.lib;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.aquila.mq.jna.lib.PCFConstants.MQCFT_STRING;

/**
 * MQCFST - PCF String Parameter Structure
 * Based on cmqcfc.h
 */
@Slf4j
public class MQCFST {

    // Header size: Type(4) + StrucLength(4) + Parameter(4) + CodedCharSetId(4) + StringLength(4) = 20 bytes
    public static final int MQCFST_HEADER_SIZE = 20;

    public int Type = MQCFT_STRING;         // Structure type
    public int StrucLength;                  // Structure length
    public int Parameter;                    // Parameter identifier
    public int CodedCharSetId = 0;          // Coded character set identifier
    public int StringLength;                 // Length of string
    public byte[] StringData;                // String value (variable length)

    public MQCFST() {
    }

    /**
     * Create a string parameter
     */
    public static MQCFST create(int parameter, String value) {
        MQCFST param = new MQCFST();
        param.Parameter = parameter;

        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        // Pad to 4-byte boundary
        int paddedLength = ((valueBytes.length + 3) / 4) * 4;
        param.StringData = new byte[paddedLength];
        Arrays.fill(param.StringData, (byte) ' ');
        System.arraycopy(valueBytes, 0, param.StringData, 0, valueBytes.length);

        param.StringLength = valueBytes.length;
        param.StrucLength = MQCFST_HEADER_SIZE + paddedLength;

        return param;
    }

    /**
     * Convert to byte array for sending
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(StrucLength);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(Type);
        buffer.putInt(StrucLength);
        buffer.putInt(Parameter);
        buffer.putInt(CodedCharSetId);
        buffer.putInt(StringLength);
        buffer.put(StringData);
        return buffer.array();
    }

    /**
     * Parse from byte array
     */
    public static MQCFST fromBytes(byte[] data, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, data.length - offset);
        buffer.order(ByteOrder.BIG_ENDIAN);

        MQCFST param = new MQCFST();
        param.Type = buffer.getInt();
        param.StrucLength = buffer.getInt();
        param.Parameter = buffer.getInt();
        param.CodedCharSetId = buffer.getInt();
        param.StringLength = buffer.getInt();

        int stringDataLength = param.StrucLength - MQCFST_HEADER_SIZE;
        param.StringData = new byte[stringDataLength];
        buffer.get(param.StringData);

        return param;
    }

    /**
     * Get the string value (trimmed)
     */
    public String getStringValue() {
        if (StringData == null || StringLength == 0) {
            return "";
        }
        return new String(StringData, 0, StringLength, StandardCharsets.UTF_8).trim();
    }

    @Override
    public String toString() {
        return java.lang.String.format("MQCFST[Parameter=%d, Value='%s']", Parameter, getStringValue());
    }
}
