/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bala.invoice.gui;

import bala.invoice.db.DBUtilities;
import java.awt.Dimension;
import java.awt.Frame;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author admin
 */
public class YearWidget extends JPanel {

    JDialog dlg;
    private JButton backButton, addButton, refreshButton, saveButton, closeButton, deleteButton;
    private JTable table;
    private JTextField nameField;
    JPanel editPane;
    JTextField yearField;
    boolean addYear;
    private DefaultTableModel model;

    public YearWidget() {
        super(new MigLayout(
                "",//Layout constraints
                "grow",//col constraints
                "[top]" //row constraints
        ));
        try {

            backButton = new JButton("Back To Sales Entry");
            backButton.setIcon(new ImageIcon(this.getClass().getResource("back24.png")));

            addButton = new JButton("Add A New Year");
            addButton.setIcon(new ImageIcon(this.getClass().getResource("new_icon24.png")));
            addButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    createEditPane();
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
            deleteButton = new JButton("Delete");
            deleteButton.setIcon(new ImageIcon(this.getClass().getResource("delete24.png")));
            deleteButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int row = table.getSelectedRow();
                        // int col=table.columnAtPoint(e.getPoint());
                        int yr = Integer.parseInt(((String) table.getValueAt(row, 0)).substring(0, 4));
                        if (yr == InvoiceConstants.FIN_YEAR_FIRST_PART) {
                            JOptionPane.showMessageDialog(null, "Cannot delete active financial year.Please change the active financial year to any other year before deletion.!");
                            return;
                        }
                        if (JOptionPane.showConfirmDialog(null, "Delete Financial Year " + yr + "-" + (yr + 1) + "?", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                            return;
                        };

                        Connection conn = DBUtilities.getConnection();
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("SET AUTOCOMMIT=0");
                        stmt.executeUpdate("START TRANSACTION");

                        String query = "DELETE FROM `Years` WHERE `finYearFirstPart`='" + yr + "'";
                        stmt.executeUpdate(query);
                        stmt.executeUpdate("COMMIT");
                        JOptionPane.showMessageDialog(null, "Financial year  " + yr + "-" + (yr + 1) + " deleted!");
                        refresh();
                    } catch (SQLException ex) {
                        Logger.getLogger(YearWidget.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            });
            // refresh();
            createTableModel();
            table = new JTable(model);
            //table.setModel(model);
            table.setColumnSelectionAllowed(false);
            JScrollPane scroller = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            table.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    if (e.getClickCount() == 2) {
                        //not a new product

                        int row = table.rowAtPoint(e.getPoint());
                        // int col=table.columnAtPoint(e.getPoint());
                        int yr = Integer.parseInt(((String) table.getValueAt(row, 0)).substring(0, 4));
                        //savedName = "" + table.getValueAt(row, 1);
                        InvoiceConstants.setFinYear(yr);
                        table.removeEditor();
                        JOptionPane.showMessageDialog(null, "Financial year changed to " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
                        InvoiceEditWidget.getInstance().changeFinancialYear();
                        InvoiceEditWidget.getInstance().populateWidgets();
                        InvoiceEditWidget.getInstance().loadValuesFromHashMaps();
                        
                    }

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            //  table.setSelectionBackground(Color.black)
            add(scroller, "span,wrap,grow");
            add(backButton);
            add(addButton);
            add(refreshButton);
            add(deleteButton);

        } catch (Exception ex) {
            Logger.getLogger(YearWidget.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public void createTableModel() {
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT `finYearFirstPart` FROM `Years` ORDER BY `finYearFirstPart`");
            //+ " INNER JOIN `buyers` ON `invoices`.`buyerID`=`buyers`.`buyerID`");
            Object[] columnNamesArray = {"Year"};
            int size = 0;
            rs.last();
            size = rs.getRow();
            rs.beforeFirst();
            Object[][] yearArray = new Object[size][1];
            int row = 0;
            int val = 0;
            while (rs.next()) {
                val = rs.getInt("finYearFirstPart");
                yearArray[row][0] = "" + val + "-" + (val + 1);
                row++;
            }
            model = new DefaultTableModel(yearArray, columnNamesArray);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(YearWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh() {
        refreshButton.doClick();
    }

    public void createEditPane() {
        dlg = new JDialog((Frame) YearWidget.this.getParent().getParent().getParent(), "Add", true, null);
        Frame f = (Frame) YearWidget.this.getParent().getParent().getParent();
        dlg.setLocationRelativeTo(f);
        dlg.setLocation(f.getLocationOnScreen().x + 20, f.getLocationOnScreen().y + 20);
        dlg.setTitle("Add Years");
        dlg.setModal(true);
        dlg.setSize(f.getWidth() - 40, f.getHeight() - 40);
        addYear = true;
        editPane = new JPanel();
        editPane.setLayout(new MigLayout(
                "",
                "[][grow]",//col constraints
                "[top][top]20[top]20[top]"//row constraints
        ));
        JLabel label = new JLabel("Year:");
        editPane.add(label);
        yearField = new JTextField();

        editPane.add(yearField, "grow,wrap,span");

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
                    String query = "SELECT *  FROM `Years` WHERE `finYearFirstPart`='" + yearField.getText() + "'";
                    System.out.println(query);
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next() && addYear == true) {
                        JOptionPane.showMessageDialog(YearWidget.this, "Year  "
                                + yearField.getText() + " already exists");
                        return;

                    }
                    rs.beforeFirst();

                    query = " INSERT INTO  `Years`  (`finYearFirstPart`) VALUES ('"
                            + yearField.getText() + "'" + ")";
                    // System.out.println(query);

                    System.out.println(query);
                    stmt.executeUpdate(query);
                    stmt.executeUpdate("COMMIT");
                    InvoiceConstants.setFinYear(Integer.parseInt(yearField.getText()));
                    JOptionPane.showMessageDialog(null, "Financial year changed to " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
                    refresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.getLogger(BuyerWidget.class.getName()).log(Level.SEVERE, null, ex);
                }
                //YearWidget.this.refresh();

            }
        });
        editPane.add(saveButton);

        closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();

            }
        });
        editPane.add(closeButton);
        //  nameField.setText("one");
        dlg.setContentPane(editPane);
        dlg.setVisible(true);

    }
//See SalesGui.java for use of getBackButton();

    public JButton getBackButton() {
        return this.backButton;
    }
}
