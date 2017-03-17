package print.form;

import bala.invoice.gui.InvoiceConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import util.NumberToWordsConverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bala date 20 Sep, 2014 11:41:49 AM
 */


public class PrintPanel extends JPanel {

    private HashMap widgetValues;
    private HashMap<String, Integer> productMap;
    private static double MM_TO_PIXELS_FACTOR = (1.0 / 25.4) * 72.0;
    private static int INV_NO_POS_X = (int) (115.0 * MM_TO_PIXELS_FACTOR);
    private static int INV_NO_POS_Y = (int) (57.0 * MM_TO_PIXELS_FACTOR);
    private static int INV_DATE_POS_X = (int) (182.0 * MM_TO_PIXELS_FACTOR);
    private static int INV_DATE_POS_Y = (int) (57.0 * MM_TO_PIXELS_FACTOR);
    private static int BUYER_ADD_POS_X = (int) (0.0 * MM_TO_PIXELS_FACTOR);
    private static int BUYER_ADD_POS_Y = (int) (34.0 * MM_TO_PIXELS_FACTOR);
    private static int TIN_NO_POS_X = (int) (15.0 * MM_TO_PIXELS_FACTOR);
    private static int TIN_NO_POS_Y = (int) (64.0 * MM_TO_PIXELS_FACTOR);
    private static int CST_NO_POS_X = (int) (70.0 * MM_TO_PIXELS_FACTOR);
    private static int FIRST_ITEM_POS_X = (int) (15.0 * MM_TO_PIXELS_FACTOR);
    private static int FIRST_ITEM_POS_Y = (int) (100.0 * MM_TO_PIXELS_FACTOR);
    private static int S_NO_POS_X = 0;
    private static int DESC_COL_POS_X = 20;
    private static int QTY_COL_POS_X = (int) (120.0 * MM_TO_PIXELS_FACTOR);
    private static int PRICE_COL_POS_X = (int) (150.0 * MM_TO_PIXELS_FACTOR);
    private static int AMOUNT_COL_POS_X = (int) (177.0 * MM_TO_PIXELS_FACTOR);
    private static int GRAND_TOTAL_COL_POS_Y = (int) (258.0 * MM_TO_PIXELS_FACTOR);
    private static int GRAND_TOTAL_WORDS_COL_POS_Y = (int) (258.0 * MM_TO_PIXELS_FACTOR);
    private static int GRAND_TOTAL_WORDS_COL_POS_X = (int) (20.0 * MM_TO_PIXELS_FACTOR);
    private static int GRAND_TOTAL_WORDS_COL_WIDTH = (int) (135.0 * MM_TO_PIXELS_FACTOR);
    private static int DESC_AREA_WIDTH = (int) (100.0 * MM_TO_PIXELS_FACTOR);
    private static int DESC_AREA_HEIGHT = (int) (15.0 * MM_TO_PIXELS_FACTOR);
    private static int QTY_COL_WIDTH = (int) (22.0 * MM_TO_PIXELS_FACTOR);
    private static int PRICE_COL_WIDTH = (int) (22.0 * MM_TO_PIXELS_FACTOR);
    private static int AMT_COL_WIDTH = (int) (25.0 * MM_TO_PIXELS_FACTOR);
    private static int V_GAP_BETWEEN_ITEMS = 0;//(int) (5.0 * MM_TO_PIXELS_FACTOR);

    public PrintPanel(HashMap widgetValues, HashMap<String, Integer> productMap) {
        this.setLayout(null);
        this.setBackground(Color.white);
        this.widgetValues = widgetValues;
        this.productMap = productMap;
        setSize(660, 842);
        addComponents();
    }

