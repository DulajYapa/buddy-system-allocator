package com.memory.model;

import java.util.*;

public class BuddySystem {
    private final int totalSize;
    private final Map<Integer, List<MemoryBlock>> freeList;
    private final Map<Integer, MemoryBlock> allocatedBlocks;
    private int totalAllocated;
    private int peakMemoryUsage;
    private int allocationCount;
    private int fragmentationCount;

    public BuddySystem(int totalSize) {
        if (!isPowerOfTwo(totalSize)) {
            throw new IllegalArgumentException("Total size must be a power of 2");
        }
        this.totalSize = totalSize;
        this.freeList = new HashMap<>();
        this.allocatedBlocks = new HashMap<>();
        this.totalAllocated = 0;
        this.peakMemoryUsage = 0;
        this.allocationCount = 0;
        this.fragmentationCount = 0;
        initializeMemory();
    }

    private void initializeMemory() {
        MemoryBlock initialBlock = new MemoryBlock(totalSize, 0);
        freeList.put(totalSize, new ArrayList<>());
        freeList.get(totalSize).add(initialBlock);
    }

    private boolean isPowerOfTwo(int n) {
        return n > 0 && ((n & (n - 1)) == 0);
    }

    private int findSmallestSuitableBlock(int size) {
        int blockSize = 1;
        while (blockSize < size) {
            blockSize *= 2;
        }
        return blockSize;
    }

    public Integer allocate(int size, String processId) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }

        int blockSize = findSmallestSuitableBlock(size);
        if (blockSize > totalSize) {
            return null;
        }

        MemoryBlock block = findAndSplitBlock(blockSize);
        if (block == null) {
            return null;
        }

        block.setAllocated(true);
        block.setProcessId(processId);
        allocatedBlocks.put(block.getStartAddress(), block);

        totalAllocated += block.getSize();
        peakMemoryUsage = Math.max(peakMemoryUsage, totalAllocated);
        allocationCount++;
        updateFragmentationCount();

        return block.getStartAddress();
    }

    private MemoryBlock findAndSplitBlock(int requestedSize) {
        int currentSize = requestedSize;
        while (currentSize <= totalSize) {
            List<MemoryBlock> blocks = freeList.get(currentSize);
            if (blocks != null && !blocks.isEmpty()) {
                MemoryBlock block = blocks.remove(0);
                while (block.getSize() > requestedSize) {
                    splitBlock(block);
                    block = freeList.get(block.getSize() / 2).remove(0);
                }
                return block;
            }
            currentSize *= 2;
        }
        return null;
    }

    private void splitBlock(MemoryBlock block) {
        int newSize = block.getSize() / 2;
        int newAddress = block.getStartAddress() + newSize;

        MemoryBlock buddy1 = new MemoryBlock(newSize, block.getStartAddress());
        MemoryBlock buddy2 = new MemoryBlock(newSize, newAddress);

        freeList.computeIfAbsent(newSize, k -> new ArrayList<>()).add(buddy1);
        freeList.computeIfAbsent(newSize, k -> new ArrayList<>()).add(buddy2);
    }

    public boolean deallocate(int address) {
        MemoryBlock block = allocatedBlocks.remove(address);
        if (block == null) {
            return false;
        }

        totalAllocated -= block.getSize();
        block.setAllocated(false);
        block.setProcessId(null);

        mergeBlocks(block);
        updateFragmentationCount();
        return true;
    }

    private void mergeBlocks(MemoryBlock block) {
        int currentSize = block.getSize();
        while (currentSize < totalSize) {
            int buddyAddress = block.getStartAddress() ^ currentSize;
            MemoryBlock buddy = findBuddy(currentSize, buddyAddress);

            if (buddy == null) {
                freeList.computeIfAbsent(currentSize, k -> new ArrayList<>()).add(block);
                break;
            }

            freeList.get(currentSize).remove(buddy);
            int newAddress = Math.min(block.getStartAddress(), buddyAddress);
            block = new MemoryBlock(currentSize * 2, newAddress);
            currentSize *= 2;
        }

        if (currentSize == totalSize) {
            freeList.computeIfAbsent(currentSize, k -> new ArrayList<>()).add(block);
        }
    }

    private MemoryBlock findBuddy(int size, int address) {
        List<MemoryBlock> blocks = freeList.get(size);
        if (blocks != null) {
            for (MemoryBlock block : blocks) {
                if (block.getStartAddress() == address) {
                    return block;
                }
            }
        }
        return null;
    }

    private void updateFragmentationCount() {
        fragmentationCount = 0;
        for (List<MemoryBlock> blocks : freeList.values()) {
            fragmentationCount += blocks.size();
        }
    }

    // Getters for statistics
    public int getTotalSize() { return totalSize; }
    public int getTotalAllocated() { return totalAllocated; }
    public int getFreeMemory() { return totalSize - totalAllocated; }
    public int getPeakMemoryUsage() { return peakMemoryUsage; }
    public int getAllocationCount() { return allocationCount; }
    public int getFragmentationCount() { return fragmentationCount; }

    public List<MemoryBlock> getAllBlocks() {
        List<MemoryBlock> allBlocks = new ArrayList<>(allocatedBlocks.values());
        for (List<MemoryBlock> blocks : freeList.values()) {
            allBlocks.addAll(blocks);
        }
        allBlocks.sort(Comparator.comparingInt(MemoryBlock::getStartAddress));
        return allBlocks;
    }

    public Map<String, MemoryBlock> getAllocatedBlocksByProcess() {
        Map<String, MemoryBlock> blocksByProcess = new HashMap<>();
        for (MemoryBlock block : allocatedBlocks.values()) {
            if (block.getProcessId() != null) {
                blocksByProcess.put(block.getProcessId(), block);
            }
        }
        return blocksByProcess;
    }
}