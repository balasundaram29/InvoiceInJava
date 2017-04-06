package print.spreadsheet;

//import com.sun.java.swing.plaf.nimbus.SliderPainter;
import bala.invoice.gui.InvoiceConstants;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
//import javax.swing.border.Border;
import jxl.Range;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.write.BoldStyle;
import jxl.format.BorderLineStyle;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import util.NumberToWordsConverter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ANNAIENG
 */
public class PrintManager {

    public static int NO_OF_SHEETS = 4;
    //public static int[] COL_WIDTHS = {5, 9, 9, 9, 9, 9, 10, 16, 23};
    public static int[] COL_WIDTHS = {5, 5, 9, 9, 9, 11, 9, 15, 23};
    public static int LEFTMOST_COL = 0;
    public static int RIGHTMOST_COL = 8;
    public static int TOPMOST_ROW = 1;
    public static int V_OFFSET_BOTTOM = -5;
    public static int BOTTOMMOST_ROW = 50 + V_OFFSET_BOTTOM;
    public static int ROWS = 60;
    public static int TABLE_FIRST_ROW = 18;
    public static int COMP_NAME_ROW = 1;
    public static int COMP_NAME_COL = 2;
    public static int SHEET_NAME_ROW = 0;
    public static int SHEET_NAME_COL = 8;
    public static int TO_LABEL_ROW = 8;
    public static int TO_LABEL_COL_BEGIN = 0;
    public static int TO_LABEL_COL_END = 5;
    public static int INV_LABEL_ROW = 8;
    public static int INV_LABEL_COL_BEGIN = 6;
    public static int INV_LABEL_COL_END = 8;
    public static int BUYER_NAME_ROW = 9;
    public static int BUYER_NAME_COL_BEGIN = 0;
    public static int BUYER_NAME_COL_END = 5;
    public static int BUYER_ADDRESS_ROW = 10;
    public static int BUYER_ADDRESS_COL_BEGIN = 0;
    public static int BUYER_ADDRESS_COL_END = 5;
    public static int BUYER_ADDRESS_ROWS_COUNT = 3;
    public static int INV_NO_ROW = 10;
    public static int INV_NO_COL_BEGIN = 6;
    public static int INV_NO_COL_END = 7;
    public static int INV_DATE_ROW = 10;
    public static int INV_DATE_COL = 8;
    public static int BUYER_TIN_CST_DC_ROW = 13;
    public static int BUYER_TIN_COL_BEGIN = 0;
    public static int BUYER_TIN_COL_END = 3;
    public static int BUYER_CST_COL_BEGIN = 4;
    public static int BUYER_CST_COL_END = 5;
    public static int DC_COL_BEGIN = 6;
    public static int DC_COL_END = 7;
    public static int DC_DATE_COL = 8;
    public static int TRANS_LR_ORDER_NO_ROW = 14;
    public static int TRANS_COL_BEGIN = 0;
    public static int TRANS_COL_END = 3;
    public static int LR_COL_BEGIN = 4;
    public static int LR_COL_END = 5;
    public static int ORDER_COL_BEGIN = 6;
    public static int ORDER_COL_END = 7;
    public static int ORDER_DATE_COL = 8;
    public static int DEST_FREIGHT_DOCS_ROW = 15;
    public static int DEST_COL_BEGIN = 0;
    public static int DEST_COL_END = 3;
    public static int FREIGHT_COL_BEGIN = 4;
    public static int FREIGHT_COL_END = 5;
    public static int DOCS_COL_BEGIN = 6;
    public static int DOCS_COL_END = 8;
    public static int TABLE_HEADING_ROW = 16;
    public static int S_NO_COL = 0;
    public static int DESC_COL_BEGIN = 1;
    public static int DESC_COL_END = 5;
    public static int QTY_COL = 6;
    public static int PRICE_COL = 7;
    public static int AMOUNT_COL = 8;
    public static int ROWS_PER_ITEM = 2;
    public static int GAP_BETWEEN_ITEMS = 0;

