import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class PProgramTablePanel extends JPanel {
    private CProgramTable programTable;
    private boolean selectionMode = false;
    private JButton deleteButton;
    private JButton cancelButton;
    private final JPanel buttonsPanel;
    private JButton editButton;
    private JButton sortButton;
    private JComboBox<String> sortByBox;
    private CSearchPanels.ProgramSearchPanel searchPanel;

    public PProgramTablePanel() {
        programTable = new CProgramTable();

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        JPanel searchPanelContainer = new JPanel(new BorderLayout());
        searchPanelContainer.setOpaque(false);
        gbc.gridy = 0;
        gbc.weighty = 0.02;

        searchPanel = new CSearchPanels.ProgramSearchPanel(searchParams -> {
            String searchText = searchParams[0].toLowerCase();
            String columnName = searchParams[1];
    
            // Get the existing sorter from CProgramTable instead of creating a new one
            TableRowSorter<DefaultTableModel> sorter;
            
            // If the table is already sorted, use that sorter, otherwise create a temporary one
            if (programTable.isSorted()) {
                sorter = (TableRowSorter<DefaultTableModel>) programTable.getTable().getRowSorter();
            } else {
                // Create a new sorter and apply it
                DefaultTableModel model = (DefaultTableModel) programTable.getTable().getModel();
                sorter = new TableRowSorter<>(model);
                programTable.getTable().setRowSorter(sorter);
            }
    
            // Apply the filter to the existing sorter
            if (searchText.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                if ("All".equals(columnName)) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                } else {
                    // Map column names to column indices
                    int columnIndex = -1;
                    switch (columnName) {
                        case "Program Name": columnIndex = 0; break;
                        case "Program Code": columnIndex = 1; break;
                        case "College Code": columnIndex = 2; break;
                    }
                    if (columnIndex >= 0) {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, columnIndex));
                    }
                }
            }
            
            // Update sort button icon to reflect current state
            updateSortButtonIcon();
        });
        
        searchPanelContainer.add(searchPanel, BorderLayout.NORTH);
        this.add(searchPanelContainer, gbc);

        JPanel topRow = new JPanel(new GridBagLayout());
        topRow.setOpaque(false);
        topRow.setPreferredSize(new Dimension(1, 100));
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        this.add(topRow, gbc);

        GridBagConstraints gbcTopRow = new GridBagConstraints();
        gbcTopRow.fill = GridBagConstraints.BOTH;
        gbcTopRow.gridy = 0; 
        gbcTopRow.weightx = 0.5;
        gbcTopRow.weighty = 1.0; 
        gbcTopRow.anchor = GridBagConstraints.SOUTH; 

        gbcTopRow.gridx = 0;
        gbcTopRow.weightx = 0.5;
        gbcTopRow.anchor = GridBagConstraints.WEST; 

        //setup TopLeftPanel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false); 
        topRow.add(leftPanel, gbcTopRow);

        gbcTopRow.gridx = 1;
        gbcTopRow.weightx = 0.5; 
        gbcTopRow.anchor = GridBagConstraints.EAST; 

        //setup TopRightPanel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        topRow.add(rightPanel, gbcTopRow);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        buttonsPanel.setOpaque(false);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        sortPanel.setOpaque(false);


        JLabel sortByLabel = new JLabel("Sort by:");
        sortByBox = new JComboBox<>(new String[]{"Program Name", "Program Code", "College Code"});
        
        sortButton = new JButton(new ImageIcon("Assets/DecendingIcon.png"));
        sortButton.setBorderPainted(false);
        sortButton.setFocusPainted(false);
        sortButton.setContentAreaFilled(false);

        sortButton.addActionListener(e -> {
            System.out.println("Sort button clicked");
            int columnIndex = sortByBox.getSelectedIndex();
            programTable.toggleSorting(columnIndex);
            updateSortButtonIcon();
        });
        
        sortPanel.add(sortByLabel);
        sortPanel.add(sortByBox);
        sortPanel.add(sortButton);

        JButton addProgramButton = new JButton(new ImageIcon("Assets/PlusIcon.png"));
        addProgramButton.setBorderPainted(false);
        addProgramButton.setFocusPainted(false);
        addProgramButton.setContentAreaFilled(false);
        addProgramButton.setBackground(new Color(0xE7E7E7));
        addProgramButton.addActionListener(e -> {
            Dialogs.addProgramDialog(programTable);
        });
 
        deleteButton = new JButton(new ImageIcon("Assets/DeleteIcon.png"));
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.addActionListener(e -> {
            JTable table = programTable.getTable();
            int selectedRow = table.getSelectedRow();
        
            if (selectedRow != -1) {
                Object idValue = table.getValueAt(selectedRow, 1);
                if (idValue != null) {
                    String programCode = idValue.toString();
                    Dialogs.deleteProgramDialog(programCode, programTable); 
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a college to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        editButton = new JButton(new ImageIcon("Assets/EditIcon.png"));
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.addActionListener(e -> {
            JTable table = programTable.getTable();
            int selectedRow = table.getSelectedRow();
        
            if (selectedRow != -1) {
                Object idValue = table.getValueAt(selectedRow, 1);
                if (idValue != null) {
                    String programCode = idValue.toString();
                    Dialogs.editProgramDialog(programCode, programTable); 
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a college to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonsPanel.add(addProgramButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(editButton);

        JLabel programsText = new JLabel("Programs");
        programsText.setFont(new Font("Helvetica", Font.BOLD, 32));
        JPanel textContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textContainer.setOpaque(false);

        textContainer.add(programsText);
        textContainer.add(sortPanel);
        leftPanel.add(textContainer, BorderLayout.SOUTH);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false); 
        gbc.gridy = 2;
        gbc.weighty = 0.9;
        this.add(bottomRow, gbc);
        
        JScrollPane scrollPane = new JScrollPane(programTable);
        bottomRow.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void updateSortButtonIcon() {
        if (!programTable.isSorted()) {
            sortButton.setIcon(new ImageIcon("Assets/SortDisabledIcon.png"));
        } else {
            sortButton.setIcon(new ImageIcon(
                programTable.getCurrentSortOrder() == SortOrder.ASCENDING 
                ? "Assets/AscendingIcon.png" 
                : "Assets/DecendingIcon.png"
            ));
        }
    }
    
    public CProgramTable getProgramTable() {
        return programTable;
    }
}