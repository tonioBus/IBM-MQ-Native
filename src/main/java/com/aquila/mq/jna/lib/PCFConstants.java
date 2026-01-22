package com.aquila.mq.jna.lib;

/**
 * PCF (Programmable Command Format) Constants for IBM MQ
 * Based on cmqcfc.h
 */
public final class PCFConstants {

    private PCFConstants() {
    }

    // PCF Structure Types
    public static final int MQCFT_COMMAND = 1;
    public static final int MQCFT_RESPONSE = 2;
    public static final int MQCFT_INTEGER = 3;
    public static final int MQCFT_STRING = 4;
    public static final int MQCFT_INTEGER_LIST = 5;
    public static final int MQCFT_STRING_LIST = 6;
    public static final int MQCFT_EVENT = 7;
    public static final int MQCFT_USER = 8;
    public static final int MQCFT_BYTE_STRING = 9;
    public static final int MQCFT_TRACE_ROUTE = 10;
    public static final int MQCFT_REPORT = 12;
    public static final int MQCFT_INTEGER_FILTER = 13;
    public static final int MQCFT_STRING_FILTER = 14;
    public static final int MQCFT_BYTE_STRING_FILTER = 15;
    public static final int MQCFT_COMMAND_XR = 16;
    public static final int MQCFT_XR_MSG = 17;
    public static final int MQCFT_XR_ITEM = 18;
    public static final int MQCFT_XR_SUMMARY = 19;
    public static final int MQCFT_GROUP = 20;
    public static final int MQCFT_STATISTICS = 21;
    public static final int MQCFT_ACCOUNTING = 22;
    public static final int MQCFT_INTEGER64 = 23;
    public static final int MQCFT_INTEGER64_LIST = 25;

    // PCF Commands
    public static final int MQCMD_NONE = 0;
    public static final int MQCMD_CHANGE_Q_MGR = 1;
    public static final int MQCMD_INQUIRE_Q_MGR = 2;
    public static final int MQCMD_CHANGE_PROCESS = 3;
    public static final int MQCMD_COPY_PROCESS = 4;
    public static final int MQCMD_CREATE_PROCESS = 5;
    public static final int MQCMD_DELETE_PROCESS = 6;
    public static final int MQCMD_INQUIRE_PROCESS = 7;
    public static final int MQCMD_CHANGE_Q = 8;
    public static final int MQCMD_CLEAR_Q = 9;
    public static final int MQCMD_COPY_Q = 10;
    public static final int MQCMD_CREATE_Q = 11;
    public static final int MQCMD_DELETE_Q = 12;
    public static final int MQCMD_INQUIRE_Q = 13;
    public static final int MQCMD_REFRESH_Q_MGR = 16;
    public static final int MQCMD_RESET_Q_STATS = 17;
    public static final int MQCMD_INQUIRE_Q_NAMES = 18;
    public static final int MQCMD_INQUIRE_PROCESS_NAMES = 19;
    public static final int MQCMD_INQUIRE_CHANNEL_NAMES = 20;
    public static final int MQCMD_CHANGE_CHANNEL = 21;
    public static final int MQCMD_COPY_CHANNEL = 22;
    public static final int MQCMD_CREATE_CHANNEL = 23;
    public static final int MQCMD_DELETE_CHANNEL = 24;
    public static final int MQCMD_INQUIRE_CHANNEL = 25;
    public static final int MQCMD_PING_CHANNEL = 26;
    public static final int MQCMD_RESET_CHANNEL = 27;
    public static final int MQCMD_START_CHANNEL = 28;
    public static final int MQCMD_STOP_CHANNEL = 29;
    public static final int MQCMD_START_CHANNEL_INIT = 30;
    public static final int MQCMD_START_CHANNEL_LISTENER = 31;
    public static final int MQCMD_CHANGE_NAMELIST = 32;
    public static final int MQCMD_COPY_NAMELIST = 33;
    public static final int MQCMD_CREATE_NAMELIST = 34;
    public static final int MQCMD_DELETE_NAMELIST = 35;
    public static final int MQCMD_INQUIRE_NAMELIST = 36;
    public static final int MQCMD_INQUIRE_NAMELIST_NAMES = 37;
    public static final int MQCMD_ESCAPE = 38;
    public static final int MQCMD_RESOLVE_CHANNEL = 39;
    public static final int MQCMD_PING_Q_MGR = 40;
    public static final int MQCMD_INQUIRE_Q_STATUS = 41;
    public static final int MQCMD_INQUIRE_CHANNEL_STATUS = 42;
    public static final int MQCMD_CONFIG_EVENT = 43;
    public static final int MQCMD_Q_MGR_EVENT = 44;
    public static final int MQCMD_PERFM_EVENT = 45;
    public static final int MQCMD_CHANNEL_EVENT = 46;

