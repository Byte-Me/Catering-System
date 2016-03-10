package GUI;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel statistics;
    private JPanel driver;
    private JPanel chef;
    private JPanel administration;
    private JPanel sale;
    private JButton addUserButton;
    private JTable userTable;
    private JFormattedTextField fromDate;
    private JFormattedTextField toDate;
    private JButton getStatisticsButton;
    private JButton addCustomerButton;
    private JTable customerTable;

    private static DefaultTableModel model;

    public MainWindow(int userType) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create GUI for specific user type
        switch (userType) {
            case 0:
                // Admin have access to everything
                break;
            case 1:
                // Sale
                tabbedPane1.remove(administration);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(driver);
                break;
            case 2:
                // Chef
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(driver);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            case 3:
                // Driver
                tabbedPane1.remove(statistics);
                tabbedPane1.remove(chef);
                tabbedPane1.remove(administration);
                tabbedPane1.remove(sale);
                break;
            default:
        }

        // Setup the different panels
        setupAdministration();
        setupStatistics();
        setupSale();

        pack();
        setSize(800, 400);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void setupAdministration() {

        addUserButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                new AddUser(mainPanel.getParent());
            }
        });

        String[] header = {"First Name", "Last Name", "Email", "Username", "User Type"}; // Header titles

        model = new DefaultTableModel(); // Model of the table
        model.setColumnIdentifiers(header); // Add header to columns

        userTable.setModel(model); // Add model to table

        // TODO - testdata (remove)
        model.addRow(new Object[]{"Ole Kristian", "Aune", "noe@noe.com", "ole", "Admin"});
        model.addRow(new Object[]{"Even", "Dalen", "noeannet@noeannet.com", "even", "Admin"});

    }

    private void setupSale() {

        addCustomerButton.addActionListener(new ActionListener() { // Button action listener
            public void actionPerformed(ActionEvent e) {
                //new AddUser(mainPanel.getParent());
            }
        });

        String[] header = {"Name", "Email", "Phone"}; // Header titles

        model = new DefaultTableModel(); // Model of the table
        model.setColumnIdentifiers(header); // Add header to columns

        customerTable.setModel(model); // Add model to table

        // TODO - testdata (remove)
        model.addRow(new Object[]{"Some Curporation LTD", "noe@noe.com", "45987700"});
        model.addRow(new Object[]{"Johan Olsen", "noeannet@noeannet.com", "91482099"});

    }

    private void setupStatistics() {

        try {
            final MaskFormatter maskFormatter = new MaskFormatter("##/##/##"); // Defining format pattern
            //maskFormatter.setPlaceholderCharacter('_');
            maskFormatter.setPlaceholder("00-00-00"); // Placeholder

            fromDate.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter;
                }
            });

            toDate.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter;
                }
            });

        } catch(Exception e) {
            System.err.println(e);
        }


        getStatisticsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fDate = fromDate.getText();
                String tDate = toDate.getText();

                System.out.println("From: " + fDate + " To: " + tDate);
            }
        });


    }

    //FIXME - logikk skal ikke ligge her, kun GUI
    public static void addUser(String fName, String lName, String email, String uName, int uNum) {
        String uType = "";
        switch (uNum) {
            case 0:
                uType = "Admin";
                break;
            case 1:
                uType = "Sale";
                break;
            case 2:
                uType = "Chef";
                break;
            case 3:
                uType = "Driver";
                break;
        }
        model.addRow(new Object[]{fName, lName, email, uName, uType});
    }
}
