package bala.invoice.gui;

import bala.invoice.db.DBUtilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ANNAIENG
 */
public class InvoiceConstants {
    public static String FinYearString = "BETWEEN '2015-04-2015 AND ";
    public static int INV_NO_KEY = 0;
    public static int INV_DATE_KEY = 1;
    public static int BUYER_ID_KEY = 2;
    public static int BUYER_NAME_KEY = 3;
    public static int ITEM_LIST_KEY = 4;
    public static int TAX_RATE_KEY = 5;
    public static int TAX_TYPE_KEY = 6;
    public static int FORM_C_KEY = 7;
    public static int REMARKS_KEY=8;
    public static int TOTAL_KEY = 9;
    public static int TAX_AMOUNT_KEY = 10;
    public static int GRAND_TOTAL_KEY = 11;
    public static int BUYER_ADDRESS_KEY = 12;
    public static int BUYER_TINNO_KEY = 13;
    public static int BUYER_CSTNO_KEY = 14;
    public static int ROUND_OFF_KEY=15;
    //following correspond for item table
    public static int tableRowCount = 0;
    public static int SL_NO_COL = 0;
    public static int DESC_COL = 1;
    public static int QTY_COL = 2;
    public static int PRICE_COL = 3;
    public static int AMOUNT_COL = 4;
    public static int TOTAL_COLS = 5;
    public static int FIN_YEAR_FIRST_PART=-1;
    
   // public static 
//HashMap<String, Integer> productMap = null;// new HashMap<String, Integer>();
  //  HashMap<String, Integer> buyerMap = null;
    public static void setFinYear(int year){
        FIN_YEAR_FIRST_PART=year;
    } 
    public static void setFinYearFromDB(){
        try {
            Connection conn = DBUtilities.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT MAX(`finYearFirstPart`) FROM `Years`");
            if (rs.next()) {
                int year = (int) rs.getInt(1);
                 System.out.println("IC:Financial Year is"+year) ;
               
                setFinYear(year);

            }
        } catch (Exception ex) {
            Logger.getLogger(InvoiceConstants.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