    // PCF Completion Codes
    public static final int MQCFC_LAST = 1;
    public static final int MQCFC_NOT_LAST = 0;

    // Queue Attributes
    public static final int MQCA_Q_NAME = 2016;
    public static final int MQCA_Q_DESC = 2013;
    public static final int MQCA_BASE_Q_NAME = 2002;
    public static final int MQCA_CREATION_DATE = 2004;
    public static final int MQCA_CREATION_TIME = 2005;
    public static final int MQCA_INITIATION_Q_NAME = 2008;
    public static final int MQCA_PROCESS_NAME = 2012;
    public static final int MQCA_REMOTE_Q_MGR_NAME = 2017;
    public static final int MQCA_REMOTE_Q_NAME = 2018;
    public static final int MQCA_XMIT_Q_NAME = 2024;
    public static final int MQCA_CLUSTER_NAME = 2029;
    public static final int MQCA_CLUSTER_NAMELIST = 2030;
    public static final int MQCA_TRIGGER_DATA = 2023;
    public static final int MQCA_DEF_XMIT_Q_NAME = 2025;
    public static final int MQCA_BACKOUT_REQ_Q_NAME = 2001;
    public static final int MQCA_CF_STRUC_NAME = 2052;
    public static final int MQCA_STORAGE_CLASS = 2022;

    // Integer Queue Attributes
    public static final int MQIA_Q_TYPE = 20;
    public static final int MQIA_INHIBIT_PUT = 9;
    public static final int MQIA_INHIBIT_GET = 8;
    public static final int MQIA_CURRENT_Q_DEPTH = 3;
    public static final int MQIA_MAX_Q_DEPTH = 15;
    public static final int MQIA_MAX_MSG_LENGTH = 13;
    public static final int MQIA_DEF_PRIORITY = 6;
    public static final int MQIA_DEF_PERSISTENCE = 5;
    public static final int MQIA_OPEN_INPUT_COUNT = 17;
    public static final int MQIA_OPEN_OUTPUT_COUNT = 18;
    public static final int MQIA_TRIGGER_CONTROL = 24;
    public static final int MQIA_TRIGGER_TYPE = 28;
    public static final int MQIA_TRIGGER_MSG_PRIORITY = 26;
    public static final int MQIA_TRIGGER_DEPTH = 29;
    public static final int MQIA_SCOPE = 45;
    public static final int MQIA_USAGE = 12;
    public static final int MQIA_SHAREABILITY = 23;
    public static final int MQIA_DEF_INPUT_OPEN_OPTION = 4;
    public static final int MQIA_HARDEN_GET_BACKOUT = 7;
    public static final int MQIA_MSG_DELIVERY_SEQUENCE = 16;
    public static final int MQIA_RETENTION_INTERVAL = 21;
    public static final int MQIA_BACKOUT_THRESHOLD = 22;
    public static final int MQIA_DIST_LISTS = 34;
    public static final int MQIA_DEF_BIND = 61;
    public static final int MQIA_DEFINITION_TYPE = 7;
    public static final int MQIA_CLUSTER_Q_TYPE = 59;
    public static final int MQIA_Q_DEPTH_HIGH_LIMIT = 40;
    public static final int MQIA_Q_DEPTH_LOW_LIMIT = 41;
    public static final int MQIA_Q_DEPTH_MAX_EVENT = 42;
    public static final int MQIA_Q_DEPTH_HIGH_EVENT = 43;
    public static final int MQIA_Q_DEPTH_LOW_EVENT = 44;
    public static final int MQIA_Q_SERVICE_INTERVAL = 54;
    public static final int MQIA_Q_SERVICE_INTERVAL_EVENT = 46;
    public static final int MQIA_NPM_CLASS = 78;
    public static final int MQIA_MONITORING_Q = 123;
    public static final int MQIA_STATISTICS_Q = 127;
    public static final int MQIA_ACCOUNTING_Q = 133;

