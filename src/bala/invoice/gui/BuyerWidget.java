package bala.invoice.gui;

import bala.invoice.db.ColumnNamesCountIncorrectException;
import bala.invoice.db.DBUtilities;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ANNAIENG
 */
public class BuyerWidget extends JPanel {

    JDialog dlg;
    boolean addBuyer = false;
    private JButton refreshButton;
    private JButton backButton, addButton;
    private JTable table;
    private DefaultTableModel model;
    //in model frame used for editing
    private JTextField nameField, theTINNoField, theCSTField, emailField;//= new JTextField();
    private JTextArea addrArea;
    private JButton saveButton, deleteButton, closeButton;
    private JPanel editPane;//=new JPanel();
    private String savedName = null;

    public BuyerWidget() {
        super(new MigLayout(
                "",//Layout constraints
                "grow",//col constrints
                "[top]" //row constraints
        ));
        try {

            backButton = new JButton("Back To Sales Entry");
            backButton.setIcon(new ImageIcon(this.getClass().getResource("back24.png")));

            addButton = new JButton("Add A New Buyer");
            addButton.setIcon(new ImageIcon(this.getClass().getResource("new_icon24.png")));
            addButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    addBuyer = true;
                    createEditDialog();
                    nameField.setEditable(true);
                    dlg.setContentPane(editPane);
                    dlg.setVisible(true);
                    validate();
                }
            });

            refreshButton = new JButton("Refresh");
            refreshButton.setIcon(new ImageIcon(this.getClass().getResource("refresh24.png")));
            refreshButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    createTableModel();
                    table.setModel(model);
                }
            });
           createTableModel();
            table = new JTable(model);
           
            table.setColumnSelectionAllowed(false);
            JScrollPane scroller = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            //  table.setSelectionBackground(Color.black)
            add(scroller, "span,wrap,grow");
            add(backButton);
            add(addButton);
            add(refreshButton);
        }catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(InvoiceListWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        table.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                //  throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //not a new buyer
                    addBuyer = false;
                    createEditDialog();
                    // createEditPane();
                    nameField.setEditable(true);
                    nameField.setText("two");
                    int row = table.rowAtPoint(e.getPoint());
                    // int col=table.columnAtPoint(e.getPoint());
                    int bID = Integer.parseInt("" + table.getValueAt(row, 0));
                    savedName = "" + table.getValueAt(row, 1);
                    loadValuesFromDB(bID);
                }
                // throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    public void refresh() {
        this.refreshButton.doClick();
    }

    public void createTableModel() {
        try {
            // loadListFromDB();
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT `buyerID`,`name`, `address` FROM `buyers` ORDER BY `name`");
            //+ " INNER JOIN `buyers` ON `invoices`.`buyerID`=`buyers`.`buyerID`");
            Object[] columnNamesArray = {"BuyerID", "Name", "Address"};
            model = DBUtilities.buildTableModel(rs, columnNamesArray);
            
        } catch (Exception ex) {
            Logger.getLogger(BuyerWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createEditPane() {
        editPane = new JPanel();
        editPane.setLayout(new MigLayout(
                "",
                "[][grow]",//col constraints
                "[top][top]20[top]20[top]"//row constraints
        ));
        JLabel label = new JLabel("Name:");
        editPane.add(label);
        nameField = new JTextField();

        editPane.add(nameField, "grow,wrap,span");
        label = new JLabel("Address:");
        editPane.add(label);
        addrArea = new JTextArea();
        addrArea.setMinimumSize(new Dimension(400, 100));
        addrArea.setMaximumSize(new Dimension(60000, 100));
        editPane.add(addrArea, "wrap,grow,span");
        label = new JLabel("TINNo:");
        editPane.add(label);
        theTINNoField = new JTextField();
        editPane.add(theTINNoField, "grow,wrap,span");
        label = new JLabel("CSTNo:");
        editPane.add(label);
        theCSTField = new JTextField();
        editPane.add(theCSTField, "grow,wrap,span");
        saveButton = new JButton("Save");
        saveButton.setIcon(new ImageIcon(this.getClass().getResource("save24.png")));
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection conn = DBUtilities.getConnection();
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("SET AUTOCOMMIT=0");
                    stmt.executeUpdate("START TRANSACTION");
                    String query = "SELECT *  FROM `buyers` WHERE `name`='" + nameField.getText() + "'";
                    System.out.println(query);
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next() && addBuyer == true) {
                        JOptionPane.showMessageDialog(BuyerWidget.this, "Buyer with  name "
                                + nameField.getText() + " already exists");
                        return;

                    }
                    rs.beforeFirst();
                    if (rs.next()) {
                        query = " UPDATE  `buyers` SET `address`='" + addrArea.getText() + "',`name`='" + nameField.getText() + "',`TINNo`='" + theTINNoField.getText() + "',`CSTNo`='"
                                + theCSTField.getText() + "'" + " WHERE `name`='" + nameField.getText() + "'";

                    } else {
                        query = " INSERT INTO  `buyers`  (`name`,`address`,`TINNo` ,`CSTNo`) VALUES ('"
                                + nameField.getText() + "','" + addrArea.getText() + "','" + theTINNoField.getText() + "','" + theCSTField.getText() + "')";
                        // System.out.println(query);
                    }
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    stmt.executeUpdate("COMMIT");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(BuyerWidget.class.getName()).log(Level.SEVERE, null, ex);
                }
                BuyerWidget.this.refresh();

            }
        });
        editPane.add(saveButton);
        deleteButton = new JButton("Delete");
        deleteButton.setIcon(new ImageIcon(this.getClass().getResource("delete24.png")));
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (JOptionPane.showConfirmDialog(null, "Delete this Buyer (Buyer Name : " + nameField.getText() + ")?", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                    };
                    Connection conn = DBUtilities.getConnection();
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("SET AUTOCOMMIT=0");
                    stmt.executeUpdate("START TRANSACTION");
                    String query = "DELETE   FROM `buyers` WHERE `name`='" + nameField.getText() + "'";
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    stmt.executeUpdate("COMMIT");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(BuyerWidget.class.getName()).log(Level.SEVERE, null, ex);
                }
                BuyerWidget.this.refresh();
            }

        });
        editPane.add(deleteButton);
        closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();

            }
        });
        editPane.add(closeButton);
        //  nameField.setText("one");

    }

    public void createEditDialog() {
        dlg = new JDialog((Frame) BuyerWidget.this.getParent().getParent().getParent(), "Add", true, null);
        Frame f = (Frame) BuyerWidget.this.getParent().getParent().getParent();
        dlg.setLocationRelativeTo(f);
        dlg.setLocation(f.getLocationOnScreen().x + 20, f.getLocationOnScreen().y + 20);
        dlg.setTitle("Add/Edit Buyers");
        dlg.setModal(true);
        dlg.setSize(f.getWidth() - 40, f.getHeight() - 40);
        createEditPane();
        // dlg.setContentPane(editPane);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //dlg.setVisible(true);
    }

    public void loadValuesFromDB(int buyerID) {
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM `buyers` WHERE `buyerID`='" + buyerID + "'";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                System.out.println("Result in buyers");
                final String name = rs.getString("name");
                System.out.println("Name in buyers" + name);
                final String address = rs.getString("address");
                final String tin = rs.getString("TINNo");
                final String cst = rs.getString("CSTNo");
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        nameField.setText(name);

                        addrArea.setText(address);
                        theTINNoField.setText(tin);
                        theCSTField.setText(cst);
                        //          add(editPane);
                        //        BuyerWidget.this.add(editPane);
                        // validate();
                        dlg.setContentPane(editPane);
                        dlg.setVisible(true);
                        validate();
                    }
                });

            }
        } catch (Exception ex) {
            ex.printStackTrace();;
            Logger.getLogger(BuyerWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the backButton
     */
    public JButton getBackButton() {
        return backButton;
    }
}
