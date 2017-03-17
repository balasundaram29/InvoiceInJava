package bala.invoice.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ANNAIENG
 */
public class DBUtilities {
   static  Connection conn = null;

    public static Connection getConnection() {

        if(conn!=null){
            return conn;
        }
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "");

        try {
            conn = DriverManager.getConnection(
                    "jdbc:" + "mysql" + "://"
                    + "localhost"
                    + ":" + "3306/" + "InvoiceDB",
                    connectionProps);

            System.out.println("Connected to database");
            return conn;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            Vector<String> columnNames = new Vector<String>();
            int cols = meta.getColumnCount();
            for (int column = 1; column <= cols; column++) {
                columnNames.add(meta.getColumnName(column));
            }
            Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
            Vector<Object> row;//=new Vector();
            while (rs.next()) {
                //System.out.println("Result set next");
                row = new Vector<Object>();
                for (int column = 1; column <= cols; column++) {
                    row.add(rs.getObject(column));
                }
                tableData.add(row);
            }
            DefaultTableModel model = new DefaultTableModel(tableData,columnNames);
            return model;

        } catch (Exception ex) {
            Logger.getLogger(DBUtilities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
     public static DefaultTableModel buildTableModel(ResultSet rs,Object[] columnNamesArray)
             throws ColumnNamesCountIncorrectException {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            Vector<Object> columnNames = new Vector<Object>();
           int  cols = meta.getColumnCount();
            if(cols != columnNamesArray.length){
                throw new ColumnNamesCountIncorrectException("Number  of Column Names given is different from number "
                        + "of  columns in ResultSet ");
            }
            for (int column = 0; column < cols; column++) {
                columnNames.add(columnNamesArray[column]);
            }
            Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
            Vector<Object> row;//=new Vector();

            while (rs.next()) {
                //System.out.println("Result set next");
                row = new Vector<Object>();
                for (int column = 1; column <= cols; column++) {
                    row.add(rs.getObject(column));
                }
                tableData.add(row);
            }
            
            DefaultTableModel model = new DefaultTableModel(tableData,columnNames);
            return model;

        } catch (Exception ex) {
            Logger.getLogger(DBUtilities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