    // Queue Types
    public static final int MQQT_LOCAL = 1;
    public static final int MQQT_MODEL = 2;
    public static final int MQQT_ALIAS = 3;
    public static final int MQQT_REMOTE = 6;
    public static final int MQQT_CLUSTER = 7;
    public static final int MQQT_ALL = 1001;

    // PCF Reason Codes
    public static final int MQRCCF_CFH_TYPE_ERROR = 3001;
    public static final int MQRCCF_CFH_LENGTH_ERROR = 3002;
    public static final int MQRCCF_CFH_VERSION_ERROR = 3003;
    public static final int MQRCCF_CFH_MSG_SEQ_NUMBER_ERR = 3004;
    public static final int MQRCCF_CFH_CONTROL_ERROR = 3005;
    public static final int MQRCCF_CFH_PARM_COUNT_ERROR = 3006;
    public static final int MQRCCF_CFH_COMMAND_ERROR = 3007;
    public static final int MQRCCF_COMMAND_FAILED = 3008;
    public static final int MQRCCF_CFIN_LENGTH_ERROR = 3009;
    public static final int MQRCCF_CFST_LENGTH_ERROR = 3010;
    public static final int MQRCCF_CFST_STRING_LENGTH_ERR = 3011;
    public static final int MQRCCF_FORCE_VALUE_ERROR = 3012;
    public static final int MQRCCF_STRUCTURE_TYPE_ERROR = 3013;
    public static final int MQRCCF_CFIN_PARM_ID_ERROR = 3014;
    public static final int MQRCCF_CFST_PARM_ID_ERROR = 3015;
    public static final int MQRCCF_MSG_LENGTH_ERROR = 3016;
    public static final int MQRCCF_CFIN_DUPLICATE_PARM = 3017;
    public static final int MQRCCF_CFST_DUPLICATE_PARM = 3018;
    public static final int MQRCCF_PARM_COUNT_TOO_SMALL = 3019;
    public static final int MQRCCF_PARM_COUNT_TOO_BIG = 3020;
    public static final int MQRCCF_Q_ALREADY_IN_CELL = 3021;
    public static final int MQRCCF_Q_TYPE_ERROR = 3022;
    public static final int MQRCCF_MD_FORMAT_ERROR = 3023;
    public static final int MQRCCF_CFSL_LENGTH_ERROR = 3024;
    public static final int MQRCCF_REPLACE_VALUE_ERROR = 3025;
    public static final int MQRCCF_CFIL_DUPLICATE_VALUE = 3026;
    public static final int MQRCCF_CFIL_COUNT_ERROR = 3027;
    public static final int MQRCCF_CFIL_LENGTH_ERROR = 3028;
    public static final int MQRCCF_MODE_VALUE_ERROR = 3029;
    public static final int MQRCCF_QUIESCE_VALUE_ERROR = 3030;

    // System Queue Names
    public static final String SYSTEM_ADMIN_COMMAND_QUEUE = "SYSTEM.ADMIN.COMMAND.QUEUE";
    public static final String SYSTEM_DEFAULT_MODEL_QUEUE = "SYSTEM.DEFAULT.MODEL.QUEUE";

    // MQCFH Structure ID
    public static final String MQCFH_STRUC_ID = "CFH ";
    public static final int MQCFH_VERSION_1 = 1;
    public static final int MQCFH_VERSION_2 = 2;
    public static final int MQCFH_VERSION_3 = 3;
    public static final int MQCFH_CURRENT_VERSION = MQCFH_VERSION_3;

    // MQCFST Structure ID
    public static final String MQCFST_STRUC_ID = "CFST";

    // MQCFIN Structure ID
    public static final String MQCFIN_STRUC_ID = "CFIN";

    // MQCFIL Structure ID
    public static final String MQCFIL_STRUC_ID = "CFIL";

    /**
     * Get human-readable queue type name
     */
    public static String getQueueTypeName(int queueType) {
        switch (queueType) {
            case MQQT_LOCAL:
                return "Local";
            case MQQT_MODEL:
                return "Model";
            case MQQT_ALIAS:
                return "Alias";
            case MQQT_REMOTE:
                return "Remote";
            case MQQT_CLUSTER:
                return "Cluster";
            default:
                return "Unknown(" + queueType + ")";
        }
    }
}
