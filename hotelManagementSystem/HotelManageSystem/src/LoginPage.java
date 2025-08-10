import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LoginPage {
    private Image image;
    private Container cp;
    private final JFrame mainFrame;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final JPanel loginPanel;

    private final JLabel lbTitleLogin;
    private final JLabel lbLoginPassword;
    private final JLabel lbLoginUsername;

    private final JPasswordField tfLoginPassword;
    private final JTextField tfLoginUsername;

    private final JButton btnLogin;
    private JButton btnCreateAccount;

    private final JRadioButton rbReceptionistLogin;
    private final JRadioButton rbManagerLogin;
    private final JRadioButton rbGeneralManagerLogin;
    private ButtonGroup bgLogin;

    private final String directory = "HotelManagementSystem\\employee.txt";
    private final Color customColor = new Color(207, 224, 197);
    private final Color loginColor = new Color(228, 172, 196);
    private final Color lilac = new Color(172, 146, 225);
    private final Color lightBlue = new Color(173, 216, 230);
    private final Font myFont;

    public LoginPage(){
        try {
            image = ImageIO.read(new File("HotelManagementSystem\\sunsetHotel.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame("Login");
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainFrame.setSize(800,700);
        mainFrame.setResizable(false);


        loginPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, 800, 700, this);
                Color overlayColor = new Color(228, 172, 196, 255);
                g.setColor(overlayColor);


                g.fillRect(200, 200, 400, 240);
            }
        };

        loginPanel.setLayout(null);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        lbTitleLogin = new JLabel("Login");
        lbTitleLogin.setBounds(375, 200, 200, 25);
        myFont = lbTitleLogin.getFont().deriveFont(Font.BOLD, 15);
        lbTitleLogin.setFont(myFont);

        lbLoginUsername = new JLabel("Username");
        lbLoginUsername.setBounds(235, 250, 200, 25);

        lbLoginPassword = new JLabel("Password");
        lbLoginPassword.setBounds(235, 300, 200, 25);

        tfLoginUsername = new JTextField(20);
        tfLoginUsername.setBounds(300, 250, 200,25);

        tfLoginPassword = new JPasswordField(20);
        tfLoginPassword.setBounds(300, 300, 200, 25);

        rbReceptionistLogin = new JRadioButton("Receptionist");
        rbReceptionistLogin.setBounds(220, 400, 100, 25);


        rbManagerLogin = new JRadioButton("Manager");
        rbManagerLogin.setBounds(450, 400, 140, 25);


        rbGeneralManagerLogin = new JRadioButton("General Manager");
        rbGeneralManagerLogin.setBounds(320, 400, 130, 25);

        rbReceptionistLogin.setOpaque(false);
        rbManagerLogin.setOpaque(false);
        rbGeneralManagerLogin.setOpaque(false);

        ButtonGroup bgLogin = new ButtonGroup();
        bgLogin.add(rbReceptionistLogin);
        bgLogin.add(rbManagerLogin);
        bgLogin.add(rbGeneralManagerLogin);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(350, 350, 100, 20);
        btnLogin.setBackground(Color.LIGHT_GRAY);



        loginPanel.add(lbTitleLogin);
        loginPanel.add(lbLoginUsername);
        loginPanel.add(tfLoginUsername);
        loginPanel.add(lbLoginPassword);
        loginPanel.add(tfLoginPassword);
        loginPanel.add(btnLogin);
        loginPanel.add(rbReceptionistLogin);
        loginPanel.add(rbManagerLogin);
        loginPanel.add(rbGeneralManagerLogin);

        cardPanel.add(loginPanel, "Login");

        mainFrame.add(cardPanel);
        mainFrame.setVisible(true);

        rbManagerLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        rbReceptionistLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        rbGeneralManagerLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(directory));
                    String line;
                    String username = tfLoginUsername.getText().trim();
                    String password = tfLoginPassword.getText().trim();
                    String RoleTemp = null;

                    if (username.isEmpty()|| password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill in the blanks", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    boolean found = false;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        try {
                            if (parts.length > 4 && (parts[2].trim()).equals(username) && (parts[3].trim()).equals(password)) {
                                RoleTemp = parts[4].trim();

                                found = true;
                                break;
                            }


                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    if ((found) && (rbReceptionistLogin.isSelected()) && (RoleTemp.equals("Receptionist"))) {
                        new Receptionist();
                        mainFrame.dispose();

                    } else if ((found) && (rbManagerLogin.isSelected()) && (RoleTemp.equals("Manager"))) {
                        new Manager();
                        mainFrame.dispose();

                    } else if ((found) && (rbGeneralManagerLogin.isSelected()) && (RoleTemp.equals("General Manager"))) {
                        new GeneralManager();
                        mainFrame.dispose();
                    } else if (!found) {
                        JOptionPane.showMessageDialog(null, "Username or password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                    }else if (!rbManagerLogin.isSelected() && !rbReceptionistLogin.isSelected() && !rbGeneralManagerLogin.isSelected()){
                        JOptionPane.showMessageDialog(null, "Please select a role", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Please choose the role correctly", "Error", JOptionPane.ERROR_MESSAGE);

                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        tfLoginUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        tfLoginPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });
    }
}

