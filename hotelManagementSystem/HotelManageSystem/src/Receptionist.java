import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static javax.swing.SwingUtilities.invokeLater;

public class Receptionist {

    public static final String STATUS_OCCUPIED = "Occupied";
    public static final String STATUS_AVAILABLE = "Available";


    private JFrame mainFrame;
    private Container container;
    private CardLayout cardLayout;

    private JPanel mainPanel, checkInPanel, takenRoomsPanel, availableRoomsPanel, menuPanel;

    private JTable checkingIntableRoom, tableTakenRooms, tableAvailableRooms;
    private DefaultTableModel roomModel, takenRoomsModel, checkInRoomsModel;

    private JScrollPane scrollPaneRoom, scrollPaneTakenRooms, scrollPaneAvailableRooms;
    private JComboBox<String> dateComboBox;
    private JTextField tfName;
    private JTextField tfSurname;
    private JTextField tfPhoneNumber;
    private JTextField tfDate;

    private JButton btnConfirm;
    private JButton btnRooms, btnLogout, btnCheckIn, btnCheckOut, btnGoToCheckIn, btnGoToTakenRooms, btnGoBackToMainPageAvailableRooms, btnGoBackToMainPageTakenRooms, btnGoBackToMainPageCheckIn;
    private JLabel welcomeLabel = new JLabel("Welcome, Receptionist");

    private String[] columnNames1 = {"Room Number", "Room Type", "Room Price", "Room Status", "Maintenance Status"};
    private String[] columnNames2 = {"Room Number", "Room Type", "Room Price", "Room Status", "Customer Name", "Customer Surname", "Phone Number", "Check In Date"};

    private String[] roomTypes = {"Single", "Double", "Triple", "King", "Master Suite"};
    private String[] roomStatuses = {"Available", "Reserved", "Occupied"};
    private String[] roomPrices = {"1000", "1500", "2000", "2500", "3000"};

    private String customerName, customerSurname, phoneNumber, roomNumber, roomType, roomStatus, roomPrice;

    public static String roomsDirectory = "HotelManagementSystem\\rooms.txt";
    public static String takenRoomsDirectory = "HotelManagementSystem\\takenRooms.txt";
    public static String checkInRoomsDirectory = "HotelManagementSystem\\checkInRooms.txt";
    public static String customersDirectory = "HotelManagementSystem\\customers.txt";

    private ImageIcon ReceptionIcon = new ImageIcon(new ImageIcon("Images\\Reception.jpg").getImage().getScaledInstance(800, 530, Image.SCALE_DEFAULT));
    private JLabel ReceptionLabel = new JLabel(ReceptionIcon);