    public void addComponents() {
        JLabel l = new JLabel("{0,0,translation 0,45}");
        l.setBorder(BorderFactory.createLineBorder(Color.black));
        l.setBounds(0, 0, 40, 20);
        add(l);
        //JLabel l = new JLabel((String) widgetValues.get(InvoiceConstants.INV_NO_KEY));
        l = new JLabel((String) widgetValues.get(InvoiceConstants.INV_NO_KEY));
        l.setBounds(INV_NO_POS_X, INV_NO_POS_Y, (int) (50.0 * MM_TO_PIXELS_FACTOR), (int) (6.0 * MM_TO_PIXELS_FACTOR));
        add(l);
        Date date1 = (Date) widgetValues.get(InvoiceConstants.INV_DATE_KEY);
        String dateString = (date1.getDate()) + "/" + (date1.getMonth() + 1) + "/" + (date1.getYear() + 1900);
        l = new JLabel(dateString);
        l.setBounds(INV_DATE_POS_X, INV_DATE_POS_Y, l.getPreferredSize().width, l.getPreferredSize().height);
        add(l);
        JTextArea buyerArea = new JTextArea();
        buyerArea.setBounds(BUYER_ADD_POS_X, BUYER_ADD_POS_Y, (int) (93.0 * MM_TO_PIXELS_FACTOR), (int) (30.0 * MM_TO_PIXELS_FACTOR));
        buyerArea.setLineWrap(true);
        buyerArea.setWrapStyleWord(true);
        buyerArea.setText((String) widgetValues.get(InvoiceConstants.BUYER_NAME_KEY) + "\n" + (String) widgetValues.get(InvoiceConstants.BUYER_ADDRESS_KEY));
        buyerArea.setBorder(BorderFactory.createEmptyBorder());
        add(buyerArea);
        JLabel label;
        label = new JLabel("" + (String) widgetValues.get(InvoiceConstants.BUYER_TINNO_KEY));
        label.setBounds(TIN_NO_POS_X, TIN_NO_POS_Y, (int) (42.0 * MM_TO_PIXELS_FACTOR), (int) (8.0 * MM_TO_PIXELS_FACTOR));
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.LEFT);
        add(label);
        label = new JLabel("" + (String) widgetValues.get(InvoiceConstants.BUYER_CSTNO_KEY));
        label.setBounds(CST_NO_POS_X, TIN_NO_POS_Y, (int) (33.0 * MM_TO_PIXELS_FACTOR), (int) (8.0 * MM_TO_PIXELS_FACTOR));
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.LEFT);
        add(label);
        ArrayList<ArrayList> itemsList = (ArrayList<ArrayList>) widgetValues.get(InvoiceConstants.ITEM_LIST_KEY);
        JTextArea descArea;

// = new JTextArea();
        // int rowIndex = 0;
        int CURRENT_Y = FIRST_ITEM_POS_Y;
        for (int rowIndex = 0; rowIndex < itemsList.size(); rowIndex++) {
            label = new JLabel(("" + (rowIndex + 1)) + ".");
            label.setBounds(S_NO_POS_X, CURRENT_Y, (int) (20.0 * MM_TO_PIXELS_FACTOR), (int) (7.0 * MM_TO_PIXELS_FACTOR));
            label.setVerticalAlignment(JLabel.TOP);
            label.setHorizontalAlignment(JLabel.LEFT);
            add(label);

            descArea = new JTextArea();
            descArea.setBounds(DESC_COL_POS_X, CURRENT_Y, DESC_AREA_WIDTH, DESC_AREA_HEIGHT);
            descArea.setSize(DESC_AREA_WIDTH, DESC_AREA_HEIGHT);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            ArrayList row = itemsList.get(rowIndex);
            descArea.setText((String) row.get(InvoiceConstants.DESC_COL));
            descArea.setBorder(BorderFactory.createEmptyBorder());
            add(descArea);
            label = new JLabel("" + row.get(InvoiceConstants.QTY_COL));
            label.setBounds(QTY_COL_POS_X, CURRENT_Y, QTY_COL_WIDTH, DESC_AREA_HEIGHT);
            label.setVerticalAlignment(JLabel.TOP);
            label.setHorizontalAlignment(JLabel.RIGHT);
            add(label);
            label = new JLabel("" + row.get(InvoiceConstants.PRICE_COL));
            label.setBounds(PRICE_COL_POS_X, CURRENT_Y, PRICE_COL_WIDTH, DESC_AREA_HEIGHT);
            label.setVerticalAlignment(JLabel.TOP);
            label.setHorizontalAlignment(JLabel.RIGHT);
            add(label);
            label = new JLabel("" + (BigDecimal) row.get(InvoiceConstants.AMOUNT_COL));
            label.setBounds(AMOUNT_COL_POS_X, CURRENT_Y, AMT_COL_WIDTH, DESC_AREA_HEIGHT);
            label.setVerticalAlignment(JLabel.TOP);
            label.setHorizontalAlignment(JLabel.RIGHT);
            add(label);

            CURRENT_Y += V_GAP_BETWEEN_ITEMS + DESC_AREA_HEIGHT;
        }
