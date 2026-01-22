package com.aquila.mq.jna.lib;

import static com.aquila.mq.jna.lib.PCFConstants.getQueueTypeName;

/**
 * QueueInfo - Holds information about an MQ queue
 */
public class QueueInfo {

    private String name;
    private int type;
    private String description;
    private int currentDepth;
    private int maxDepth;
    private int maxMsgLength;
    private int openInputCount;
    private int openOutputCount;
    private String baseQName;        // For alias queues
    private String remoteQName;      // For remote queues
    private String remoteQMgrName;   // For remote queues
    private String clusterName;      // For cluster queues

    public QueueInfo() {
    }

    public QueueInfo(String name) {
        this.name = name;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return getQueueTypeName(type);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxMsgLength() {
        return maxMsgLength;
    }

    public void setMaxMsgLength(int maxMsgLength) {
        this.maxMsgLength = maxMsgLength;
    }

    public int getOpenInputCount() {
        return openInputCount;
    }

    public void setOpenInputCount(int openInputCount) {
        this.openInputCount = openInputCount;
    }

    public int getOpenOutputCount() {
        return openOutputCount;
    }

    public void setOpenOutputCount(int openOutputCount) {
        this.openOutputCount = openOutputCount;
    }

    public String getBaseQName() {
        return baseQName;
    }

    public void setBaseQName(String baseQName) {
        this.baseQName = baseQName;
    }

    public String getRemoteQName() {
        return remoteQName;
    }

    public void setRemoteQName(String remoteQName) {
        this.remoteQName = remoteQName;
    }

    public String getRemoteQMgrName() {
        return remoteQMgrName;
    }

    public void setRemoteQMgrName(String remoteQMgrName) {
        this.remoteQMgrName = remoteQMgrName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-48s %-8s", name, getTypeName()));

        if (currentDepth >= 0) {
            sb.append(String.format(" Depth: %d/%d", currentDepth, maxDepth));
        }

        if (description != null && !description.isEmpty()) {
            sb.append(" [").append(description).append("]");
        }

        if (baseQName != null && !baseQName.isEmpty()) {
            sb.append(" -> ").append(baseQName);
        }

        if (remoteQName != null && !remoteQName.isEmpty()) {
            sb.append(" -> ").append(remoteQMgrName).append("/").append(remoteQName);
        }

        return sb.toString();
    }
}
