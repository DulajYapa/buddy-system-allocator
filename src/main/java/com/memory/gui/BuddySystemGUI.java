package com.memory.gui;

import com.memory.model.BuddySystem;
import com.memory.model.MemoryBlock;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Map;

public class BuddySystemGUI extends JFrame {
    private final BuddySystem buddySystem;
    private JTextField sizeField;
    private JTextField processIdField;
    private JTextField addressField;
    private JTextArea memoryMapArea;
    private DefaultTableModel tableModel;
    private JPanel statisticsPanel;

    // GUI Constants
    private static final Color PANEL_BACKGROUND = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(60, 63, 65);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);

    public BuddySystemGUI(int totalMemorySize) {
        buddySystem = new BuddySystem(totalMemorySize);
        initializeGUI();
        customizeUI();
    }

    private void initializeGUI() {
        setTitle("Buddy System Memory Allocator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create main panels
        JPanel leftPanel = createLeftPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel bottomPanel = createBottomPanel();

        // Add panels to frame
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Set frame properties
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        // Add allocation panel
        JPanel allocationPanel = createAllocationPanel();
        leftPanel.add(allocationPanel);
        leftPanel.add(Box.createVerticalStrut(10));

        // Add deallocation panel
        JPanel deallocationPanel = createDeallocationPanel();
        leftPanel.add(deallocationPanel);
        leftPanel.add(Box.createVerticalStrut(10));

        // Add statistics panel
        statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));
        statisticsPanel.setBorder(createTitledBorder("Statistics"));
        leftPanel.add(statisticsPanel);

        updateStatistics();
        return leftPanel;
    }

    private JPanel createAllocationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createTitledBorder("Allocate Memory"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Size input
        addLabelAndField(panel, "Size:", sizeField = new JTextField(10), gbc, 0);

        // Process ID input
        addLabelAndField(panel, "Process ID:", processIdField = new JTextField(10), gbc, 1);

        // Allocate button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton allocateButton = createStyledButton("Allocate");
        allocateButton.addActionListener(e -> handleAllocation());
        panel.add(allocateButton, gbc);

        return panel;
    }

    private JPanel createDeallocationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createTitledBorder("Deallocate Memory"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Address input
        addLabelAndField(panel, "Address:", addressField = new JTextField(10), gbc, 0);

        // Deallocate button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton deallocateButton = createStyledButton("Deallocate");
        deallocateButton.addActionListener(e -> handleDeallocation());
        panel.add(deallocateButton, gbc);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        // Memory Map Header
        JLabel headerLabel = new JLabel("Memory Map");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        centerPanel.add(headerLabel, BorderLayout.NORTH);

        // Memory Map Area
        memoryMapArea = new JTextArea();
        memoryMapArea.setEditable(false);
        memoryMapArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        memoryMapArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(memoryMapArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        updateMemoryMap();
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Create table
        String[] columnNames = {"Address", "Size", "Process ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable allocationTable = new JTable(tableModel);
        styleTable(allocationTable);

        JScrollPane tableScrollPane = new JScrollPane(allocationTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 150));
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);

        return bottomPanel;
    }

    private void handleAllocation() {
        try {
            int size = Integer.parseInt(sizeField.getText());
            String processId = processIdField.getText().trim();

            if (processId.isEmpty()) {
                showError("Please enter a Process ID");
                return;
            }

            Integer address = buddySystem.allocate(size, processId);
            if (address != null) {
                showSuccess("Memory allocated at address: " + address);
                updateGUI();
            } else {
                showError("Failed to allocate memory - not enough space");
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid size");
        }
    }

    private void handleDeallocation() {
        try {
            int address = Integer.parseInt(addressField.getText());

            if (buddySystem.deallocate(address)) {
                showSuccess("Memory deallocated successfully");
                updateGUI();
            } else {
                showError("Failed to deallocate memory - invalid address");
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid address");
        }
    }

    private void updateGUI() {
        updateMemoryMap();
        updateStatistics();
        updateAllocationTable();
        clearInputFields();
    }

    private void updateMemoryMap() {
        StringBuilder sb = new StringBuilder();
        for (MemoryBlock block : buddySystem.getAllBlocks()) {
            sb.append(String.format("Address: %6d | Size: %6d | Status: %s%n",
                    block.getStartAddress(),
                    block.getSize(),
                    block.isAllocated() ?
                            "Allocated (Process: " + block.getProcessId() + ")" :
                            "Free"));
        }
        memoryMapArea.setText(sb.toString());
    }

    private void updateStatistics() {
        statisticsPanel.removeAll();

        addStatisticLabel("Total Memory: " + buddySystem.getTotalSize());
        addStatisticLabel("Allocated Memory: " + buddySystem.getTotalAllocated());
        addStatisticLabel("Free Memory: " + buddySystem.getFreeMemory());
        addStatisticLabel("Peak Usage: " + buddySystem.getPeakMemoryUsage());
        addStatisticLabel("Allocation Count: " + buddySystem.getAllocationCount());
        addStatisticLabel("Fragmentation Count: " + buddySystem.getFragmentationCount());

        statisticsPanel.revalidate();
        statisticsPanel.repaint();
    }

    private void updateAllocationTable() {
        tableModel.setRowCount(0);
        Map<String, MemoryBlock> allocations = buddySystem.getAllocatedBlocksByProcess();

        for (MemoryBlock block : allocations.values()) {
            tableModel.addRow(new Object[]{
                    block.getStartAddress(),
                    block.getSize(),
                    block.getProcessId()
            });
        }
    }

    // Utility methods
    private void addLabelAndField(JPanel panel, String labelText, JTextField field,
                                  GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addStatisticLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MAIN_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        statisticsPanel.add(label);
    }

    private void clearInputFields() {
        sizeField.setText("");
        processIdField.setText("");
        addressField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        return button;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                title);
        border.setTitleFont(HEADER_FONT);
        return border;
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(HEADER_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setFont(MAIN_FONT);
        table.setRowHeight(25);
    }

    private void customizeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BuddySystemGUI gui = new BuddySystemGUI(1024);
            gui.setVisible(true);
        });
    }
}