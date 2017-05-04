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
public class ProductWidget extends JPanel {

    JDialog dlg;
    boolean addProduct = false;
    private JButton refreshButton;
    private JButton backButton, addButton;
    private JTable table;
    private DefaultTableModel model;
    //in model frame used for editing
    private JTextField nameField, typeField, unitField, emailField;//= new JTextField();
    // private JTextArea addrArea;
    private JButton saveButton, deleteButton, closeButton;
    private JPanel editPane;//=new JPanel();
    private String savedName = null;

    public ProductWidget() {
        super(new MigLayout(
                "",//Layout constraints
                "grow",//col constrints
                "[top]" //row constraints
        ));
        try {

            backButton = new JButton("Back To Sales Entry");
            backButton.setIcon(new ImageIcon(this.getClass().getResource("back24.png")));
            backButton.setIcon(new ImageIcon(this.getClass().getResource("back24.png")));
            addButton = new JButton("Add A New Product");
            addButton.setIcon(new ImageIcon(this.getClass().getResource("new_icon24.png")));

            addButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    addProduct = true;
                    createEditDialog();
                    nameField.setEditable(true);
                    dlg.setContentPane(editPane);
                    dlg.setVisible(true);
                    validate();
                    //ProductWidget.this.refresh();
                }
            });

            refreshButton = new JButton("Refresh");
            refreshButton.setIcon(new ImageIcon(this.getClass().getResource("refresh24.png")));
            refreshButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                       createTableModel();
                        table.setModel(model);
                    } catch (Exception ex) {
                        Logger.getLogger(ProductWidget.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
        }  catch (Exception ex) {
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
                    //not a new product
                    addProduct = false;
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
        refreshButton.doClick();
    }
    public void createTableModel(){
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT `productID`,`name`,`type`, `unit` FROM `products` ORDER BY `name`");
            //+ " INNER JOIN `products` ON `invoices`.`productID`=`products`.`productID`");
            Object[] columnNamesArray = {"ProductID", "Name", "Type", "Unit"};
             model = DBUtilities.buildTableModel(rs, columnNamesArray);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(ProductWidget.class.getName()).log(Level.SEVERE, null, ex);
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

        label = new JLabel("Type:");
        editPane.add(label);
        typeField = new JTextField();
        editPane.add(typeField, "grow,wrap,span");
        label = new JLabel("Unit:");
        editPane.add(label);
        unitField = new JTextField();
        editPane.add(unitField, "grow,wrap,span");
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
                    String query = "SELECT *  FROM `products` WHERE `name`='" + nameField.getText() + "'";
                    System.out.println(query);
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(ProductWidget.this, "Product with  name "
                                + nameField.getText() + " already exists");
                        return;

                    }
                    // rs.beforeFirst();
                    if (addProduct) {

                        query = " INSERT INTO  `products`  (`name`,`type`,`unit`) VALUES ('"
                                + nameField.getText() + "','" + typeField.getText() + "','" + unitField.getText() + "')";
                    } else {
                        query = " UPDATE  `products` SET  `name`='" + nameField.getText() + "',`type`='" + typeField.getText() + "',`unit`='"
                                + unitField.getText() + "'" + " WHERE `name`='" + savedName + "'";
                        // System.out.println(query);
                    }
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    stmt.executeUpdate("COMMIT");
                    ProductWidget.this.refresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(ProductWidget.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        editPane.add(saveButton);
        deleteButton = new JButton("Delete");
        deleteButton.setIcon(new ImageIcon(this.getClass().getResource("delete24.png")));

        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (JOptionPane.showConfirmDialog(null, "Delete this Product (Product Name : " + nameField.getText() + ")?", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                    };
                    Connection conn = DBUtilities.getConnection();
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("SET AUTOCOMMIT=0");
                    stmt.executeUpdate("START TRANSACTION");
                    String query = "DELETE   FROM `products` WHERE `name`='" + nameField.getText() + "'";
                    System.out.println(query);
                    stmt.executeUpdate(query);
                    stmt.executeUpdate("COMMIT");
                    ProductWidget.this.refresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(ProductWidget.class.getName()).log(Level.SEVERE, null, ex);
                }

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
        dlg = new JDialog((Frame) ProductWidget.this.getParent().getParent().getParent(), "Add", true, null);
        Frame f = (Frame) ProductWidget.this.getParent().getParent().getParent();
        dlg.setLocationRelativeTo(f);
        dlg.setLocation(f.getLocationOnScreen().x + 20, f.getLocationOnScreen().y + 20);
        dlg.setTitle("Add/Edit Products");
        dlg.setModal(true);
        dlg.setSize(f.getWidth() - 40, f.getHeight() - 40);
        createEditPane();
        // dlg.setContentPane(editPane);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //dlg.setVisible(true);
    }

    public void loadValuesFromDB(int productID) {
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM `products` WHERE `productID`='" + productID + "'";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                System.out.println("Result in products");
                final String name = rs.getString("name");
                System.out.println("Name in products" + name);
                final String type = rs.getString("type");
                final String unit = rs.getString("unit");
                //  final String cst = rs.getString("CSTNo");
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        nameField.setText(name);

                        typeField.setText(type);
                        unitField.setText(unit);
                        //          add(editPane);
                        //        ProductWidget.this.add(editPane);
                        // validate();
                        dlg.setContentPane(editPane);
                        dlg.setVisible(true);
                        validate();
                    }
                });

            }
        } catch (Exception ex) {
            ex.printStackTrace();;
            Logger.getLogger(ProductWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the backButton
     */
    public JButton getBackButton() {
        return this.backButton;
    }
}
