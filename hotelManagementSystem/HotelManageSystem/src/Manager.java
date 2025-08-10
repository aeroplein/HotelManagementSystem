import javax.swing.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.EventObject;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;

public class Manager {
    public static String roomsDirectory = "HotelManagementSystem\\rooms.txt";
    public static String checkInRoomsDirectory = "HotelManagementSystem\\checkInRooms.txt";
    private String[] columnNamesRooms = {"Room Number", "Room Type ", "Price","Status","Maintenance"};
    private Container container;
    private JFrame frame;
    private JTable ManagementTable;
    private DefaultTableModel roomManageModel;
    private JScrollPane scrollPane;
    private JButton back,logout;
    private JButton addRoom, removeRoom,goToAddRoomPanel;
    private JPanel managementPanel, addRoomPanel,cardPanel;
    private JTextField tfRoomNumber;
    private JComboBox<String> RoomType;
    private JTextField Status;
    private JComboBox<String> Maintenance = new JComboBox<>();
    private JLabel roomNumberLabel, roomTypeLabel, statusLabel, maintenanceLabel, welcomeLabel;
    private CardLayout cardLayout = new CardLayout();
    private JButton btnUpdateRoom;
    private Color backgroundColor = new Color(207, 224, 197);
    private ImageIcon image = new ImageIcon("HotelManagementSystem\\addRoomImage.jpg");
    private JLabel imageLabel = new JLabel(image);


