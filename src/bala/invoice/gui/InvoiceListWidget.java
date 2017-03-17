package bala.invoice.gui;


import bala.invoice.db.ColumnNamesCountIncorrectException;
import bala.invoice.db.DBUtilities;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
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
public class InvoiceListWidget extends JPanel {

    private JButton refreshButton;
    private JButton backButton;
    private JTable table;

    public InvoiceListWidget() {
        super(new MigLayout(
                "",//Layout constraints
                "grow",
                "grow"));
        try {


            backButton = new JButton("Back To Sales Entry");
            refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    loadListFromDB();
                }
            });
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT `invno` AS 'Invoice No',`invdate`  AS 'Date',`name` AS 'Buyer',"
                    + " `bill_value`  AS 'Invoice Value' FROM `invoices`"
                    + " INNER JOIN `buyers` ON `invoices`.`buyerID`=`buyers`.`buyerID`");
            Object[] columnNamesArray = {"Invoice No", "Date", "Buyer", "Invoice Value"};
            DefaultTableModel model = DBUtilities.buildTableModel(rs, columnNamesArray);
            table = new JTable(model);




            table.setColumnSelectionAllowed(false);
            JScrollPane scroller = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            //  table.setSelectionBackground(Color.black)
            add(scroller, "span,wrap,grow");
            add(backButton);
            add(refreshButton);
        } catch (ColumnNamesCountIncorrectException ex) {
            Logger.getLogger(InvoiceListWidget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(InvoiceListWidget.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void refresh(){
        refreshButton.doClick();
    }
    public void loadListFromDB() {
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
           // ResultSet rs = stmt.executeQuery("SELECT `invno`,`invdate`,`bill_value` FROM `invoices`");
             ResultSet rs = stmt.executeQuery(
                    "SELECT `invno` AS 'Invoice No',`invdate`  AS 'Date',`name` AS 'Buyer',"
                    + " `bill_value`  AS 'Invoice Value' FROM `invoices`"
                    + " INNER JOIN `buyers` ON `invoices`.`buyerID`=`buyers`.`buyerID`");
            Object[] columnNamesArray = {"Invoice No", "Date", "Buyer", "Invoice Value"};
            DefaultTableModel model = DBUtilities.buildTableModel(rs, columnNamesArray);
           // DefaultTableModel model = DBUtilities.buildTableModel(rs);
            table.setModel(model);

        } catch (Exception ex) {
            Logger.getLogger(InvoiceListWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the backButton
     */
    public JButton getBackButton() {
        return backButton;
    }

    /**
     * @return the table
     */
    public JTable getTable() {
        return table;
    }
}
