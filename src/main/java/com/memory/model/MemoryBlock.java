package com.memory.model;

public class MemoryBlock {
    private int size;
    private int startAddress;
    private boolean allocated;
    private String processId;

    public MemoryBlock(int size, int startAddress) {
        this.size = size;
        this.startAddress = startAddress;
        this.allocated = false;
        this.processId = null;
    }

    // Getters and setters
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getStartAddress() { return startAddress; }
    public void setStartAddress(int startAddress) { this.startAddress = startAddress; }

    public boolean isAllocated() { return allocated; }
    public void setAllocated(boolean allocated) { this.allocated = allocated; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    @Override
    public String toString() {
        return String.format("Block[addr=%d, size=%d, %s, process=%s]",
                startAddress, size, allocated ? "allocated" : "free", processId);
    }
}