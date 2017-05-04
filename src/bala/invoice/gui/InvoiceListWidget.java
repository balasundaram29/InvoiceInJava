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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
//IMPORTAnt
//SalesGui sets BackButton Behaviour through getBackButtton
//SalesGui sets InvoiceListWidget's Table Behaviour useing MouseListener
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ANNAIENG
 */
public class InvoiceListWidget extends JPanel {

    JLabel yearLabel;
    private JButton refreshButton;
    private JButton backButton;
    private JTable table;
    private String yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";

    public InvoiceListWidget() {
        super(new MigLayout(
                "",//Layout constraints
                "grow",
                "grow"));
        try {

            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                yearLabel = new JLabel("Financial Year : None Selected");
            } else {
                yearLabel = new JLabel("Financial Year : " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
                yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";
            }

            add(yearLabel, "grow,wrap");
            backButton = new JButton("Back To Sales Entry");
            backButton.setIcon(new ImageIcon(this.getClass().getResource("back24.png")));

            refreshButton = new JButton("Refresh");
            refreshButton.setIcon(new ImageIcon(this.getClass().getResource("refresh24.png")));
            refreshButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                        yearLabel.setText("Financial Year : None Selected");
                    } else {
                        yearLabel.setText("Financial Year : " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
                        yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";
                    }
                    loadListFromDB();
                }
            });
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT `invno` AS 'Invoice No',DATE_FORMAT(`invdate`,'%d-%m-%Y')  AS 'Date',`name` AS 'Buyer',"
                    + " `bill_value`  AS 'Invoice Value' FROM `invoices`"
                    + " INNER JOIN `buyers` ON `invoices`.`buyerID`=`buyers`.`buyerID`"
                    + "WHERE `invdate` "
                    + yearString
                    + "ORDER BY `invdate`,`invno`"
            );
            Object[] columnNamesArray = {"Invoice No", "Date", "Buyer", "Invoice Value"};
            DefaultTableModel model = DBUtilities.buildTableModel(rs, columnNamesArray);
            //SalesGui sets InvoiceListWidget's Table's Behaviour useing MouseListener
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

    public void refresh() {
        refreshButton.doClick();
    }

    public void loadListFromDB() {
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            // ResultSet rs = stmt.executeQuery("SELECT `invno`,`invdate`,`bill_value` FROM `invoices`");
            ResultSet rs = stmt.executeQuery(
                    "SELECT `invno` AS 'Invoice No',DATE_FORMAT(`invdate`,'%d-%m-%Y')  AS 'Date',`name` AS 'Buyer',"
                    + " `bill_value`  AS 'Invoice Value' FROM `invoices`"
                    + " INNER JOIN `buyers` ON `invoices`.`buyerID`=`buyers`.`buyerID`"
                    + "WHERE `invdate` "
                    + yearString
                    + "ORDER BY `invdate`,`invno`");
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
