import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BudgetPlanner extends JFrame {
    private DefaultTableModel tableModel;
    private JTable budgetTable;
    private JTextField descriptionField, amountField;
    private JComboBox<String> typeComboBox;
    private JLabel balanceLabel;
    private double balance = 0.0;
    private static final String FILE_NAME = "budgetplan.csv";

    public BudgetPlanner() {
        createUI();
        loadFromCSV();
    }

    private void createUI() {
        setTitle("Budget Planner");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Entry"));

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(new String[] { "Income", "Expense" });
        inputPanel.add(typeComboBox);

        JButton addButton = new JButton("Add Entry");
        addButton.addActionListener(e -> addEntry());
        inputPanel.add(addButton);

        JButton saveButton = new JButton("Save to CSV");
        saveButton.addActionListener(e -> saveToCSV());
        inputPanel.add(saveButton);

        String[] columns = { "Description", "Amount", "Type" };
        tableModel = new DefaultTableModel(columns, 0);
        budgetTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(budgetTable);

        balanceLabel = new JLabel("Balance: Rs. 0.00", SwingConstants.LEFT);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(balanceLabel, BorderLayout.SOUTH);
    }

    private void addEntry() {
        try {
            String description = descriptionField.getText().trim();
            float amount = Float.parseFloat(amountField.getText().trim());
            String type = (String) typeComboBox.getSelectedItem();

            if (description.isEmpty() || amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter valid information", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.addRow(new Object[] { description, String.format("Rs. %.2f", amount), type });

            if ("Income".equals(type)) {
                balance += amount;
            } else {
                balance -= amount;
            }
            updateBalance();

            descriptionField.setText("");
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a numeric value.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: Rs. %.2f", balance));
    }

    private void saveToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.println(tableModel.getValueAt(i, 0) + "," +
                        tableModel.getValueAt(i, 1) + "," +
                        tableModel.getValueAt(i, 2));
            }
            JOptionPane.showMessageDialog(this, "Data saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    tableModel.addRow(new Object[] { data[0], data[1], data[2] });
                    double amount = Double.parseDouble(data[1].replace("Rs. ", ""));
                    if ("Income".equals(data[2])) {
                        balance += amount;
                    } else {
                        balance -= amount;
                    }
                }
            }
            updateBalance();
        } catch (IOException e) {
            System.out.println("No existing data found.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetPlanner().setVisible(true));
    }
}