    public static int TOTAL_ROW = 39 + V_OFFSET_BOTTOM;//40;
    public static int TOTAL_LABEL_COL = 7;
    public static int TOTAL_COL = 8;
    public static int FORM_C_AND_TAX_ROW = 40 + V_OFFSET_BOTTOM;
    public static int FORM_C_COL_BEGIN = 1;
    public static int FORM_C_COL_END = 5;
    public static int TAX_RATE_COL = 7;
    public static int TAX_AMOUNT_COL = 8;
    public static int ROUNDOFF_ROW = 41 + V_OFFSET_BOTTOM;
    public static int ROUNDOFF_LABEL_COL = 7;
    public static int ROUNDOFF_COL = 8;
    public static int GRAND_TOTAL_ROW_BEGIN = 42 + V_OFFSET_BOTTOM;
    public static int GRAND_TOTAL_ROW_END = 44 + V_OFFSET_BOTTOM;
    public static int JURISD_ROW = 45 + V_OFFSET_BOTTOM;
    public static int JURISD_COL_BEGIN = 0;
    public static int JURISD_COL_END = 5;
    public static int FOR_COMPANY_ROW = 45 + V_OFFSET_BOTTOM;
    public static int FOR_COMPANY_COL_BEGIN = 6;
    public static int FOR_COMPANY_COL_END = 8;
    public static int AUTH_SIGNATORY_ROW = 50 + V_OFFSET_BOTTOM;
    public static int AUTH_SIGNATORY_COL_BEGIN = 6;
    public static int AUTH_SIGNATORY_COL_END = 8;
    public static int PREPARED_ROW_BEGIN = 48 + V_OFFSET_BOTTOM;
    public static int PREPARED_ROW_END = 50 + V_OFFSET_BOTTOM;
    public static int PREPARED_COL_BEGIN = 0;
    public static int PREPARED_COL_END = 2;
    public static int CHECKED_ROW_BEGIN = 48 + V_OFFSET_BOTTOM;
    public static int CHECKED_ROW_END = 50 + V_OFFSET_BOTTOM;
    public static int CHECKED_COL_BEGIN = 3;
    public static int CHECKED_COL_END = 5;
    public static int INTEREST_ROW_BEGIN = 46 + V_OFFSET_BOTTOM;
    public static int INTEREST_ROW_END = 47 + V_OFFSET_BOTTOM;
    public static int INTEREST_COL_BEGIN = 0;
    public static int INTEREST_COL_END = 5;
    public static int GRAND_TOTAL_IN_WORDS_COL_BEGIN = 0;
    public static int GRAND_TOTAL_IN_WORDS_COL_END = 6;
    public static int GRAND_TOTAL_LABEL_COL = 7;
    public static int GRAND_TOTAL_COL = 8;
    String[] sheetNames = {"Original", "Duplicate", "Triplicate", "Quadruplicate"};
    public static String companyName = "CHRISTEC ENGINEERING COMPANY";
    public static String companyActivity1 = "Mfrs.:Openwell,Borewell Submersible Pumpsets,Jet And Monoblock Pumpsets,";
    public static String companyActivity2 = "Single Phase And Three Phase Industrial Motors";
    public static String[] companyAddress = {"3/32A, J.V.Nagar,VK Road,Vinayagapuram,",
        "Saravanampatti Post, Coimbatore - 641 035", "Mobile : 95669 95507"};
    public static String CompTINString = "TIN : 33942227422 ";
    public static String CompCSTString = "CST NO : 1164697 ";
    public static BorderLineStyle sheetBorder = BorderLineStyle.THIN;