    private void updateRoomStatusToFile(String roomNumber, String roomStatus) throws IOException{

        for (int i = 0; i < roomModel.getRowCount(); i++) {
            if (roomModel.getValueAt(i, 0).equals(roomNumber)) {
                roomModel.setValueAt(roomStatus, i, 3);
            }
        }



        try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomsDirectory))) {
            for (int i = 0; i < roomModel.getRowCount(); i++) {
                String roomNumberRow = ((String) roomModel.getValueAt(i, 0)).trim();
                System.out.println(roomNumberRow);
                String roomType = ((String) roomModel.getValueAt(i, 1)).trim();
                System.out.println(roomType);
                String roomPrice = ((String) roomModel.getValueAt(i, 2)).trim();
                System.out.println(roomPrice);
                String roomStatusRow = ((String) roomModel.getValueAt(i, 3)).trim();
                System.out.println(roomStatusRow);
                String MaintenanceStatus = ((String) roomModel.getValueAt(i, 4)).trim();
                System.out.println(MaintenanceStatus);
                writer.write(roomNumberRow + "," + roomType + "," + roomPrice + "," + roomStatusRow + "," + MaintenanceStatus + "\n");
            }
        } catch (IOException ex) {
            System.err.println("An error occured writing the new status to the file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void checkIn(String roomNumber, DefaultTableModel checkInRoomsModel, DefaultTableModel takenRoomsModel, String checkInRoomsDirectory, String takenRoomsDirectory) {

       String roomType = ((String) checkInRoomsModel.getValueAt(checkingIntableRoom.getSelectedRow(), 1)).trim();
        String roomPrice = ((String) checkInRoomsModel.getValueAt(checkingIntableRoom.getSelectedRow(), 2)).trim();
        String maintenanceStatus = ((String) checkInRoomsModel.getValueAt(checkingIntableRoom.getSelectedRow(), 4)).trim();
        maintenanceStatus = maintenanceStatus.trim();
        String name = tfName.getText();
        String surname = tfSurname.getText();
        String phoneNumber = tfPhoneNumber.getText();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(new Date());
        if (maintenanceStatus.equals("Under Maintenance")) {
            JOptionPane.showMessageDialog(mainFrame, "The room is under maintenance and cannot be checked into");
            return;
        }
        checkInRoomsModel.removeRow(checkingIntableRoom.getSelectedRow());
        takenRoomsModel.addRow(new Object[]{roomNumber, roomType, roomPrice, STATUS_OCCUPIED, name, surname, phoneNumber, date});
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(checkInRoomsDirectory))) {
            for (int i = 0; i < checkInRoomsModel.getRowCount(); i++) {
                String roomNumberRow = (String) checkInRoomsModel.getValueAt(i, 0);
                String roomTypeRow = (String) checkInRoomsModel.getValueAt(i, 1);
                String roomPriceRow = (String) checkInRoomsModel.getValueAt(i, 2);
                String roomStatusRow = (String) checkInRoomsModel.getValueAt(i, 3);
                String maintenanceStatusRow = (String) checkInRoomsModel.getValueAt(i, 4);
                writer.write(roomNumberRow + "," + roomTypeRow + "," + roomPriceRow + "," + roomStatusRow + "," + maintenanceStatusRow + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();}
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(takenRoomsDirectory, true))) {
            writer.write("\n" + roomNumber + "," + roomType + "," + roomPrice + "," + STATUS_OCCUPIED + "," + name + "," + surname + "," + phoneNumber + "," + date);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customersDirectory, true))) {
            writer.write("\n" + name + "," + surname + "," + phoneNumber + "," + roomNumber + "," + date);
        } catch (IOException ex) {
            ex.printStackTrace();}
        try {
            updateRoomStatusToFile(roomNumber, STATUS_OCCUPIED);
        } catch (IOException ex) {
            ex.printStackTrace();}
        tfName.setText("");
        tfSurname.setText("");
        tfPhoneNumber.setText("");

    }

    private void checkOut(String roomNumber, DefaultTableModel takenRoomsModel, DefaultTableModel checkInRoomsModel, String takenRoomsDirectory, String checkInRoomsDirectory) {

        int selectedRow = tableTakenRooms.getSelectedRow();

        String roomType = (String) takenRoomsModel.getValueAt(selectedRow, 1);
        String roomPrice = (String) takenRoomsModel.getValueAt(selectedRow, 2);
        String customerName = (String) takenRoomsModel.getValueAt(selectedRow, 4);
        String customerSurname = (String) takenRoomsModel.getValueAt(selectedRow, 5);
        String phoneNumber = (String) takenRoomsModel.getValueAt(selectedRow, 6);
        String date = (String) takenRoomsModel.getValueAt(selectedRow, 7);

        checkInRoomsModel.addRow(new Object[]{roomNumber, roomType, roomPrice, STATUS_AVAILABLE, "Under Maintenance"});

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(checkInRoomsDirectory, true))) {
            writer.write("\n" + roomNumber + "," + roomType + "," + roomPrice + "," + STATUS_AVAILABLE + ",Under Maintenance");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        takenRoomsModel.removeRow(selectedRow);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(takenRoomsDirectory))) {
            for (int i = 0; i < takenRoomsModel.getRowCount(); i++) {
                String roomNumberRow = (String) takenRoomsModel.getValueAt(i, 0);
                String roomTypeRow = (String) takenRoomsModel.getValueAt(i, 1);
                String roomPriceRow = (String) takenRoomsModel.getValueAt(i, 2);
                String roomStatusRow = (String) takenRoomsModel.getValueAt(i, 3);
                String customerNameRow = (String) takenRoomsModel.getValueAt(i, 4);
                String customerSurnameRow = (String) takenRoomsModel.getValueAt(i, 5);
                String phoneNumberRow = (String) takenRoomsModel.getValueAt(i, 6);
                String dateRow = (String) takenRoomsModel.getValueAt(i, 7);
                writer.write(roomNumberRow + "," + roomTypeRow + "," + roomPriceRow + "," + roomStatusRow + "," + customerNameRow + "," + customerSurnameRow + "," + phoneNumberRow + "," + dateRow + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

                for (int i = 0; i < roomModel.getRowCount(); i++) {
                     if (roomModel.getValueAt(i, 0).equals(roomNumber)) {
                          roomModel.setValueAt(STATUS_AVAILABLE, i, 3);
                          roomModel.setValueAt("Under Maintenance", i, 4);
                     }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomsDirectory))) {
                    for (int i = 0; i < roomModel.getRowCount(); i++) {
                        String roomNumberRow = (String) roomModel.getValueAt(i, 0);
                        String roomType1 = (String) roomModel.getValueAt(i, 1);
                        String roomPrice1 = (String) roomModel.getValueAt(i, 2);
                        String roomStatusRow = (String) roomModel.getValueAt(i, 3);
                        String MaintenanceStatus = (String) roomModel.getValueAt(i, 4);
                        writer.write(roomNumberRow + "," + roomType1 + "," + roomPrice1 + "," + roomStatusRow + "," + MaintenanceStatus + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    }

    public Receptionist()  {

        menuPanel = new JPanel(null);

        mainFrame = new JFrame();
        container = mainFrame.getContentPane();
        cardLayout = new CardLayout();
        mainFrame.setResizable(false);

        mainPanel = new JPanel(cardLayout);
        checkInPanel = new JPanel(null);
        takenRoomsPanel = new JPanel(null);
        availableRoomsPanel = new JPanel(null);

        mainFrame.setTitle("Receptionist");
        mainFrame.setSize(800, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setLayout(cardLayout);

        mainPanel.add(checkInPanel, "checkInPanel");
        mainPanel.add(takenRoomsPanel, "takenRoomsPanel");
        mainPanel.add(availableRoomsPanel, "availableRoomsPanel");

        roomModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        takenRoomsModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        checkInRoomsModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        checkingIntableRoom = new JTable();
        tableTakenRooms = new JTable();
        tableAvailableRooms = new JTable();

        takenRoomsModel.setColumnIdentifiers(columnNames2);
        checkInRoomsModel.setColumnIdentifiers(columnNames1);
        roomModel.setColumnIdentifiers(columnNames1);

        scrollPaneRoom = new JScrollPane(checkingIntableRoom);
        scrollPaneTakenRooms = new JScrollPane(tableTakenRooms);
        scrollPaneAvailableRooms = new JScrollPane(tableAvailableRooms);

        btnRooms = new JButton("Rooms");
        btnLogout = new JButton("Logout");
        btnCheckIn = new JButton("Check In");
        btnCheckOut = new JButton("Check Out");
        btnGoToCheckIn = new JButton("Check In");
        btnGoToTakenRooms = new JButton("Taken Rooms");
        ReceptionLabel.setBounds(0, 0, 800, 530);
        menuPanel.add(ReceptionLabel);

        btnGoBackToMainPageAvailableRooms = new JButton("Main Page");
        btnGoBackToMainPageTakenRooms = new JButton("Main Page");
        btnGoBackToMainPageCheckIn = new JButton("Main Page");

        roomTypes = new String[]{"Single", "Double", "Triple", "King", "Master Suite"};
        roomStatuses = new String[]{"Available", "Reserved", "Occupied"};
        roomPrices = new String[]{"1000", "1500", "2000", "2500", "3000"};

        customerName = "";
        customerSurname = "";
        roomNumber = "";
        roomType = "";
        roomStatus = "";
        roomPrice = "";

        Color customColor = new Color(102, 57, 18);

        welcomeLabel.setBounds(100, 70, 200, 20);
        availableRoomsPanel.add(welcomeLabel);

        checkInPanel.add(scrollPaneRoom);
        takenRoomsPanel.add(scrollPaneTakenRooms);

        availableRoomsPanel.add(scrollPaneAvailableRooms);


        checkInPanel.add(btnCheckIn);

        menuPanel.add(btnRooms);
        menuPanel.add(btnLogout);
        menuPanel.add(btnGoToCheckIn);
        menuPanel.add(btnGoToTakenRooms);

        mainPanel.add(menuPanel, "mainPanel");
        container.add(mainPanel);

        btnRooms.setBounds(90, 575, 100, 40);
        btnGoToCheckIn.setBounds(250, 575,100, 40);
        btnGoToTakenRooms.setBounds(400, 575, 120, 40);
        btnLogout.setBounds(580, 575, 100, 40);

        btnRooms.setBackground(customColor);
        btnGoToCheckIn.setBackground(customColor);
        btnGoToTakenRooms.setBackground(customColor);
        btnLogout.setBackground(customColor);

        btnRooms.setForeground(Color.WHITE);
        btnGoToCheckIn.setForeground(Color.WHITE);
        btnGoToTakenRooms.setForeground(Color.WHITE);
        btnLogout.setForeground(Color.WHITE);

        menuPanel.setBackground(new Color(200, 168, 131));

        checkInPanel.setBackground(new Color(200, 168, 131));
        takenRoomsPanel.setBackground(new Color(200, 168, 131));
        availableRoomsPanel.setBackground(new Color(200, 168, 131));


        btnGoBackToMainPageAvailableRooms.setBounds(100, 450, 100, 40);
        btnGoBackToMainPageTakenRooms.setBounds(200, 450, 100, 40);
        btnGoBackToMainPageCheckIn.setBounds(200, 550, 100, 20);

        btnGoBackToMainPageAvailableRooms.setBackground(customColor);
        btnGoBackToMainPageTakenRooms.setBackground(customColor);
        btnGoBackToMainPageCheckIn.setBackground(customColor);
        btnCheckIn.setBackground(customColor);
        btnCheckOut.setBackground(customColor);
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckOut.setForeground(Color.WHITE);
        btnGoBackToMainPageAvailableRooms.setForeground(Color.WHITE);
        btnGoBackToMainPageTakenRooms.setForeground(Color.WHITE);
        btnGoBackToMainPageCheckIn.setForeground(Color.WHITE);

        btnCheckOut.setBounds(500, 450, 100, 40);

        takenRoomsPanel.add(btnCheckOut);

        availableRoomsPanel.add(btnGoBackToMainPageAvailableRooms);
        takenRoomsPanel.add(btnGoBackToMainPageTakenRooms);
        checkInPanel.add(btnGoBackToMainPageCheckIn);

        checkingIntableRoom.setModel(checkInRoomsModel);
        tableTakenRooms.setModel(takenRoomsModel);
        tableAvailableRooms.setModel(roomModel);

        tfName = new JTextField();
        tfSurname = new JTextField();
        tfPhoneNumber = new JTextField();
        tfDate = new JTextField();
        btnConfirm = new JButton("Confirm");

        try {
            MaskFormatter maskFormatter = new MaskFormatter("05#########");
            maskFormatter.setPlaceholderCharacter(' ');
            tfPhoneNumber = new JFormattedTextField(maskFormatter);
            tfPhoneNumber.setBounds(160, 370, 100, 20);
            checkInPanel.add(tfPhoneNumber);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tfName.setBounds(100, 310, 100, 20);
        tfSurname.setBounds(210, 310, 100, 20);
        tfPhoneNumber.setBounds(320, 310, 100, 20);
        btnConfirm.setBounds(100, 550, 100, 20);
        btnConfirm.setBackground(customColor);
        btnConfirm.setForeground(Color.WHITE);

        checkInPanel.add(tfName);
        checkInPanel.add(tfSurname);
        checkInPanel.add(tfPhoneNumber);
        checkInPanel.add(tfDate);
        checkInPanel.add(btnConfirm);


        JLabel nameLabel = new JLabel("Name:");
        JLabel surnameLabel = new JLabel("Surname:");
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JLabel dateLabel = new JLabel("Date:");


        nameLabel.setBounds(50, 310, 100, 20);
        surnameLabel.setBounds(50, 340, 100, 20);
        phoneNumberLabel.setBounds(50, 370, 100, 20);
        dateLabel.setBounds(50, 400, 100, 20);


        tfName.setBounds(160, 310, 100, 20);
        tfSurname.setBounds(160, 340, 100, 20);
        tfPhoneNumber.setBounds(160, 370, 100, 20);
        tfDate.setBounds(160, 400, 100, 20);

        checkInPanel.add(nameLabel);
        checkInPanel.add(surnameLabel);
        checkInPanel.add(phoneNumberLabel);
        checkInPanel.add(dateLabel);

        JTextField roomNumberField = new JTextField();
        JTextField roomPriceField = new JTextField();
        JTextField roomTypeField = new JTextField();

        roomNumberField.setBounds(160, 430, 100, 20);
        roomPriceField.setBounds(160, 460, 100, 20);
        roomTypeField.setBounds(160, 490, 100, 20);


        checkInPanel.add(roomNumberField);
        checkInPanel.add(roomPriceField);
        checkInPanel.add(roomTypeField);

        JLabel roomNumberLabel = new JLabel("Room Number:");
        JLabel roomPriceLabel = new JLabel("Room Price:");
        JLabel roomTypeLabel = new JLabel("Room Type:");

        roomNumberLabel.setBounds(50, 430, 100, 20);
        roomPriceLabel.setBounds(50, 460, 100, 20);
        roomTypeLabel.setBounds(50, 490, 100, 20);

        scrollPaneRoom.setBounds(100, 100, 600, 200);
        scrollPaneTakenRooms.setBounds(50, 100, 680, 200);
        scrollPaneAvailableRooms.setBounds(100, 100, 600, 200);

        checkInPanel.add(roomNumberLabel);
        checkInPanel.add(roomPriceLabel);
        checkInPanel.add(roomTypeLabel);

        tfDate.setEditable(false);
        roomNumberField.setEditable(false);
        roomPriceField.setEditable(false);
        roomTypeField.setEditable(false);


        checkingIntableRoom.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && checkingIntableRoom.getSelectedRow() != -1) {
                    int selectedRow = checkingIntableRoom.getSelectedRow();
                    roomNumberField.setText((String) checkInRoomsModel.getValueAt(selectedRow, 0));
                    roomTypeField.setText((String) checkInRoomsModel.getValueAt(selectedRow, 1));
                    roomPriceField.setText((String) checkInRoomsModel.getValueAt(selectedRow, 2));
                    tfDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                }
            }
        });


        try (BufferedReader reader = new BufferedReader(new FileReader(roomsDirectory))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String roomNumber = parts[0];
                    String roomType = parts[1];
                    String roomPrice = parts[2];
                    String roomStatus = parts[3];
                    String maintenanceStatus = parts[4];
                    Object[] rowData = {roomNumber, roomType, roomPrice, roomStatus, maintenanceStatus};
                    roomModel.addRow(rowData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(checkInRoomsDirectory))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String roomNumber = parts[0];
                    String roomType = parts[1];
                    String roomPrice = parts[2];
                    String roomStatus = parts[3];
                    String maintenanceStatus = parts[4];
                    Object[] rowData = {roomNumber, roomType, roomPrice, roomStatus, maintenanceStatus};
                    checkInRoomsModel.addRow(rowData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(takenRoomsDirectory))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {

                    String roomNumber = parts[0];
                    String roomType = parts[1];
                    String roomPrice = parts[2];
                    String roomStatus = parts[3];
                    String customerName = parts[4];
                    String customerSurname = parts[5];
                    String phoneNumber = parts[6];
                    String date = parts[7];
                    Object[] rowData = {roomNumber, roomType, roomPrice, roomStatus, customerName, customerSurname, phoneNumber, date};
                    takenRoomsModel.addRow(rowData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "availableRoomsPanel");
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                new LoginPage();
            }
        });

        btnGoToCheckIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "checkInPanel");
            }
        });

        btnGoToTakenRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "takenRoomsPanel");

            }
        });

        btnGoBackToMainPageAvailableRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "mainPanel");
            }
        });

        btnGoBackToMainPageTakenRooms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "mainPanel");
            }
        });

        btnGoBackToMainPageCheckIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "mainPanel");
            }
        });

        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (checkingIntableRoom.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(mainFrame, "Please select a room to check in");
                    return;
                }

                if (tfName.getText().isEmpty() || tfSurname.getText().isEmpty() || tfPhoneNumber.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "Please fill in all the fields.");
                    return;
                }

                String phoneNumber = tfPhoneNumber.getText();


                if (phoneNumber.contains(" ")) {
                    JOptionPane.showMessageDialog(mainFrame, "Please enter a complete phone number.");
                    return;
                }

                int selectedRow = checkingIntableRoom.getSelectedRow();

                String roomNumber = (String) checkInRoomsModel.getValueAt(selectedRow, 0);
                checkIn(roomNumber, checkInRoomsModel, takenRoomsModel, checkInRoomsDirectory, takenRoomsDirectory);
            }
        });

        btnCheckOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = tableTakenRooms.getSelectedRow();


                if (tableTakenRooms.getSelectedRow() != -1) {
                    String roomNumber = (String) takenRoomsModel.getValueAt(selectedRow, 0);
                    checkOut(roomNumber, takenRoomsModel, checkInRoomsModel, takenRoomsDirectory, checkInRoomsDirectory);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Please select a row to check out");
                }

            }

        });

        tfName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((!Character.isLetter(c) && !Character.isWhitespace(c)) || (Character.isWhitespace(c) && (tfName.getText().length() == 0 || tfName.getText().endsWith(" ")))) {
                    e.consume();
                }
            }
        });

        tfSurname.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((!Character.isLetter(c) && !Character.isWhitespace(c)) || (Character.isWhitespace(c) && (tfSurname.getText().length() == 0 || tfSurname.getText().endsWith(" ")))) {
                    e.consume();
                }
            }
        });

        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout.show(mainPanel, "mainPanel");

    }
//    public static void main(String[] args){
//            invokeLater(Receptionist::new);
//    }
}