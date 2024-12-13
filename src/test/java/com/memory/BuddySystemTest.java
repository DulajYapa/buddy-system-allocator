package com.memory;

import com.memory.model.BuddySystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuddySystemTest {
    private BuddySystem buddySystem;
    private static final int TOTAL_MEMORY = 1024;

    @Before
    public void setUp() {
        buddySystem = new BuddySystem(TOTAL_MEMORY);
    }

    @Test
    public void testInitialization() {
        assertEquals(TOTAL_MEMORY, buddySystem.getTotalSize());
        assertEquals(0, buddySystem.getTotalAllocated());
        assertEquals(TOTAL_MEMORY, buddySystem.getFreeMemory());
    }

    @Test
    public void testSimpleAllocation() {
        Integer address = buddySystem.allocate(256, "P1");
        assertNotNull(address);
        assertEquals(256, buddySystem.getTotalAllocated());
        assertEquals(TOTAL_MEMORY - 256, buddySystem.getFreeMemory());
    }

    @Test
    public void testMultipleAllocations() {
        Integer addr1 = buddySystem.allocate(256, "P1");
        Integer addr2 = buddySystem.allocate(128, "P2");

        assertNotNull(addr1);
        assertNotNull(addr2);
        assertEquals(384, buddySystem.getTotalAllocated());
    }

    @Test
    public void testDeallocation() {
        Integer address = buddySystem.allocate(256, "P1");
        assertTrue(buddySystem.deallocate(address));
        assertEquals(0, buddySystem.getTotalAllocated());
        assertEquals(TOTAL_MEMORY, buddySystem.getFreeMemory());
    }

    @Test
    public void testAllocationFailure() {
        Integer addr1 = buddySystem.allocate(512, "P1");
        Integer addr2 = buddySystem.allocate(512, "P2");
        Integer addr3 = buddySystem.allocate(256, "P3");

        assertNotNull(addr1);
        assertNotNull(addr2);
        assertNull(addr3);
    }

    @Test
    public void testFragmentation() {
        Integer addr1 = buddySystem.allocate(256, "P1");
        Integer addr2 = buddySystem.allocate(128, "P2");
        Integer addr3 = buddySystem.allocate(256, "P3");

        assertTrue(buddySystem.deallocate(addr2));
        Integer addr4 = buddySystem.allocate(64, "P4");
        assertNotNull(addr4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSize() {
        buddySystem.allocate(-1, "P1");
    }

    @Test
    public void testPeakUsage() {
        Integer addr1 = buddySystem.allocate(512, "P1");
        assertEquals(512, buddySystem.getPeakMemoryUsage());
        Integer addr2 = buddySystem.allocate(256, "P2");
        assertEquals(768, buddySystem.getPeakMemoryUsage());
        buddySystem.deallocate(addr1);
        assertEquals(768, buddySystem.getPeakMemoryUsage());
    }

    @Test
    public void testAllocationCount() {
        int initialCount = buddySystem.getAllocationCount();
        buddySystem.allocate(256, "P1");
        buddySystem.allocate(128, "P2");
        assertEquals(initialCount + 2, buddySystem.getAllocationCount());
    }
}