JComponent lineComp= new JComponent
                () {

            @Override
            protected void paintComponent(Graphics g) {
               g.setColor(Color.white);
                g.fillRect(0,0,getWidth(),getHeight());
               g.setColor(Color.black);
               g.drawLine(0,2, getWidth(),2);
            }

        };
        lineComp.setBounds(AMOUNT_COL_POS_X,CURRENT_Y, AMT_COL_WIDTH,3);

        add(lineComp);
         CURRENT_Y +=5;
        label = new JLabel("" + (BigDecimal) widgetValues.get(InvoiceConstants.TOTAL_KEY));


          label.setBounds(AMOUNT_COL_POS_X, CURRENT_Y, AMT_COL_WIDTH, (int)(10.0*MM_TO_PIXELS_FACTOR));
            label.setVerticalAlignment(JLabel.TOP);
            label.setHorizontalAlignment(JLabel.RIGHT);
            add(label);
              CURRENT_Y +=11;
        String formString = widgetValues.get(InvoiceConstants.FORM_C_KEY) == "Y" ? "Against 'Form C' : " : "";
        label = new JLabel(formString);
        label.setBounds(DESC_COL_POS_X, CURRENT_Y, DESC_AREA_WIDTH, (int) (7.0 * MM_TO_PIXELS_FACTOR));
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.RIGHT);
       // add(label);
       CURRENT_Y += 20;
        
        
        label = new JLabel(formString + widgetValues.get(InvoiceConstants.TAX_TYPE_KEY) + "  :  " + widgetValues.get(InvoiceConstants.TAX_RATE_KEY) + " %");
        label.setBounds(DESC_COL_POS_X, CURRENT_Y, DESC_AREA_WIDTH, (int) (7.0 * MM_TO_PIXELS_FACTOR));
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);

        label = new JLabel("" + widgetValues.get(InvoiceConstants.TAX_AMOUNT_KEY));
        label.setBounds(AMOUNT_COL_POS_X, CURRENT_Y, AMT_COL_WIDTH, (int) (7.0 * MM_TO_PIXELS_FACTOR));
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);
        CURRENT_Y = GRAND_TOTAL_COL_POS_Y;

        BigDecimal b = (BigDecimal) widgetValues.get(InvoiceConstants.GRAND_TOTAL_KEY);
        double d = b.doubleValue();
        String grtS = String.format("%,.2f", d).replaceAll(",", "");//+
        label = new JLabel(grtS);
        label.setBounds(AMOUNT_COL_POS_X, CURRENT_Y, AMT_COL_WIDTH, (int) (7.0 * MM_TO_PIXELS_FACTOR));
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);

        String rupeesString = 
                 NumberToWordsConverter.convertToWords(Long.parseLong("" + widgetValues.get(InvoiceConstants.GRAND_TOTAL_KEY)))
                + " only.";
        rupeesString=rupeesString.trim();
        String initS = rupeesString.substring(0, 1);
       
        String cap = initS.toUpperCase();
        //rupeesString.replaceFirst(initS, cap);
       int len=rupeesString.length();
       rupeesString =rupeesString.substring(1,len);
       rupeesString=cap+rupeesString;
        //System.out.println("init string is "+initS+" cap is "+cap+" amount is "+rupeesString);
        CURRENT_Y = GRAND_TOTAL_WORDS_COL_POS_Y;
        descArea = new JTextArea(rupeesString);
        descArea.setBounds(GRAND_TOTAL_WORDS_COL_POS_X, CURRENT_Y, GRAND_TOTAL_WORDS_COL_WIDTH, (int) (20.0 * MM_TO_PIXELS_FACTOR));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(0.0f);
        descArea.setAlignmentY(0.0f);
        descArea.setBorder(BorderFactory.createEmptyBorder());

        // label.setHorizontalAlignment(JLabel.RIGHT);
        add(descArea);

    }

    public static void showPrint(HashMap widgetValues, HashMap<String, Integer> productMap) {
        JFrame f = new JFrame();
        f.setLayout(null);
        PrintPanel printPanel = new PrintPanel(widgetValues, productMap);
        f.add(printPanel);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(800, 700);
        f.setVisible(true);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package print.form;

import bala.invoice.gui.InvoiceConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;




class PrintPanel extends JPanel {

HashMap widgetValues;
HashMap<String, Integer> productMap;

public PrintPanel(HashMap widgetValues, HashMap<String, Integer> productMap) {
this.widgetValues = widgetValues;
this.productMap = productMap;
}

@Override
protected void paintComponent(Graphics g) {
// super.paintComponent(g
g.setColor(Color.white);
g.fillRect(0, 0, getWidth(), getHeight());
g.setColor(Color.red);
g.drawString((String) widgetValues.get(InvoiceConstants.INV_NO_KEY), PrintManager.INV_NO_H_DISTANCE, PrintManager.INV_NO_V_DISTANCE);

//To change body of generated methods, choose Tools | Templates.
}

}
 */