   private void updateRoom() {
    JDialog dialog = new JDialog(frame, "Update Room", true);
    dialog.setLayout(new GridLayout(6, 2));

    JComboBox<String> cbRoomNumber = new JComboBox<>();
    JComboBox<String> cbRoomType = new JComboBox<>(new String[]{"Single", "Double", "Triple", "King", "Master Suite"});
    JTextField tfPrice = new JTextField();
    JComboBox<String> cbMaintenance = new JComboBox<>(new String[]{"Ready", "Under Maintenance"});

    tfPrice.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c)) {
                e.consume();
            }
        }
    });

    for (int i = 0; i < roomManageModel.getRowCount(); i++) {
        cbRoomNumber.addItem(roomManageModel.getValueAt(i, 0).toString());
    }

    cbRoomNumber.addActionListener(e -> {
        String selectedRoomNumber = (String) cbRoomNumber.getSelectedItem();
        for (int i = 0; i < roomManageModel.getRowCount(); i++) {
            if (roomManageModel.getValueAt(i, 0).equals(selectedRoomNumber)) {
                cbRoomType.setSelectedItem(roomManageModel.getValueAt(i, 1).toString());
                tfPrice.setText(roomManageModel.getValueAt(i, 2).toString());
                cbMaintenance.setSelectedItem(roomManageModel.getValueAt(i, 4).toString());
                break;
            }
        }
    });


    if (cbRoomNumber.getItemCount() > 0) {
        cbRoomNumber.setSelectedIndex(0);
    }

    dialog.add(new JLabel("Room Number:"));
    dialog.add(cbRoomNumber);
    dialog.add(new JLabel("Room Type:"));
    dialog.add(cbRoomType);
    dialog.add(new JLabel("Price:"));
    dialog.add(tfPrice);
    dialog.add(new JLabel("Maintenance:"));
    dialog.add(cbMaintenance);

    JButton btnSave = new JButton("Save");

    btnSave.addActionListener(e -> {
        String selectedRoomNumber = (String) cbRoomNumber.getSelectedItem();
        for (int i = 0; i < roomManageModel.getRowCount(); i++) {
            if (roomManageModel.getValueAt(i, 0).equals(selectedRoomNumber)) {
                roomManageModel.setValueAt(cbRoomType.getSelectedItem(), i, 1);
                roomManageModel.setValueAt(tfPrice.getText(), i, 2);
                roomManageModel.setValueAt(cbMaintenance.getSelectedItem(), i, 4);
                break;
            }
        }

        saveRoomsToFile();
        refreshTable();
        dialog.dispose();
    });

    dialog.add(btnSave);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);
}

    private void saveRoomsToFile() {

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomsDirectory, false))) {
        for (int i = 0; i < roomManageModel.getRowCount(); i++) {
            StringBuilder roomDetails = new StringBuilder();
            for (int j = 0; j < roomManageModel.getColumnCount(); j++) {
                roomDetails.append(roomManageModel.getValueAt(i, j).toString());
                if (j < roomManageModel.getColumnCount() - 1) {
                    roomDetails.append(",");
                }
            }
            writer.write(roomDetails.toString());
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(frame, "Error saving rooms to file");
        e.printStackTrace();
    }


    List<String> checkInRoomsList = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(checkInRoomsDirectory))) {
        String line;
        while ((line = reader.readLine()) != null) {
            checkInRoomsList.add(line);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(frame, "Error reading check-in rooms file");
        e.printStackTrace();
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(checkInRoomsDirectory, false))) {
        for (int i = 0; i < roomManageModel.getRowCount(); i++) {
            String roomNumber = roomManageModel.getValueAt(i, 0).toString();
            String roomDetails = String.join(", ",
                roomManageModel.getValueAt(i, 0).toString(),
                roomManageModel.getValueAt(i, 1).toString(),
                roomManageModel.getValueAt(i, 2).toString(),
                roomManageModel.getValueAt(i, 3).toString(),
                roomManageModel.getValueAt(i, 4).toString());

            boolean found = false;
            for (int j = 0; j < checkInRoomsList.size(); j++) {
                if (checkInRoomsList.get(j).startsWith(roomNumber + ",")) {
                    checkInRoomsList.set(j, roomDetails);
                    found = true;
                    break;
                }
            }
            if (!found && roomManageModel.getValueAt(i, 3).toString().equals("Available") && roomManageModel.getValueAt(i, 4).toString().equals("Ready")) {
                checkInRoomsList.add(roomDetails);
            }
        }

        for (String line : checkInRoomsList) {
            writer.write(line);
            writer.newLine();
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(frame, "Error updating check-in rooms file");
        e.printStackTrace();
    }
}


    public void refreshTable(){
        roomManageModel.setRowCount(0);
        try(BufferedReader reader = new BufferedReader(new FileReader(Receptionist.roomsDirectory))){
            String line;
            while((line = reader.readLine()) != null){
                String[] roomDetails = line.split(",");
                roomManageModel.addRow(roomDetails);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void addRoom(int RoomNumber, String Type, boolean Maintenance) {
    boolean exists = false;
    for (int i = 0; i < roomManageModel.getRowCount(); i++) {
        String roomNumberInTable = roomManageModel.getValueAt(i, 0).toString();
        try {
            int roomNumberInTableAsInt = Integer.parseInt(roomNumberInTable);
            if (roomNumberInTableAsInt == RoomNumber) {
                exists = true;
                break;
            }
        } catch (NumberFormatException e) {
            continue;
        }
    }

    if (exists) {
        JOptionPane.showMessageDialog(frame, "Room already exists");
        return;
    }

    String stat = Status.getText();
    String maint = Maintenance ? "Ready" : "Under Maintenance";
    int price = 0;
    switch (Type) {
        case "Single":
            price = 1000;
            break;
        case "Double":
            price = 2000;
            break;
        case "Triple":
            price = 3000;
            break;
        case "King":
            price = 4000;
            break;
        case "Master Suite":
            price = 5000;
            break;
    }

    roomManageModel.addRow(new Object[]{RoomNumber, Type, price, stat, maint});


    if(stat.equals("Available")){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Receptionist.checkInRoomsDirectory, true))){
            writer.write(RoomNumber + "," + Type + "," + price + "," + stat + "," + maint);
            writer.newLine();

        }catch (IOException e){
            System.out.println("Error loading rooms" + e.getMessage());
        }
    }


    try (BufferedWriter writer = new BufferedWriter(new FileWriter(Receptionist.roomsDirectory, true))) {
        writer.write(RoomNumber + "," + Type + "," + price + "," + stat + "," + maint);
        writer.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }

    JOptionPane.showMessageDialog(frame, "Room added successfully");
}

    public void removeRoom(){

        int row = ManagementTable.getSelectedRow();
        if(row > -1){
            String status = roomManageModel.getValueAt(row, 3).toString();
            if(status.equals("Occupied")){
                JOptionPane.showMessageDialog(frame, "Room is occupied and cannot be removed");
                return;
            }
            roomManageModel.removeRow(row);
        } else{
            JOptionPane.showMessageDialog(frame, "Please select a row to remove");
            return;
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Receptionist.roomsDirectory))){
            for(int i = 0; i < roomManageModel.getRowCount(); i++){
                writer.write(roomManageModel.getValueAt(i, 0).toString()+",");
                writer.write(roomManageModel.getValueAt(i, 1).toString()+",");
                writer.write(roomManageModel.getValueAt(i, 2).toString()+",");
                writer.write(roomManageModel.getValueAt(i, 3).toString()+",");
                writer.write(roomManageModel.getValueAt(i, 4).toString());
                writer.newLine();
            }
    }catch (IOException e){
        e.printStackTrace();
    }

    try(BufferedWriter writer = new BufferedWriter(new FileWriter(Receptionist.checkInRoomsDirectory))){
        for(int i = 0; i < roomManageModel.getRowCount(); i++){
            writer.write(roomManageModel.getValueAt(i, 0).toString()+",");
            writer.write(roomManageModel.getValueAt(i, 1).toString()+",");
            writer.write(roomManageModel.getValueAt(i, 2).toString()+",");
            writer.write(roomManageModel.getValueAt(i, 3).toString()+",");
            writer.write(roomManageModel.getValueAt(i, 4).toString());
            writer.newLine();
        }
    }catch (IOException e){
        System.out.println("Error loading rooms" + e.getMessage());
    }
    }


    public Manager(){

        frame = new JFrame();
        frame.setTitle("Manager");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        container = frame.getContentPane();
        container.setLayout(new BorderLayout());

        cardPanel = new JPanel();
        cardPanel.setLayout(cardLayout);

        managementPanel = new JPanel();
        managementPanel.setLayout(null);

        addRoomPanel = new JPanel();
        addRoomPanel.setLayout(null);

        cardPanel.add(managementPanel, "ManagementPanel");
        cardPanel.add(addRoomPanel, "AddRoomPanel");

        managementPanel.setBackground(backgroundColor);
        addRoomPanel.setBackground(backgroundColor);
        addRoomPanel.add(imageLabel);
        imageLabel.setBounds(0, 0, 800, 370);

        back = new JButton("Back");
        back.setBounds(340, 500, 80, 30);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "ManagementPanel");
            }
        });

        logout = new JButton("Logout");
        logout.setBounds(650, 365, 80, 30);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginPage();
            }
        });

        goToAddRoomPanel = new JButton("Add Room");
        goToAddRoomPanel.setBounds(10, 365, 100, 30);
        goToAddRoomPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "AddRoomPanel");
            }
        });

        addRoom = new JButton("Add");
        addRoom.setBounds(650, 430, 80, 30);
        addRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfRoomNumber.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Room Number cannot be empty");
                    return;
                }
                try {
                    int RoomNum = Integer.parseInt(tfRoomNumber.getText());
                    String Type = (String) RoomType.getSelectedItem();
                    boolean Maintain = Maintenance.getSelectedItem().equals("Ready");
                    addRoom(RoomNum, Type, Maintain);
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(frame, "Room Number must be a number");
                    return;
                }



                try(BufferedWriter writer = new BufferedWriter(new FileWriter(Receptionist.checkInRoomsDirectory, true))){
                    String[] roomDetails;
                }catch (IOException ex){
                    System.out.println("Error loading rooms" + ex.getMessage());
                }

            }

        });

        btnUpdateRoom = new JButton("Update Room");
        btnUpdateRoom.setBounds(115, 365, 150, 30);
        btnUpdateRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
                updateRoom();
            }
        });

        removeRoom = new JButton("Remove");
        removeRoom.setBounds(270, 365, 100, 30);
        removeRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeRoom();
            }
        });

        roomNumberLabel = new JLabel("Room Number");
        roomNumberLabel.setBounds(50, 400, 100, 30);

        tfRoomNumber = new JTextField();
        tfRoomNumber.setBounds(50, 430, 100, 30);

        roomTypeLabel = new JLabel("Room Type");
        roomTypeLabel.setBounds(335, 400, 100, 30);

        RoomType = new JComboBox<>();
        RoomType.setBounds(335, 430, 100, 30);
        RoomType.addItem("Single");
        RoomType.addItem("Double");
        RoomType.addItem("Triple");
        RoomType.addItem("King");
        RoomType.addItem("Master Suite");

        statusLabel = new JLabel("Status");
        statusLabel.setBounds(200, 400, 100, 30);

        Status = new JTextField("Available");
        Status.setBounds(200, 430, 100, 30);
        Status.setEditable(false);

        maintenanceLabel = new JLabel("Maintenance");
        maintenanceLabel.setBounds(480, 400, 100, 30);

        Maintenance = new JComboBox();
        Maintenance.setBounds(480, 430, 140, 30);
        Maintenance.addItem("Under Maintenance");
        Maintenance.addItem("Ready");

        roomManageModel = new DefaultTableModel(columnNamesRooms, 0);


        try(BufferedReader reader = new BufferedReader(new FileReader(Receptionist.roomsDirectory))){
            String line;
            while((line = reader.readLine()) != null){
                String[] roomDetails = line.split(",");
                roomManageModel.addRow(roomDetails);
            }
        }catch (IOException e){
            System.out.println("Error loading rooms" + e.getMessage());
        }

        ManagementTable = new JTable(roomManageModel);
        ManagementTable.setBounds(10, 100, 760, 250);
        ManagementTable.setDefaultEditor(Object.class, null);

        scrollPane = new JScrollPane(ManagementTable);
        scrollPane.setBounds(10, 100, 760, 250);

        welcomeLabel = new JLabel("Welcome, Manager");
        welcomeLabel.setBounds(10, 50, 200, 30);

        addRoomPanel.add(back);

        managementPanel.add(logout);

        managementPanel.add(goToAddRoomPanel);

        managementPanel.add(removeRoom);

        managementPanel.add(btnUpdateRoom);

        managementPanel.add(scrollPane);

        addRoomPanel.add(roomNumberLabel);

        addRoomPanel.add(tfRoomNumber);

        addRoomPanel.add(roomTypeLabel);

        addRoomPanel.add(RoomType);

        addRoomPanel.add(statusLabel);

        addRoomPanel.add(Status);

        addRoomPanel.add(maintenanceLabel);

        addRoomPanel.add(Maintenance);

        addRoomPanel.add(addRoom);

        managementPanel.add(welcomeLabel);

        container.add(cardPanel, BorderLayout.CENTER);


        frame.setVisible(true);


        cardLayout.show(cardPanel, "ManagementPanel");


        tfRoomNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

    }
//    public static void main(String[] args) {
//        invokeLater(Manager::new);
//
//  }
}

