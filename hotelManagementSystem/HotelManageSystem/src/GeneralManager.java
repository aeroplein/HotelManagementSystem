import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

    public class GeneralManager {
        public static String roomsDirectory = "HotelManagementSystem\\rooms.txt";
        public static String employeeDirectory = "HotelManagementSystem\\employee.txt";

        private JFrame mainFrame = new JFrame();
        private Container container = mainFrame.getContentPane();
        private CardLayout cardLayout = new CardLayout();

        private JPanel receptionistPanel = new JPanel(null);
        private JPanel managerPanel = new JPanel(null);
        private JPanel mainPanel = new JPanel(cardLayout);

        private JTable tableReceptionist = new JTable();
        private JTable tableManager = new JTable();

        private DefaultTableModel managerModel = new DefaultTableModel();
        private DefaultTableModel receptionistModel = new DefaultTableModel();

        private String[] columnNames1 = {"Name", "Surname", "Username", "Salary"};
        private String[] columnNames2 = {"Name", "Surname", "Username", "Salary"};

        private JScrollPane scrollPane1;
        private JScrollPane scrollPane2;

        private JButton addReceptionist = new JButton("Add");
        private JButton removeReceptionist = new JButton("Remove");
        private JButton addManager = new JButton("Add");
        private JButton removeManager = new JButton("Remove");

        private JLabel welcomeLabel = new JLabel("Welcome, General Manager");
        private JLabel welcomeLabel2 = new JLabel("Welcome, General Manager");
        private JLabel managerLabel = new JLabel("Manager");
        private JLabel receptionistLabel = new JLabel("Receptionist");

        private JButton btnGoToReceptionist = new JButton("Receptionist");
        private JButton btnGoToManager = new JButton("Manager");
        private JButton btnLogout1 = new JButton("Logout");
        private JButton btnLogout2 = new JButton("Logout");
        private Color customColor = new Color(162, 194, 231);

        private String generateSecurePassword(int length) {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            password.append(charSet.charAt(randomIndex));
        }
        return password.toString();
    }

        private void updateTablesFromEmployeeFile() {
             receptionistModel.setRowCount(0);
             managerModel.setRowCount(0);

    String employeeFilePath = "HotelManagementSystem\\employee.txt";

    try (BufferedReader br = new BufferedReader(new FileReader(employeeFilePath))) {
        String line;
        while ((line = br.readLine()) != null) {

            if (line.trim().isEmpty()) continue;

            String[] employeeDetails = line.split(",");


            if (employeeDetails.length == 6) {
                String role = employeeDetails[4].trim();
                Object[] rowData = {employeeDetails[0].trim(), employeeDetails[1].trim(), employeeDetails[2].trim(), employeeDetails[5].trim()};
                if ("Receptionist".equals(role)) {
                    receptionistModel.addRow(rowData);
                } else if ("Manager".equals(role)) {
                    managerModel.addRow(rowData);
                }
            } else {

                System.err.println("Invalid employee record format: " + line);
            }
        }
    } catch (IOException e) {
        System.err.println("An error occurred while reading the employee file: " + e.getMessage());
    }
}


        private void addReceptionist() {
            JTextField tfName = new JTextField();
            JTextField tfSurname = new JTextField();
            JTextField tfUsername = new JTextField();
            JTextField tfSalary = new JTextField();

            tfSalary.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) {
                        e.consume();
                    }
                }
            });

            Object[] message = {
                 "Name:", tfName,
                 "Surname:", tfSurname,
                 "Username:", tfUsername,
                 "Salary:", tfSalary
    };

            int option = JOptionPane.showConfirmDialog(mainFrame, message, "Add Receptionist", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = tfName.getText();
                String surname = tfSurname.getText();
                String username = tfUsername.getText();
                String salary = tfSalary.getText();

                String password = generateSecurePassword(5);


                try (BufferedReader br = new BufferedReader(new FileReader(employeeDirectory))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] details = line.split(",");
                        if (details.length > 2 && details[2].trim().equals(username)) {
                            JOptionPane.showMessageDialog(mainFrame, "Username already exists. Please enter a new username.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!name.isEmpty() && !surname.isEmpty() && !username.isEmpty() && !salary.isEmpty()) {
                    receptionistModel.addRow(new Object[]{name, surname, username, salary});

                    try(BufferedWriter bw = new BufferedWriter(new FileWriter("HotelManagementSystem\\employee.txt", true))) {

                        Employee reception = new Reception(name, surname, username, password, "Receptionist", Double.parseDouble(salary));
                        bw.write(reception.toString());
                        bw.newLine();
                        updateTablesFromEmployeeFile();
                        JOptionPane.showMessageDialog(mainFrame, "Receptionist added successfully" + " with password: " + password, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }

            }
}



    private void removeManager() {
    int selectedRow = tableManager.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(mainFrame, "Please select a row to remove", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    String usernameToRemove = (String) managerModel.getValueAt(selectedRow, 2);

    List<String> linesToKeep = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(employeeDirectory))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] details = line.split(",");
            if (details.length > 2 && !details[2].trim().equals(usernameToRemove)) {
                linesToKeep.add(line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(employeeDirectory, false))) {
        for (String line : linesToKeep) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    managerModel.removeRow(selectedRow);
}

    private void removeReceptionist() {
    int selectedRow = tableReceptionist.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(mainFrame, "Please select a row to remove", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    String usernameToRemove = (String) receptionistModel.getValueAt(selectedRow, 2);

    List<String> linesToKeep = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(employeeDirectory))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] details = line.split(",");
            if (details.length > 2 && !details[2].trim().equals(usernameToRemove)) {
                linesToKeep.add(line);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(employeeDirectory, false))) {
        for (String line : linesToKeep) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    receptionistModel.removeRow(selectedRow);
}


    private void addManager() {

    JTextField tfName = new JTextField();
    JTextField tfSurname = new JTextField();
    JTextField tfUsername = new JTextField();
    JTextField tfSalary = new JTextField();
            tfSalary.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) {
                        e.consume();
                    }
                }
            });

    Object[] message = {
         "Name:", tfName,
        "Surname:", tfSurname,
        "Username:", tfUsername,
        "Salary:", tfSalary
    };

    int option = JOptionPane.showConfirmDialog(mainFrame, message, "Add Manager", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        String name = tfName.getText();
        String surname = tfSurname.getText();
        String username = tfUsername.getText();
        String salary = tfSalary.getText();
        String password = generateSecurePassword(5);

        try (BufferedReader br = new BufferedReader(new FileReader(employeeDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length > 2 && details[2].trim().equals(username)) {
                    JOptionPane.showMessageDialog(mainFrame, "Username already exists. Please enter a new username.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (!name.isEmpty() && !surname.isEmpty() && !username.isEmpty() && !salary.isEmpty()) {
            managerModel.addRow(new Object[]{name, surname, username, salary});

            try(BufferedWriter bw = new BufferedWriter(new FileWriter("HotelManagementSystem\\employee.txt", true))) {

                Employee manager = new Management(name, surname, username, password, "Manager", Double.parseDouble(salary));
                bw.write(manager.toString());
                bw.newLine();
                updateTablesFromEmployeeFile();
                JOptionPane.showMessageDialog(mainFrame, "Manager added successfully" + " with password: " + password, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

        public GeneralManager() {

            mainFrame.setTitle("General Manager");
            mainFrame.setBounds(100, 100, 800, 600);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            updateTablesFromEmployeeFile();
            mainFrame.setVisible(true);
            container.add(mainPanel);
            mainPanel.add(receptionistPanel, "receptionistPanel");
            mainPanel.add(managerPanel, "managerPanel");
            cardLayout.show(mainPanel, "mainPanel");

            receptionistPanel.setLayout(null);
            managerPanel.setLayout(null);

            welcomeLabel.setBounds(100, 50, 200, 50);
            btnGoToReceptionist.setBounds(100, 450, 150, 50);
            receptionistModel.setColumnIdentifiers(columnNames1);
            managerModel.setColumnIdentifiers(columnNames2);

            tableReceptionist.setModel(receptionistModel);
            tableManager.setModel(managerModel);
            tableReceptionist.getTableHeader().setReorderingAllowed(false);
            tableManager.getTableHeader().setReorderingAllowed(false);

            tableReceptionist.setDefaultEditor(Object.class, null);
            tableManager.setDefaultEditor(Object.class, null);

            welcomeLabel2.setBounds(100, 50, 200, 50);
            receptionistPanel.add(welcomeLabel2);
            managerLabel.setBounds(500, 50, 200, 50);
            receptionistLabel.setBounds(500, 50, 200, 50);
            managerPanel.add(managerLabel);
            receptionistPanel.add(receptionistLabel);

            scrollPane1 = new JScrollPane(tableReceptionist);
            scrollPane1.setBounds(100, 100, 600, 200);
            receptionistPanel.add(scrollPane1);

            scrollPane2 = new JScrollPane(tableManager);
            scrollPane2.setBounds(100, 100, 600, 200);
            managerPanel.add(scrollPane2);
            addReceptionist.setBounds(100, 350, 120, 30);
            removeReceptionist.setBounds(235, 350, 120, 30);
            addManager.setBounds(100, 350, 120, 30);
            removeManager.setBounds(235, 350, 120, 30);

            btnGoToReceptionist.setBounds(385, 350, 120, 30);
            btnGoToManager.setBounds(385, 350, 120, 30);
            btnLogout1.setBounds(550, 350, 100, 30);
            btnLogout2.setBounds(550, 350, 100, 30);

            receptionistPanel.add(welcomeLabel);
            receptionistPanel.add(addReceptionist);
            receptionistPanel.add(removeReceptionist);
            receptionistPanel.add(btnGoToManager);
            receptionistPanel.add(btnLogout1);

            managerPanel.add(welcomeLabel);
            managerPanel.add(addManager);
            managerPanel.add(removeManager);
            managerPanel.add(btnGoToReceptionist);
            managerPanel.add(btnLogout2);

            managerPanel.setBackground(customColor);
            receptionistPanel.setBackground(customColor);

            updateTablesFromEmployeeFile();
            addReceptionist.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    addReceptionist();
                    updateTablesFromEmployeeFile();
                }
            });

            removeReceptionist.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (tableReceptionist.getSelectedRow() == -1) {
                        JOptionPane.showMessageDialog(mainFrame, "Please select a row to remove", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try{
                        removeReceptionist();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(mainFrame, "Please select a row to remove");
                    }
                }
            });

            addManager.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addManager();
                    updateTablesFromEmployeeFile();
                }
            });

            removeManager.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (tableManager.getSelectedRow() == -1) {
                        JOptionPane.showMessageDialog(mainFrame, "Please select a row to remove", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        removeManager();
                    } catch (ArrayIndexOutOfBoundsException ex) {

                        ex.printStackTrace();
                    }

                }
            });

            btnGoToManager.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "managerPanel");
                }
            });

            btnGoToReceptionist.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "receptionistPanel");
                }
            });

            btnLogout1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainFrame.dispose();
                    new LoginPage();
                }
            });

            btnLogout2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainFrame.dispose();
                    new LoginPage();
                }
            });

        }

//        public static void main(String[] args) {
//            SwingUtilities.invokeLater(GeneralManager::new);
//        }

    }