    public void printInExcel(HashMap widgetValues, HashMap<String, Integer> productMap) {
        try {
            System.out.println("creating book");

            //Create a workbook named  invoice.xls from scratch ;cris.png required in path
            WritableWorkbook xlBook = Workbook.createWorkbook(new File("invoice.xls"));//,in);

            for (int sheetNo = 0; sheetNo < NO_OF_SHEETS; sheetNo++) {

                WritableSheet sheet = xlBook.createSheet(sheetNames[sheetNo], sheetNo);
                //  WritableSheet sheet = xlBook.createSheet(" ",sheetNo);
//WritableSheet sheet =xlBook.getSheet(sheetNo);//125

                for (int i = 13; i < 18; i++) {
                    sheet.setRowView(i, 400);
                }
                for (int i = 0; i < 9; i++) {
                    sheet.setColumnView(i, COL_WIDTHS[i]);
                }
                Label label;
                FontName fontNameCalibri = WritableFont.createFont("Calibri");
                WritableFont fontCalibri = new WritableFont(fontNameCalibri, 11, WritableFont.BOLD);// new WritableFont(fontCalibri, 11, WritableFont.NO_BOLD);
                WritableCellFormat cellFormat = new WritableCellFormat(fontCalibri);

                createAndAddLabel(SHEET_NAME_COL, SHEET_NAME_ROW, sheet, 11,
                        true, Alignment.RIGHT, sheetNames[sheetNo]);
//create sheet borders
                for (int col = LEFTMOST_COL; col <= RIGHTMOST_COL; col++) {

                    createAndAddLabelWithBorders(col, TOPMOST_ROW, sheet, 11,
                            false, Alignment.LEFT, false, false, true, false, "");
                    createAndAddLabelWithBorders(col, BOTTOMMOST_ROW, sheet, 11,
                            false, Alignment.LEFT, false, false, false, true, "");

                }
                for (int row = TOPMOST_ROW + 1; row <= BOTTOMMOST_ROW; row++) {

                    createAndAddLabelWithBorders(LEFTMOST_COL, row, sheet, 11,
                            false, Alignment.LEFT, true, false, false, false, "");
                    createAndAddLabelWithBorders(RIGHTMOST_COL, row, sheet, 11,
                            false, Alignment.LEFT, false, true, false, false, "");

                }
                File file = new File("cris.png");
                if (file.exists()) {
                    WritableImage image = new WritableImage(0, 2, 2.5, 3.75, new File("cris.png"));
                    sheet.addImage(image);
                }

                mergeCellsCreateAndAddLabelWithBorders(COMP_NAME_COL, COMP_NAME_ROW,
                        RIGHTMOST_COL, COMP_NAME_ROW, sheet, 22, true, Alignment.CENTRE,
                        false, true, true, false, companyName);
                mergeCellsCreateAndAddLabelWithBorders(COMP_NAME_COL, COMP_NAME_ROW + 1,
                        RIGHTMOST_COL, COMP_NAME_ROW + 1, sheet, 12, true, Alignment.CENTRE,
                        false, true, false, false, companyActivity1);
                mergeCellsCreateAndAddLabelWithBorders(COMP_NAME_COL, COMP_NAME_ROW + 2,
                        RIGHTMOST_COL, COMP_NAME_ROW + 2, sheet, 12, true, Alignment.CENTRE,
                        false, true, false, false, companyActivity2);

                for (int i = 0; i < 3; i++) {
                    mergeCellsCreateAndAddLabel(COMP_NAME_COL, COMP_NAME_ROW + 3 + i, RIGHTMOST_COL - 1, COMP_NAME_ROW + 3 + i,
                            sheet, 11, true, Alignment.CENTRE, companyAddress[i]);
                }

                createAndAddLabel(RIGHTMOST_COL, COMP_NAME_ROW + 3, sheet, 11,
                        true, Alignment.LEFT, CompTINString);

                createAndAddLabel(RIGHTMOST_COL, COMP_NAME_ROW + 4, sheet, 11,
                        true, Alignment.LEFT, CompCSTString);
                createAndAddLabel(RIGHTMOST_COL, COMP_NAME_ROW + 5, sheet, 11,
                        true, Alignment.LEFT, "Date :13.11.2013");

                mergeCellsCreateAndAddLabelWithBorders(INV_LABEL_COL_BEGIN, INV_LABEL_ROW,
                        INV_LABEL_COL_END, INV_LABEL_ROW, sheet, 22, true, Alignment.CENTRE, true,
                        true, true, true, "INVOICE");

                mergeCellsCreateAndAddLabel(INV_NO_COL_BEGIN, INV_NO_ROW, INV_NO_COL_END, INV_NO_ROW, sheet, 16, true, Alignment.CENTRE,
                        "Invoice No : " + (String) widgetValues.get(InvoiceConstants.INV_NO_KEY));

                Date dateI = (Date) widgetValues.get(InvoiceConstants.INV_DATE_KEY);
                String dateOfInv = (dateI.getYear() + 1900) + "-" + (dateI.getMonth() + 1) + "-" + (dateI.getDate());
                String[] splittedDateOfInv = dateOfInv.split("-");
                if (splittedDateOfInv[1].length() < 2) {
                    splittedDateOfInv[1] = "0" + splittedDateOfInv[1];
                }
                if (splittedDateOfInv[2].length() < 2) {
                    splittedDateOfInv[2] = "0" + splittedDateOfInv[2];
                }
                String dateOfInvIndian = splittedDateOfInv[2] + "-" + splittedDateOfInv[1] + "-" + splittedDateOfInv[0];
                createAndAddLabel(INV_DATE_COL, INV_DATE_ROW, sheet, 16,
                        true, Alignment.LEFT, "Date: " + dateOfInvIndian);
                mergeCellsCreateAndAddLabelWithBorders(TO_LABEL_COL_BEGIN, TO_LABEL_ROW, TO_LABEL_COL_END,
                        TO_LABEL_ROW, sheet, 12, false, Alignment.LEFT, true, true, true, false, "To : ");
                mergeCellsCreateAndAddLabelWithBorders(BUYER_NAME_COL_BEGIN, BUYER_NAME_ROW, BUYER_NAME_COL_END, BUYER_NAME_ROW,
                        sheet, 14, false, Alignment.LEFT, true, true, false, false, (String) widgetValues.get(InvoiceConstants.BUYER_NAME_KEY));

                //  fontCalibri = new WritableFont(fontNameCalibri, 14, WritableFont.NO_BOLD);
                String[] addressRows = new String[BUYER_ADDRESS_ROWS_COUNT];
                String[] addressRowsAvailable = ((String) widgetValues.get(InvoiceConstants.BUYER_ADDRESS_KEY)).split("\n");
                for (int i = 0; i < addressRows.length; i++) {
                    if (!(i < addressRowsAvailable.length)) {
                        addressRows[i] = "";
                    } else {
                        addressRows[i] = addressRowsAvailable[i];
                    }
                }
                for (int k = 0; k < addressRows.length; k++) {
                    mergeCellsCreateAndAddLabelWithBorders(BUYER_ADDRESS_COL_BEGIN, BUYER_ADDRESS_ROW + k, BUYER_ADDRESS_COL_END,
                            BUYER_ADDRESS_ROW + k, sheet, 12, false, Alignment.LEFT, true, true, false, false, addressRows[k]);
                }

                String tin = "";
                if (widgetValues.get(InvoiceConstants.BUYER_TINNO_KEY) != null) {
                    tin = (String) widgetValues.get(InvoiceConstants.BUYER_TINNO_KEY);
                }
                mergeCellsCreateAndAddLabelWithBorders(BUYER_TIN_COL_BEGIN, BUYER_TIN_CST_DC_ROW, BUYER_TIN_COL_END,
                        BUYER_TIN_CST_DC_ROW, sheet, 12, false, Alignment.LEFT, true, false, true, true, "Buyer's TIN No:" + tin);

                String cst = "";
                if (widgetValues.get(InvoiceConstants.BUYER_CSTNO_KEY) != null) {
                    cst = (String) widgetValues.get(InvoiceConstants.BUYER_CSTNO_KEY);
                }
                mergeCellsCreateAndAddLabelWithBorders(BUYER_CST_COL_BEGIN, BUYER_TIN_CST_DC_ROW, BUYER_CST_COL_END, BUYER_TIN_CST_DC_ROW, sheet, 12, false, Alignment.LEFT, false, true, true, true, "CST:" + cst);
                String dc = "";
                mergeCellsCreateAndAddLabelWithBorders(DC_COL_BEGIN, BUYER_TIN_CST_DC_ROW, DC_COL_END,
                        BUYER_TIN_CST_DC_ROW, sheet, 12, false, Alignment.LEFT, true, true, true, true, "DC No:" + dc);

                String dcDate = "";
                createAndAddLabelWithBorders(DC_DATE_COL, BUYER_TIN_CST_DC_ROW,
                        sheet, 12, false,
                        Alignment.LEFT, true, true, true, true, "Date: " + dcDate);
                String tr = "";
                mergeCellsCreateAndAddLabelWithBorders(TRANS_COL_BEGIN, TRANS_LR_ORDER_NO_ROW, TRANS_COL_END, TRANS_LR_ORDER_NO_ROW,
                        sheet, 12, false, Alignment.LEFT, true, false, true, true, "Transporter:" + tr);
                String lr = "";
                mergeCellsCreateAndAddLabelWithBorders(LR_COL_BEGIN, TRANS_LR_ORDER_NO_ROW, LR_COL_END, TRANS_LR_ORDER_NO_ROW, sheet, 12, false, Alignment.LEFT, false, true, true, true, "LR No :" + lr);

                String ordno = "";
                mergeCellsCreateAndAddLabelWithBorders(ORDER_COL_BEGIN, TRANS_LR_ORDER_NO_ROW, ORDER_COL_END, TRANS_LR_ORDER_NO_ROW,
                        sheet, 12, false, Alignment.LEFT, true, true, true, true, "Order No :" + ordno);

                String ordDate = "";
                createAndAddLabel(ORDER_DATE_COL, TRANS_LR_ORDER_NO_ROW, sheet, 12, false, Alignment.LEFT, "Date:" + ordDate);
                String dest = "";
                mergeCellsCreateAndAddLabelWithBorders(DEST_COL_BEGIN, DEST_FREIGHT_DOCS_ROW, DEST_COL_END, DEST_FREIGHT_DOCS_ROW,
                        sheet, 12, false, Alignment.LEFT, true, false, true, true, "Destination:" + dest);
                String fr = "";
                mergeCellsCreateAndAddLabelWithBorders(FREIGHT_COL_BEGIN, DEST_FREIGHT_DOCS_ROW, FREIGHT_COL_END, DEST_FREIGHT_DOCS_ROW,
                        sheet, 12, false, Alignment.LEFT, false, true, true, true, "Freight:" + fr);
                String docs = "";
                mergeCellsCreateAndAddLabelWithBorders(DOCS_COL_BEGIN, DEST_FREIGHT_DOCS_ROW, DOCS_COL_END, DEST_FREIGHT_DOCS_ROW,
                        sheet, 12, false, Alignment.LEFT, true, true, true, true, "Documents Through :" + docs);

                createAndAddLabelWithBorders(S_NO_COL, TABLE_HEADING_ROW, sheet, 12,
                        true, Alignment.LEFT, true, true, true, true, "S No");

                mergeCellsCreateAndAddLabelWithBorders(DESC_COL_BEGIN, TABLE_HEADING_ROW, DESC_COL_END, TABLE_HEADING_ROW,
                        sheet, 12, true, Alignment.LEFT, true, true, true, true, "Description");

                createAndAddLabelWithBorders(QTY_COL, TABLE_HEADING_ROW, sheet, 12,
                        true, Alignment.CENTRE, true, true, true, true, "Quantity");

                createAndAddLabelWithBorders(PRICE_COL, TABLE_HEADING_ROW, sheet, 12,
                        true, Alignment.RIGHT, true, true, true, true, "Rate(Rs.)");

                createAndAddLabelWithBorders(AMOUNT_COL, TABLE_HEADING_ROW, sheet, 12,
                        true, Alignment.RIGHT, true, true, true, true, "Amount(Rs.)");

                ArrayList<ArrayList> itemsList = (ArrayList<ArrayList>) widgetValues.get(InvoiceConstants.ITEM_LIST_KEY);
                fontCalibri = new WritableFont(fontNameCalibri, 12, WritableFont.NO_BOLD);
                cellFormat = new WritableCellFormat(fontCalibri);
                //create vertical  lines for table
                for (int row = TABLE_HEADING_ROW + 1; row <= GRAND_TOTAL_ROW_END; row++) {
                    createAndAddLabelWithBorders(S_NO_COL, row, sheet, 12,
                            false, Alignment.LEFT, true, true, false, false, "");
                    createAndAddLabelWithBorders(DESC_COL_END, row, sheet, 12,
                            false, Alignment.LEFT, false, true, false, false, "");
                    createAndAddLabelWithBorders(QTY_COL, row, sheet, 12,
                            false, Alignment.LEFT, false, true, false, false, "");
                    createAndAddLabelWithBorders(PRICE_COL, row, sheet, 12,
                            false, Alignment.LEFT, false, true, false, false, "");
                    createAndAddLabelWithBorders(AMOUNT_COL, row, sheet, 12,
                            false, Alignment.LEFT, false, true, false, false, "");
                }

                int CURR_ROW = TABLE_HEADING_ROW + 1;
                for (int rowIndex = 0; rowIndex < itemsList.size(); rowIndex++) {
                    ArrayList row = itemsList.get(rowIndex);

                    createAndAddLabelWithBorders(S_NO_COL, CURR_ROW, sheet, 11,
                            false, Alignment.LEFT, true, true, false, false, " " + (rowIndex + 1));
                    Range range = sheet.mergeCells(DESC_COL_BEGIN, CURR_ROW, DESC_COL_END, CURR_ROW + ROWS_PER_ITEM - 1);
                    cellFormat = new WritableCellFormat(fontCalibri);
                    cellFormat.setAlignment(Alignment.LEFT);
                    cellFormat.setWrap(true);//)
                    cellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
                    cellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
                    cellFormat.setVerticalAlignment(VerticalAlignment.TOP);
                    label = new Label(DESC_COL_BEGIN, CURR_ROW, "" + row.get(InvoiceConstants.DESC_COL));
                    label.setCellFormat(cellFormat);
                    sheet.addCell(label);

                    createAndAddLabelWithBorders(QTY_COL, CURR_ROW, sheet, 12,
                            false, Alignment.CENTRE, true, true, false, false,
                            row.get(InvoiceConstants.QTY_COL) + "");
                    createAndAddLabelWithBorders(PRICE_COL, CURR_ROW, sheet, 12,
                            false, Alignment.RIGHT, true, true, false, false,
                            row.get(InvoiceConstants.PRICE_COL) + "");
                    createAndAddLabelWithBorders(AMOUNT_COL, CURR_ROW, sheet, 12,
                            false, Alignment.RIGHT, true, true, false, false,
                            row.get(InvoiceConstants.AMOUNT_COL) + "");
                    CURR_ROW = CURR_ROW + ROWS_PER_ITEM + GAP_BETWEEN_ITEMS;
                }
                sheet.mergeCells(DESC_COL_BEGIN, CURR_ROW, DESC_COL_END, CURR_ROW + ROWS_PER_ITEM - 1);
                cellFormat = new WritableCellFormat(fontCalibri);
                cellFormat.setAlignment(Alignment.LEFT);
                cellFormat.setWrap(true);//)
                cellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
                cellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
                cellFormat.setVerticalAlignment(VerticalAlignment.TOP);
                label = new Label(DESC_COL_BEGIN, CURR_ROW, "" + widgetValues.get(InvoiceConstants.REMARKS_KEY));
                label.setCellFormat(cellFormat);
                sheet.addCell(label);
                createAndAddLabelWithBorders(TOTAL_LABEL_COL, TOTAL_ROW, sheet, 12,
                        false, Alignment.RIGHT, true, true, false, false,
                        "Total");

                createAndAddLabelWithBorders(TOTAL_COL, TOTAL_ROW, sheet, 12,
                        false, Alignment.RIGHT, true, true, false, false,
                        widgetValues.get(InvoiceConstants.TOTAL_KEY) + "");

                String formString = widgetValues.get(InvoiceConstants.FORM_C_KEY) == "Y" ? "Against 'Form C'" : "";
                mergeCellsCreateAndAddLabelWithBorders(FORM_C_COL_BEGIN, FORM_C_AND_TAX_ROW, FORM_C_COL_END, FORM_C_AND_TAX_ROW,
                        sheet, 12, true, Alignment.RIGHT, true, true, false, false, formString);

                createAndAddLabelWithBorders(TAX_RATE_COL, FORM_C_AND_TAX_ROW, sheet, 11,
                        false, Alignment.RIGHT, true, true, false, false, widgetValues.get(InvoiceConstants.TAX_TYPE_KEY)
                        + " : " + widgetValues.get(InvoiceConstants.TAX_RATE_KEY) + " %");
                createAndAddLabelWithBorders(TAX_AMOUNT_COL, FORM_C_AND_TAX_ROW, sheet, 11,
                        false, Alignment.RIGHT, true, true, false, false, widgetValues.get(InvoiceConstants.TAX_AMOUNT_KEY) + "");

                String sign;
                if (((BigDecimal) widgetValues.get(InvoiceConstants.ROUND_OFF_KEY)).signum() >= 0) {
                    sign = "(+)";
                } else {
                    sign = "(-)";
                }
                createAndAddLabelWithBorders(ROUNDOFF_LABEL_COL, ROUNDOFF_ROW, sheet, 11, false, Alignment.RIGHT, true, true, false, true, "Round-off"
                        + sign);
                createAndAddLabelWithBorders(ROUNDOFF_COL, ROUNDOFF_ROW, sheet, 11, false, Alignment.RIGHT, true, true, false, true,
                        "" + ((BigDecimal) widgetValues.get(InvoiceConstants.ROUND_OFF_KEY)).abs());

                createAndAddLabelWithBorders(GRAND_TOTAL_LABEL_COL, GRAND_TOTAL_ROW_BEGIN + 1,
                        sheet, 12, true, Alignment.RIGHT, true, true, false, false, "GRAND TOTAL");

                createAndAddLabelWithBorders(GRAND_TOTAL_COL, GRAND_TOTAL_ROW_BEGIN + 1, sheet,
                        12, true, Alignment.RIGHT, true, true, false, false,
                        "" + widgetValues.get(InvoiceConstants.GRAND_TOTAL_KEY) + ".00");

                sheet.mergeCells(GRAND_TOTAL_IN_WORDS_COL_BEGIN, GRAND_TOTAL_ROW_BEGIN, GRAND_TOTAL_IN_WORDS_COL_END,
                        GRAND_TOTAL_ROW_END);
                String rupeesString = "Rupees "
                        + NumberToWordsConverter.convertToWords(Long.parseLong("" + widgetValues.get(InvoiceConstants.GRAND_TOTAL_KEY)))
                        + " only.";
                label = new Label(GRAND_TOTAL_IN_WORDS_COL_BEGIN, GRAND_TOTAL_ROW_BEGIN, rupeesString);
                fontCalibri = new WritableFont(fontNameCalibri, 12, WritableFont.BOLD);
                cellFormat = new WritableCellFormat(fontCalibri);
                cellFormat.setAlignment(Alignment.LEFT);
                cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
                cellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
                cellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
                cellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
                cellFormat.setWrap(true);
                cellFormat.setWrap(true);

                label.setCellFormat(cellFormat);
                sheet.addCell(label);
                // cellFormat = new WritableCellFormat(fontCalibri);
                mergeCellsCreateAndAddLabelWithBorders(JURISD_COL_BEGIN, JURISD_ROW, JURISD_COL_END, JURISD_ROW,
                        sheet, 11, false, Alignment.LEFT, true, true, true, false, "Subject to Coimbatore jurisdiction.");
                mergeCellsCreateAndAddLabelWithBorders(INTEREST_COL_BEGIN, INTEREST_ROW_BEGIN, INTEREST_COL_END, INTEREST_ROW_BEGIN,
                        sheet, 11, false, Alignment.LEFT, true, true, false, false, "Interest at 24% per annum will be charged on amounts ");
                mergeCellsCreateAndAddLabelWithBorders(INTEREST_COL_BEGIN, INTEREST_ROW_END, INTEREST_COL_END, INTEREST_ROW_END,
                        sheet, 11, false, Alignment.LEFT, true, true, false, false, "not paid within 30 days from the date of invoice. ");
                mergeCellsAddLabelWithBordersAndVAlign(PREPARED_COL_BEGIN, PREPARED_ROW_BEGIN, PREPARED_COL_END, PREPARED_ROW_END,
                        sheet, 11, false, Alignment.LEFT, VerticalAlignment.TOP, true, true, true, true, "Prepared By :");
                mergeCellsAddLabelWithBordersAndVAlign(CHECKED_COL_BEGIN, CHECKED_ROW_BEGIN, CHECKED_COL_END, CHECKED_ROW_END,
                        sheet, 11, false, Alignment.LEFT, VerticalAlignment.TOP, true, true, true, true, "Checked By :");
                mergeCellsCreateAndAddLabelWithBorders(FOR_COMPANY_COL_BEGIN, FOR_COMPANY_ROW, FOR_COMPANY_COL_END, FOR_COMPANY_ROW,
                        sheet, 12, true, Alignment.RIGHT, true, true, true, false, "for " + companyName + ",");
                mergeCellsCreateAndAddLabelWithBorders(AUTH_SIGNATORY_COL_BEGIN, AUTH_SIGNATORY_ROW, AUTH_SIGNATORY_COL_END, AUTH_SIGNATORY_ROW,
                        sheet, 12, true, Alignment.RIGHT, true, true, false, true, "Authorized Signatory.");
                SheetSettings sheetSettings = sheet.getSettings();
                sheetSettings.setHeaderMargin(0.0);
                sheetSettings.setFooterMargin(0.0);
                sheetSettings.setLeftMargin(0.50);
                sheetSettings.setRightMargin(0.0);
                sheetSettings.setTopMargin(0.0);
                sheetSettings.setBottomMargin(0.0);

            }
            xlBook.write();
            xlBook.close();
            String[] commands = {"cmd", "/c", "start", "\"DummyTitle\"", "invoice.xls"};//
            //String commands = "oocalc /home/bala/GitProjects//InvoiceInJava/Invoice.xls";
            Runtime.getRuntime().exec(commands);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Workbook cannot be created ."
                    + "Please close the excel app that is using 'Invoice.xls' file");
            Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void mergeCellsCreateAndAddLabel(int col1, int row1, int col2,
            int row2, WritableSheet sheet, int fontSize, boolean bold,
            Alignment horzAlignment, String text) {
        try {
            sheet.mergeCells(col1, row1, col2, row2);
            FontName fontNameCalibri = WritableFont.createFont("Calibri");
            WritableFont fontCalibri = bold ? new WritableFont(fontNameCalibri, fontSize, WritableFont.BOLD) : new WritableFont(fontNameCalibri, fontSize, WritableFont.NO_BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(fontCalibri);
            cellFormat.setAlignment(horzAlignment); //)setBorder(Border.ALL, BorderLineStyle.MEDIUM);
            Label label = new Label(col1, row1, text);
            label.setCellFormat(cellFormat);
            sheet.addCell(label);
        } catch (Exception ex) {
            Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mergeCellsCreateAndAddLabelWithBorders(int col1, int row1, int col2,
            int row2, WritableSheet sheet, int fontSize, boolean bold,
            Alignment horzAlignment, boolean leftBorder, boolean rightBorder, boolean topBorder,
            boolean bottomBorder, String text) {
        try {
            sheet.mergeCells(col1, row1, col2, row2);
            FontName fontNameCalibri = WritableFont.createFont("Calibri");
            WritableFont fontCalibri = bold ? new WritableFont(fontNameCalibri, fontSize, WritableFont.BOLD) : new WritableFont(fontNameCalibri, fontSize, WritableFont.NO_BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(fontCalibri);
            cellFormat.setAlignment(horzAlignment); //)setBorder(Border.ALL, BorderLineStyle.MEDIUM);
            if (leftBorder) {
                cellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
            }
            if (rightBorder) {
                cellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            }
            if (topBorder) {
                cellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
            }
            if (bottomBorder) {
                cellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            }
            if (col1 == LEFTMOST_COL) {
                cellFormat.setBorder(Border.LEFT, sheetBorder);
            }
            if (col2 == RIGHTMOST_COL) {
                cellFormat.setBorder(Border.RIGHT, sheetBorder);
            }
            if (row1 == TOPMOST_ROW) {
                cellFormat.setBorder(Border.TOP, sheetBorder);
            }
            if (row2 == BOTTOMMOST_ROW) {
                cellFormat.setBorder(Border.BOTTOM, sheetBorder);
            }
            Label label = new Label(col1, row1, text);
            label.setCellFormat(cellFormat);
            sheet.addCell(label);

        } catch (Exception ex) {
            Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mergeCellsAddLabelWithBordersAndVAlign(int col1, int row1, int col2,
            int row2, WritableSheet sheet, int fontSize, boolean bold,
            Alignment horzAlignment, VerticalAlignment vertAlignment, boolean leftBorder, boolean rightBorder, boolean topBorder,
            boolean bottomBorder, String text) {
        try {
            sheet.mergeCells(col1, row1, col2, row2);
            FontName fontNameCalibri = WritableFont.createFont("Calibri");
            WritableFont fontCalibri = bold ? new WritableFont(fontNameCalibri, fontSize, WritableFont.BOLD) : new WritableFont(fontNameCalibri, fontSize, WritableFont.NO_BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(fontCalibri);
            cellFormat.setAlignment(horzAlignment);
            cellFormat.setVerticalAlignment(vertAlignment);//)setBorder(Border.ALL, BorderLineStyle.MEDIUM);
            if (leftBorder) {
                cellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
            }
            if (rightBorder) {
                cellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            }
            if (topBorder) {
                cellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
            }
            if (bottomBorder) {
                cellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            }
            if (col1 == LEFTMOST_COL) {
                cellFormat.setBorder(Border.LEFT, sheetBorder);
            }
            if (col2 == RIGHTMOST_COL) {
                cellFormat.setBorder(Border.RIGHT, sheetBorder);
            }
            if (row1 == TOPMOST_ROW) {
                cellFormat.setBorder(Border.TOP, sheetBorder);
            }
            if (row2 == BOTTOMMOST_ROW) {
                cellFormat.setBorder(Border.BOTTOM, sheetBorder);
            }
            Label label = new Label(col1, row1, text);
            label.setCellFormat(cellFormat);
            sheet.addCell(label);

        } catch (Exception ex) {
            Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createAndAddLabel(int col, int row, WritableSheet sheet,
            int fontSize, boolean bold, Alignment horzAlignment, String text) {
        try {
            FontName fontNameCalibri = WritableFont.createFont("Calibri");
            WritableFont fontCalibri = bold ? new WritableFont(fontNameCalibri, fontSize, WritableFont.BOLD) : new WritableFont(fontNameCalibri, fontSize, WritableFont.NO_BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(fontCalibri);
            cellFormat.setAlignment(horzAlignment);
            if (col == LEFTMOST_COL) {
                cellFormat.setBorder(Border.LEFT, sheetBorder);
            }
            if (col == RIGHTMOST_COL && row != SHEET_NAME_ROW) {
                cellFormat.setBorder(Border.RIGHT, sheetBorder);
            }
            if (row == TOPMOST_ROW) {
                cellFormat.setBorder(Border.TOP, sheetBorder);
            }
            if (row == BOTTOMMOST_ROW) {
                cellFormat.setBorder(Border.BOTTOM, sheetBorder);
            }

            Label label = new Label(col, row, text);
            label.setCellFormat(cellFormat);
            sheet.addCell(label);
        } catch (Exception ex) {
            Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createAndAddLabelWithBorders(int col, int row, WritableSheet sheet,
            int fontSize, boolean bold, Alignment horzAlignment, boolean leftBorder, boolean rightBorder, boolean topBorder,
            boolean bottomBorder, String text) {
        try {
            FontName fontNameCalibri = WritableFont.createFont("Calibri");
            WritableFont fontCalibri = bold ? new WritableFont(fontNameCalibri, fontSize, WritableFont.BOLD) : new WritableFont(fontNameCalibri, fontSize, WritableFont.NO_BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(fontCalibri);
            cellFormat.setAlignment(horzAlignment);
            if (leftBorder) {
                cellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
            }
            if (rightBorder) {
                cellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            }
            if (topBorder) {
                cellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
            }
            if (bottomBorder) {
                cellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            }
            if (col == LEFTMOST_COL) {
                cellFormat.setBorder(Border.LEFT, sheetBorder);
            }
            if (col == RIGHTMOST_COL) {
                cellFormat.setBorder(Border.RIGHT, sheetBorder);
            }
            if (row == TOPMOST_ROW) {
                cellFormat.setBorder(Border.TOP, sheetBorder);
            }
            if (row == BOTTOMMOST_ROW) {
                cellFormat.setBorder(Border.BOTTOM, sheetBorder);
            }
            Label label = new Label(col, row, text);
            label.setCellFormat(cellFormat);
            sheet.addCell(label);
        } catch (Exception ex) {
            Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
