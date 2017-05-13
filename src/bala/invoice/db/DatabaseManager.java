package bala.invoice.db;

import bala.invoice.gui.InvoiceConstants;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ANNAIENG
 */
public class DatabaseManager {

    public static void createDatabaseAndTables() {
        //Statement stmt = conn.createStatement();
        try {
            String databaseName = "InvoiceDB";
            String userName = "root";
            String password = "";

            String url = "jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=convertToNull";
            Connection connection = DriverManager.getConnection(url, userName, password);

            String sql = "CREATE DATABASE " + databaseName;

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

            //sql = "DROP DATABASE  IF EXISTS InvoiceDB";
            // statement.executeUpdate(sql);
            statement.close();
            JOptionPane.showMessageDialog(null, databaseName + " Database has been created successfully", "System Message", JOptionPane.INFORMATION_MESSAGE);
            connection.close();
            //get connection using utilities
            Connection conn = DBUtilities.getConnection();
            statement = conn.createStatement();
            statement.executeUpdate("SET AUTOCOMMIT=0");
            statement.executeUpdate("START TRANSACTION");


            /*    "DROP TABLE  IF EXISTS Particulars")
             #query.exec_("DROP TABLE  IF EXISTS Invoices")
             #query.exec_("DROP TABLE  IF EXISTS Products")
             #query.exec_("DROP TABLE  IF EXISTS Buyers")*/
            sql = "CREATE TABLE IF NOT EXISTS Buyers "
                    + "("
                    + "buyerID INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(40) NOT NULL UNIQUE,"
                    + "address VARCHAR(150),"
                    + "TINNo VARCHAR(25) ,"
                    + "CSTNo VARCHAR(25) , "
                    + "email VARCHAR(60)"
                    + ")ENGINE=InnoDB";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Products"
                    + "("
                    + "productID INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(40) NOT NULL UNIQUE,"
                    + "unit VARCHAR(20),"
                    + "type VARCHAR(15)"
                    + ")ENGINE=InnoDB";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Invoices"
                    + "("
                    + "invno INTEGER PRIMARY KEY,"
                    + "invdate DATE NOT NULL,"
                    + "buyerID INTEGER, "
                    + "cstrate DECIMAL(5,2),"
                    + "scrate DECIMAL(5,2),"
                    + "other INTEGER,"
                    + "bill_value INTEGER,"
                    + "tax_type  VARCHAR(6),"
                    + "form_c CHAR(1),"
                    + "FOREIGN KEY(buyerID) REFERENCES Buyers(buyerID)"
                    + ")ENGINE=InnoDB";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Particulars"
                    + "("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "invno INTEGER NOT NULL,"
                    + "productID INTEGER NOT NULL,"
                    + "quantity INTEGER NOT NULL,"
                    + "price DECIMAL(10,2),"
                    + "FOREIGN KEY(invno) REFERENCES Invoices(invno),"
                    + "FOREIGN KEY(productID) REFERENCES Products(productID)"
                    + ")ENGINE=InnoDB";
            statement.executeUpdate(sql);
            /*   sql = "CREATE TRIGGER one_invno_per_finyear"
             + "("
             + "BEFORE INSERT ON Invoices FOR EACH ROW"
             + "BEGIN \n"
             + "IF EXISTS(SELECT 1 FROM Invoices WHERE `invno`=NEW.`invno` AND YEAR(DATE_SUB(`invdate`,INTERVAL 3 MONTH))=YEAR(DATE_SUB(NEW.`invdate`,INTERVAL 3 MONTH);\n"
             + " THEN SIGNAL = SQLSTATE='45000' \n"
             + "SET MESSAGE_TEXT = 'Cannot add this invoice no : You can use one invoice no only once in financial year'\n"
             + "ENDIF ;\n"
             + "END";
             statement.executeUpdate(sql);*/

            statement.executeUpdate("COMMIT");
            JOptionPane.showMessageDialog(null, databaseName + " Tables have been created successfully", "System Message", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createTriggersOnly(Connection conn) {
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate("SET AUTOCOMMIT=0");
            statement.executeUpdate("START TRANSACTION");
            String sql = "delimiter $$\n"
                    + "CREATE TRIGGER one_invno_per_finyear\n"
                    + "BEFORE INSERT ON Invoices FOR EACH ROW\n"
                    + "BEGIN \n"
                    + "IF EXISTS(SELECT 1 FROM Invoices WHERE `invno`=NEW.`invno` AND YEAR(DATE_SUB(`invdate`,INTERVAL 3 MONTH))=YEAR(DATE_SUB(NEW.`invdate`,INTERVAL 3 MONTH)))\n"
                    + " THEN SIGNAL SQLSTATE '45000' \n"
                    + "SET MESSAGE_TEXT = 'Cannot add this invoice no : You can use one invoice no only once in financial year';\n"
                    + "END IF; \n"
                    + "END;$$\n";
            System.out.print(sql);
            PrintWriter writer = new PrintWriter("filename.sql");
            writer.print(sql);
            writer.close();
            statement.executeUpdate(sql);
            statement.executeUpdate("COMMIT");
        } catch (Exception ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteInvoice(Connection conn, int invNo) {
        try {
             String yearString = "";
            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                return;//yearLabel = new JLabel("Financial Year : None Selected");
            } else {
                yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";
            }
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("SET AUTOCOMMIT=0");
            stmt.executeUpdate("START TRANSACTION");
            String query = "DELETE   FROM `invoices` WHERE "
                    + "`invno` = " + "\'" + invNo + "\'"+
                    " AND `invdate` "+yearString ;

            stmt.executeUpdate(query);
            stmt.executeUpdate("COMMIT");
        } catch (Exception ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveValues(Connection conn, HashMap widgetValues, HashMap<String, Integer> productMap) {
        try {
            String yearString = "";
            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                return;//yearLabel = new JLabel("Financial Year : None Selected");
            } else {
                yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";
            }
            Statement stmt = conn.createStatement();

            String query = "SELECT * FROM `invoices` WHERE `invno` = " + "\'"
                    + widgetValues.get(InvoiceConstants.INV_NO_KEY) + "\'"
                     + "AND `invdate` " + yearString;
            ResultSet rs = stmt.executeQuery(query);
            //  query.exec_(s);
            if (rs.next()) {
                int result = JOptionPane.showConfirmDialog(null, "Invoice no already exists. Overwrite?");
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            stmt.executeUpdate("SET AUTOCOMMIT=0");
            stmt.executeUpdate("START TRANSACTION");
           // query = "DELETE   FROM `Particulars` WHERE "
            //     + "`invno` = " + "\'" + widgetValues.get(InvoiceConstants.INV_NO_KEY) + "\'";

            //  stmt.executeUpdate(query);
            
            
            query = "DELETE   FROM `invoices` WHERE "
                    + "`invno` = " + "\'" + widgetValues.get(InvoiceConstants.INV_NO_KEY) + "\'"
                    + "AND `invdate` " + yearString;

            stmt.executeUpdate(query);
            Date dateR = (Date) widgetValues.get(InvoiceConstants.INV_DATE_KEY);
            String dateOfInv = (dateR.getYear() + 1900) + "-" + (dateR.getMonth() + 1) + "-" + (dateR.getDate());
            System.out.println("Date found from HashMap is " + dateOfInv);

            query = "INSERT INTO `invoices`"
                    + " (`invno`,`invdate`,`buyerID`,`cstrate`,`scrate`, `bill_value`,`tax_type`,`form_c`,`remarks`)"
                    + " VALUES  ("
                    + "'" + widgetValues.get(InvoiceConstants.INV_NO_KEY) + "',"
                    + "'" + dateOfInv + "'," + "'" + widgetValues.get(InvoiceConstants.BUYER_ID_KEY) + "',"
                    + "'" + widgetValues.get(InvoiceConstants.TAX_RATE_KEY) + "',"
                    + "'0' ," + "'" + widgetValues.get(InvoiceConstants.GRAND_TOTAL_KEY) + "','"
                    + widgetValues.get(InvoiceConstants.TAX_TYPE_KEY) + "','"
                    + widgetValues.get(InvoiceConstants.FORM_C_KEY) + "','"
                    + widgetValues.get(InvoiceConstants.REMARKS_KEY)
                    + "')";

            //CREATE TRIGGER one_invno_per_finyear
            //BEFORE INSERT ON Invoices FOR EACH ROW
            //BEGIN
//IF EXISTS(SELECT 1 FROM Invoices WHERE `invno`=new.`invno` AND YEAR(DATE_SUB(`invdate`,INTERVAL 3 MONTH))=YEAR(DATE_SUB(new.`invdate`,INTERVAL 3 MONTH);
            // THEN
            // SIGNAL = SQLSTATE='45000'
            //SET MESSAGE_TEXT = "Cannot add this invoice no : You can use one invoice no only once in financial year"
            //ENDIF ;
//END           //END   
// IF (`invno`=new.`invno` AND YEAR(DATE_SUB(invdate,INTERVAL 3 MONTH))=YEAR(DATE_SUB(invdate,INTERVAL 3 MONTH));
            //DECLARE `invyear` AS YEAR;
            //SELECT YEAR(new.`invdate`)INTO `invyear`;
            //SELECT 
            System.out.println(query);
            stmt.executeUpdate(query);
            query = "SELECT LAST_INSERT_ID()";
            rs = stmt.executeQuery(query);
            Integer lastInsertID = -1;
            if (rs.next()) {
                lastInsertID = rs.getInt(1);
            }
            for (ArrayList row : (ArrayList<ArrayList>) widgetValues.get(InvoiceConstants.ITEM_LIST_KEY)) {

                query = "INSERT INTO `Particulars` (`invoiceID`,`productID`,`quantity`,`price`)"
                        + " VALUES ("
                        + "'" + lastInsertID + "',"
                        // + "'" + widgetValues.get(InvoiceConstants.INV_NO_KEY) + "',"
                        + "'" + productMap.get(row.get(InvoiceConstants.DESC_COL)) + "',"
                        + "'" + row.get(InvoiceConstants.QTY_COL) + "',"
                        + "'" + row.get(InvoiceConstants.PRICE_COL) + "')";
                System.out.println(query);
                stmt.executeUpdate(query);
            }
            stmt.executeUpdate("COMMIT");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadValuesIntoProductMap(Connection conn, HashMap<String, Integer> productMap) {

    }

    public void loadValuesIntoHashMapFromDB(Connection conn, HashMap widgetValues, HashMap<String, Integer> productMap, HashMap<String, Integer> buyerMap) {
        try {
String yearString = "";
            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                return;//yearLabel = new JLabel("Financial Year : None Selected");
            } else {
                yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";
            }
            Statement stmt = conn.createStatement();

            String query = "SELECT * FROM `Invoices` WHERE `invno` = " + "'"
                    + widgetValues.get(InvoiceConstants.INV_NO_KEY) + "'"+
                    "AND `invdate`"+yearString;
            ResultSet rs = stmt.executeQuery(query);
            //  query.exec_(s);
            Integer InvoiceID = -1;
            if (rs.next()) {
                //int result = JOptionPane.showConfirmDialog(null, "Invoice no already exists. Overwrite?");
                //if (result != JOptionPane.YES_OPTION) {
                //   return;
                //}
                //`buyerID`,`cstrate`,`scrate`, `bill_value`,`tax_type`,`form_c`
                InvoiceID = rs.getInt("InvoiceID");
                widgetValues.put(InvoiceConstants.INV_NO_KEY, String.valueOf(rs.getInt("invno")));
                widgetValues.put(InvoiceConstants.INV_DATE_KEY, rs.getDate("invdate"));
                widgetValues.put(InvoiceConstants.BUYER_ID_KEY, rs.getInt("buyerID"));
                widgetValues.put(InvoiceConstants.BUYER_NAME_KEY, getBuyerName(buyerMap, rs.getInt("buyerID")));
                widgetValues.put(InvoiceConstants.TAX_RATE_KEY, rs.getDouble("cstrate"));
                widgetValues.put(InvoiceConstants.TAX_TYPE_KEY, rs.getString(("tax_type")));
                widgetValues.put(InvoiceConstants.FORM_C_KEY, rs.getString(("form_c")));
                widgetValues.put(InvoiceConstants.REMARKS_KEY, rs.getString(("remarks")));

            } else {
                return;
            }

            query = "SELECT *  FROM `Particulars` WHERE "
                    + "`invoiceID` = " + "\'" + InvoiceID + "\'";
            rs = stmt.executeQuery(query);

            ArrayList itemsList = new ArrayList<ArrayList>();
            int rowIndex = 0;
            while (rs.next())// : ArrayList<ArrayList>) widgetValues.get(InvoiceConstants.ITEM_LIST_KEY)) {
            //query = "INSERT INTO `Particulars` (`invno`,`productID`,`quantity`,`price`)"
            {
                ArrayList<Object> row = new ArrayList<Object>();
                row.add(rowIndex++);
                row.add(getProductName(productMap, rs.getInt("productID")));
                int qty = rs.getInt("quantity");
                row.add(qty);
                BigDecimal price = new BigDecimal(rs.getDouble("price")).setScale(2, RoundingMode.HALF_UP);
                row.add(price);
                BigDecimal amount = (price.multiply(new BigDecimal(qty))).setScale(2, RoundingMode.HALF_UP);
                row.add(amount);
                itemsList.add(row);
                System.out.println(row);

            }
            widgetValues.put(InvoiceConstants.ITEM_LIST_KEY, itemsList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getProductName(HashMap<String, Integer> productMap, int id) {
        for (String key : productMap.keySet()) {
            if (productMap.get(key) == id) {
                return key;
            }
        }
        return null;
    }

    public String getBuyerName(HashMap<String, Integer> buyerMap, int id) {
        for (String key : buyerMap.keySet()) {
            if (buyerMap.get(key) == id) {
                return key;
            }
        }
        return null;
    }
}
