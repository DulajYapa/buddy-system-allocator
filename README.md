# Buddy System Memory Allocator
## EEX5563/EEX5564 – Computer Architecture and Operating Systems
Registration Number: 321428512

## Overview
This project implements a Buddy System Memory Allocation algorithm with a graphical user interface. The implementation demonstrates memory management techniques used in operating systems, with a focus on efficient memory allocation and deallocation while minimizing fragmentation.

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [Technical Requirements](#technical-requirements)
- [Installation](#installation)
- [Usage Guide](#usage-guide)
- [Testing Instructions](#testing-instructions)
- [Implementation Details](#implementation-details)
- [Author](#author)
- [License](#license)

## Features
- Dynamic memory allocation using the Buddy System algorithm
- Real-time visualization of memory states
- Process-based memory management
- Memory allocation/deallocation statistics
- Graphical user interface for interaction
- Comprehensive error handling

## Project Structure
```
buddy-system-allocator/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── memory/
│   │   │           ├── model/
│   │   │           │   ├── MemoryBlock.java
│   │   │           │   └── BuddySystem.java
│   │   │           └── gui/
│   │   │               └── BuddySystemGUI.java
│   └── test/
│       └── java/
│           └── com/
│               └── memory/
│                   └── BuddySystemTest.java
├── pom.xml
└── README.md
```

## Technical Requirements
- Java Development Kit (JDK) 8 or higher
- Maven 3.6 or higher
- Minimum 512MB RAM
- Display resolution: 1024x768 or higher

## Installation
1. Clone the repository:
```bash
https://github.com/DulajYapa/buddy-system-allocator.git
cd buddy-system-allocator
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
java -jar target/buddy-system-allocator-1.0.jar
```

## Usage Guide

### 1. Allocating Memory
- Enter memory size in bytes
- Provide a unique Process ID
- Click "Allocate" button
- System will display the allocated memory address

### 2. Deallocating Memory
- Enter the memory address
- Click "Deallocate" button
- System will free the memory block

### 3. Monitoring Memory
- View memory map in center panel
- Check statistics on left panel
- Monitor allocations in bottom table

## Testing Instructions

### Quick Start Testing
```bash
mvn test
```

### Manual Testing Guide

#### 1. Basic Allocation Testing
```
Step 1: Launch the application
Step 2: Enter values:
- Size: 256
- Process ID: P1
Step 3: Click "Allocate"
Expected Result: Success message with allocated address
```

#### 2. Sequential Allocation Testing
```
Test Sequence:
1. Allocate 256 bytes for "Process1"
2. Allocate 128 bytes for "Process2"
3. Allocate 512 bytes for "Process3"

Expected Results:
- First allocation: Success (address will be 0)
- Second allocation: Success (address will be 256)
- Third allocation: Failure (insufficient space)
```

#### 3. Edge Case Testing
```
Test Cases:
a) Negative Size:
   - Enter: -100
   Expected: Error message

b) Zero Size:
   - Enter: 0
   Expected: Error message

c) Empty Process ID:
   - Leave Process ID blank
   Expected: Error message
```

### Automated Test Cases
```java
@Test
public void testSimpleAllocation() {
    Integer address = buddySystem.allocate(256, "P1");
    assertNotNull(address);
    assertEquals(256, buddySystem.getTotalAllocated());
}
```

### Test Environment Setup
```bash
# Clean and build
mvn clean install

# Run specific test class
mvn test -Dtest=BuddySystemTest

# Generate test reports
mvn surefire-report:report
```

## Implementation Details

### Key Components

1. MemoryBlock
- Represents individual memory blocks
- Tracks size, address, and allocation status
- Manages process associations

2. BuddySystem
- Implements core allocation algorithm
- Manages block splitting and merging
- Maintains memory state

3. BuddySystemGUI
- Provides visual interface
- Handles user interactions
- Displays memory status

### Performance Characteristics
- Memory Operations: O(log n)
- Space Efficiency: Low fragmentation
- Response Time: < 100ms for operations

### Known Limitations
- Memory size must be power of 2
- Internal fragmentation due to power-of-2 sizing
- Single-threaded operations

## Author
- Registration Number: 321428512
- Course: EEX5563/EEX5564
- Academic Year: 2023/2024
- University: The Open University of Sri Lanka

## License
This project is licensed under the MIT License - see the LICENSE file for details.

---
**Note**: This project is part of the Computer Architecture and Operating Systems course assignment.
