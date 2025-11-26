package com.algolab.util;

public class LeftPannelNodeData{
    public enum NodeType {
        FOLDER,
        DATABASE
    }

    private String name;
    private NodeType type;

    public LeftPannelNodeData(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name; // what the TreeView displays
    }
}