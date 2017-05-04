package bala.invoice.gui;

import bala.invoice.db.DBUtilities;
import bala.invoice.db.DatabaseManager;
import com.toedter.calendar.JDateChooser;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import net.miginfocom.swing.MigLayout;
//import spreadsheet.PrintManager;

public class InvoiceEditWidget extends JPanel implements DocumentListener {

    public int table_row_count = 0;
    HashMap<Integer, Object> widgetValues;
    HashMap<String, Integer> productMap = null;// new HashMap<String, Integer>();
    HashMap<String, Integer> buyerMap = null;// new HashMap<String, Object>();
    ArrayList tableItemsList = new ArrayList<ArrayList>();
    // buyerDict={}
    Connection conn = null;
    JTable salesTable = null;
    SalesTableModel tableModel = null;
    private JLabel yearLabel;
    JTextField invoiceNoTextField;
    JDateChooser dChooser;
    JComboBox buyerCombo;
    JComboBox productCombo;
    JTextArea buyerAddressArea;
    JButton removeButton;
    JButton addButton;
    JTextField taxRateTextField;
    JComboBox taxTypeCombo;
    JCheckBox formCCheckBox;
    JTextField remarksField;
    JTextField totalTextField;
    JButton viewAllButton;
    JButton printXLButton;
    JButton printFormButton;
    JButton deleteButton;
    JButton saveButton;
    BigDecimal total;
    BigDecimal grTotalRounded, taxRate, taxAmount, grTotal;
    int tabelRenderEntry = 0;
    private static InvoiceEditWidget instance = null;

    public static InvoiceEditWidget getInstance() {
        if (instance == null) {
            instance = new InvoiceEditWidget();
        }
        return instance;
    }

    private InvoiceEditWidget() {

        super(new MigLayout(
                "", // Layout Constraints
                "[][grow][][grow,right]", // Column constraints
                "[top]20[top]" // Row constraints
        ));

        conn = DBUtilities.getConnection();
        if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
            InvoiceConstants.setFinYearFromDB();
        }

        fillProductAndBuyerLists();

        if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
            yearLabel = new JLabel("Financial Year : None Selected");
        } else {
            yearLabel = new JLabel("Financial Year : " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
        }

        add(yearLabel, "grow,wrap");
        JLabel label;
        label = new JLabel("Invoice No :");
        invoiceNoTextField = new JTextField();
        //if no financial year selected get the maximum financial year

        invoiceNoTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                    JOptionPane.showMessageDialog(null, "There are no  financial years available.\n Create/Select  financial year using Edit->Manage Years MenuItem.");
                    return;
                }
                loadValuesFromHashMaps();
            }
        });
        add(label);
        add(invoiceNoTextField, "grow");

        label = new JLabel("Date :");
        add(label, "gap unrelated");
        dChooser = new JDateChooser(new Date(), "dd-MM-yyyy");

        dChooser.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {

                        if ("date".equals(e.getPropertyName())) {
                            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                                Date newDate = (Date) e.getNewValue();
                                System.out.println("date new value is" + newDate);
                                System.out.println("date new Date is" + new Date());
                                JOptionPane.showMessageDialog(null, "There are no financial years available.\nCreate/Select  financial year using Edit->Manage Years MenuItem.");
                                //
                                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                                if (sdf1.format(newDate).equals(sdf1.format(new Date()))) {
                                    return;
                                }
                                // if (newDate.compareTo(new Date()) == 0) {
                                //   return;
                                //}
                                dChooser.setDate(new Date());
                            }
                            System.out.println(e.getPropertyName()
                                    + ": " + (Date) e.getNewValue());
                            Date newDate = (Date) e.getNewValue();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            Date firstDate = null;
                            Date lastDate = null;
                            try {
                                firstDate = sdf.parse("01-04-" + InvoiceConstants.FIN_YEAR_FIRST_PART);

                                lastDate = sdf.parse("31-03-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
                            } catch (Exception ex) {
                                Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (newDate.before(firstDate) || newDate.after(lastDate)) {
                                JOptionPane.showMessageDialog(null, "The selected date is outside the currently active financial year "
                                        + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "."
                                        + "Select date in the financial year " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "."
                                        + "\n Or create/select correct financial year using Edit->Manage Years MenuItem.");
                                dChooser.setDate(firstDate);

                            }

                        }
                    }
                });

        try {
            // dChooser.setSelectableDateRange(new SimpleDateFormat("DD-MM-YYYY").parse("01-04-" + (InvoiceConstants.FIN_YEAR_FIRST_PART)),
            //       new SimpleDateFormat("DD-MM-YYYY").parse("31-03-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1)));
            //System.out.println("Fianacial Year fp " + Integer.toString(InvoiceConstants.FIN_YEAR_FIRST_PART));

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        add(dChooser, "grow,wrap");
        label = new JLabel("Buyer :");

        buyerCombo = new JComboBox();
//buyerMap
        ArrayList<String> buyerList = new ArrayList<String>();
        for (String key : buyerMap.keySet()) {
            buyerList.add(key);
            //buyerCombo.addItem(key);
        }
        if (buyerList != null && buyerList.size() >= 0) {
            Collections.sort(buyerList);

            for (String bname : buyerList) {
                //buyerList.add(key);
                buyerCombo.addItem(bname);
            }

        }

        buyerCombo.setMaximumSize(new Dimension(1000, 25));
        add(label);
        add(buyerCombo, "grow");
        label = new JLabel("Buyer's Address :");
        add(label, "gap unrelated");
        buyerAddressArea = new JTextArea();
        buyerAddressArea.setMinimumSize(new Dimension(100, 80));

        buyerAddressArea.setBorder(BorderFactory.createLineBorder(Color.black));
        add(buyerAddressArea, "wrap,grow");//"cell column row width height"
        try {

            String q = "SELECT `address` from `Buyers` WHERE `name`=" + "\'" + (String) buyerCombo.getSelectedItem() + "\'";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(q);
            if (rs.next()) {
                String addr = (String) rs.getString("address");
                buyerAddressArea.setText(addr);

            }
        } catch (SQLException ex) {
            Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        buyerCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    String q = "SELECT `address` from `Buyers` WHERE `name`=" + "\'" + (String) buyerCombo.getSelectedItem() + "\'";
                    Statement s = conn.createStatement();
                    ResultSet rs = s.executeQuery(q);
                    if (rs.next()) {
                        buyerAddressArea.setText((String) rs.getString("address"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        removeButton = new JButton("Remove Item");
        add(removeButton);
        removeButton.setIcon(new ImageIcon(this.getClass().getResource("remove24.png")));
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r;
                /* for (int i=0;i<salesTable.getRowCount();i++){
                 TableColumn itemColumn = salesTable.getColumnModel().getColumn(1);
           
                 JComboBox combo=(JComboBox)salesTable.getModel().getValueAt(i, InvoiceConstants.DESC_COL);
                    
                 if(combo.isFocusOwner()){
                 System.out.println("Combo : "+i+" is FocusOwner");
                 }else
                 {
                 System.out.println("Combo : "+i+" is not FocusOner");
                 }
                 }*/
                if (salesTable == null) {
                    return;
                }
                if (salesTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(InvoiceEditWidget.this, "Please select a row by clicking on SNo,Quantity,Price or Amount column.");
                    return;
                }
                if (salesTable != null && (r = salesTable.getSelectedRow()) != -1) {
                    try {

                        buyerAddressArea.requestFocusInWindow();
                        Robot robot = new Robot();
                        Point point;
                        Rectangle cellRect = salesTable.getCellRect(salesTable.getSelectedRow(), InvoiceConstants.PRICE_COL, false);
                        /*point = new Point(cellRect.x + 5, cellRect.y + 5);
                         SwingUtilities.convertPointToScreen(point, salesTable);
                         robot.mouseMove(point.x, point.y);
                         JOptionPane.showMessageDialog(InvoiceEditWidget.this, "Point clicked is : (" + point.x + " , " + point.y + ")");
                         if (true) {
                         return;
                         }
                         robot.mousePress(InputEvent.getMaskForButton(1));
                         robot.mouseRelease(InputEvent.getMaskForButton(1));*/
                        tableItemsList.remove(r);
                        tableModel.fireTableDataChanged();
                        InvoiceEditWidget.this.repaint();
                        salesTable.repaint();
                        point = new Point(salesTable.getTableHeader().getHeaderRect(1).x + 12,
                                salesTable.getTableHeader().getHeaderRect(1).y + 3);
                        SwingUtilities.convertPointToScreen(point, salesTable.getTableHeader());
                        robot.mouseMove(point.x, point.y);
                        robot.mousePress(InputEvent.getMaskForButton(1));
                        robot.mouseRelease(InputEvent.getMaskForButton(1));

                        computeTotal();
                        //Robot robot= new Robot();

                    } catch (AWTException ex) {
                        Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        });
        add(new JLabel(" "));//dummy
        add(new JLabel(" "));//dummy
        addButton = new JButton("Add Item");
        addButton.addActionListener(new AddButtonListener());
        addButton.setIcon(new ImageIcon(this.getClass().getResource("add24.png")));
        add(addButton, "wrap");
        // "cell column row width height"  cell 3 2 50 20,

        tableModel = new SalesTableModel();
        salesTable = new SalesTable(tableModel);
        salesTable.setRowHeight(20);
        Enumeration enumeration = salesTable.getColumnModel().getColumns();
        TableColumn aColumn;
        int index = 0;

        while (enumeration.hasMoreElements()) {

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            //if (!(index == InvoiceConstants.SL_NO_COL || index == InvoiceConstants.DESC_COL)) {
            renderer.setHorizontalAlignment(SwingConstants.LEFT);
            ((TableColumn) enumeration.nextElement()).setCellRenderer(renderer);
            //}

            index++;
        }

        salesTable.setCellSelectionEnabled(true);
        salesTable.setSelectionBackground(Color.lightGray);
        salesTable.setSelectionForeground(Color.white);
        salesTable.setRowHeight(25);
        //JTable salesTable = new JTable(model);
        JScrollPane scroller = new JScrollPane(salesTable);
        salesTable.setFillsViewportHeight(true);
        // model.fireTableDataChanged();
        add(scroller, "span,wrap,grow");
        TableColumn itemColumn = salesTable.getColumnModel().getColumn(1);
        productCombo = new JComboBox();
        ArrayList<String> productList = new ArrayList<String>();
        for (String key : productMap.keySet()) {
            productList.add(key);
            //buyerCombo.addItem(key);
        }
        if (productList != null && productList.size() >= 0) {
            Collections.sort(productList);

            for (String pname : productList) {
                //buyerList.add(key);
                productCombo.addItem(pname);

            }
        }
        itemColumn.setCellEditor(new DefaultCellEditor(productCombo));
        label = new JLabel("VAT/CST :");
        taxRateTextField = new JTextField();
        taxRateTextField.setText("0.00");
        taxRateTextField.getDocument().addDocumentListener(this);
        add(label);
        add(taxRateTextField, "grow");
        label = new JLabel("Tax Type :");
        taxTypeCombo = new JComboBox();
        add(label);
        taxTypeCombo.addItem("VAT");
        taxTypeCombo.addItem("CST");
        add(taxTypeCombo);

        formCCheckBox = new JCheckBox("AgainstForm 'C'");
        add(formCCheckBox, "grow,wrap");
        label = new JLabel("Remarks :");
        add(label);
        remarksField = new JTextField("Accessories : 1 Bag");
        add(remarksField, "grow");
        add(new JLabel(" "));//dummy
        label = new JLabel("Total(Rs.):");
        totalTextField = new JTextField();
        add(label);
        add(totalTextField, "grow,wrap,right");
        viewAllButton = new JButton("View all Invoices");
        add(viewAllButton);
        viewAllButton.setIcon(new ImageIcon(this.getClass().getResource("viewall24.png")));
        printXLButton = new JButton("Print In Excel");
        add(printXLButton);
        printXLButton.setIcon(new ImageIcon(this.getClass().getResource("print24.png")));
        printXLButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fillUpMapsFromDisplayedValues();
                new print.spreadsheet.PrintManager().printInExcel(widgetValues, productMap);
            }
        });
        printFormButton = new JButton("Print In Form");
        add(printFormButton);
        printFormButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fillUpMapsFromDisplayedValues();
                new print.form.PrintManager().printInForm(widgetValues, productMap);
            }
        });
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteButtonListener());
        add(deleteButton);
        deleteButton.setIcon(new ImageIcon(this.getClass().getResource("delete24.png")));
        saveButton = new JButton("Save");
        saveButton.setIcon(new ImageIcon(this.getClass().getResource("save24.png")));
        saveButton.addActionListener(new SaveButtonListener());
        add(saveButton);
        populateWidgets();
        //    add(label);

    }

    public void populateWidgets() {
        try {
            String yearString = "";
            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                return;//yearLabel = new JLabel("Financial Year : None Selected");
            } else {
                yearString = "BETWEEN '" + InvoiceConstants.FIN_YEAR_FIRST_PART + "-04-01' AND '" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "-03-31' ";
            }

            String q = "SELECT MAX(`invno`) from `Invoices` WHERE `invdate` " + yearString;// WHERE `name`=" + "\'" + (String) buyerCombo.getSelectedItem() + "\'";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(q);
            int i = 0;
            if (rs.next()) {
                i = rs.getInt(1);
                invoiceNoTextField.setText(Integer.toString(i + 1));
                refresh();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        // invoiceNoTextField.setText("112");
        //taxRateTextField.setText("5.5");
    }

    public void loadValuesFromHashMaps() {
        if (invoiceNoTextField.getText() == null || invoiceNoTextField.getText().isEmpty()) {
            this.tableItemsList = new ArrayList<ArrayList>();
            this.tableModel.fireTableDataChanged();
            this.taxRateTextField.setText("0.00");
            //  this.dChooser.setDate(new Date());
            this.totalTextField.setText("0");
            return;

        }
        widgetValues = new HashMap<Integer, Object>();
        widgetValues.put(InvoiceConstants.INV_NO_KEY, invoiceNoTextField.getText());
        new DatabaseManager().loadValuesIntoHashMapFromDB(conn, widgetValues, productMap, buyerMap);
        if (widgetValues.get(InvoiceConstants.INV_DATE_KEY) == null) {
           // dChooser.setDate(new Date());
            Date firstDate = null;
            Date lastDate = null;
            Date newDate = null;
            try {
                newDate = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                firstDate = sdf.parse("01-04-" + InvoiceConstants.FIN_YEAR_FIRST_PART);

                lastDate = sdf.parse("31-03-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
            } catch (Exception ex) {
                Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (newDate.before(firstDate) || newDate.after(lastDate)) {

                dChooser.setDate(firstDate);

            } else {
                dChooser.setDate(newDate);
            }

        } else {
            dChooser.setDate((Date) widgetValues.get(InvoiceConstants.INV_DATE_KEY));
        }

        if (widgetValues.get(InvoiceConstants.BUYER_NAME_KEY) != null) {
            buyerCombo.setSelectedItem(widgetValues.get(InvoiceConstants.BUYER_NAME_KEY));
        }
        tableItemsList = (ArrayList<ArrayList>) widgetValues.get(InvoiceConstants.ITEM_LIST_KEY);
        if (tableItemsList == null) {
            this.tableItemsList = new ArrayList<ArrayList>();
        }
        tableModel.fireTableDataChanged();
        if (widgetValues.get(InvoiceConstants.TAX_RATE_KEY) == null) {
            this.totalTextField.setText("0");
        } else {
            taxRateTextField.setText(String.valueOf((Double) widgetValues.get(InvoiceConstants.TAX_RATE_KEY)));
        }
        if (widgetValues.get(InvoiceConstants.TAX_TYPE_KEY) == null) {
            this.taxTypeCombo.setSelectedItem("VAT");
        } else {
            taxTypeCombo.setSelectedItem(widgetValues.get(InvoiceConstants.TAX_TYPE_KEY));
        }
        String c = widgetValues.get(InvoiceConstants.FORM_C_KEY) + "";
        if (c.equals("y") || c.equals("Y")) {
            formCCheckBox.setSelected(true);
        } else {
            formCCheckBox.setSelected(false);
        }
        if (widgetValues.get(InvoiceConstants.REMARKS_KEY) == null) {
            remarksField.setText("");
        } else {
            c = widgetValues.get(InvoiceConstants.REMARKS_KEY) + "";
            remarksField.setText(c);
        }
        //if (tableItemsList != null && tableItemsList.size() != 0) {
        computeTotal();
        // }

    }

    public void refresh() {
        this.fillProductAndBuyerLists();
        productCombo.removeAllItems();
        ArrayList<String> productList = new ArrayList<String>();
        for (String key : productMap.keySet()) {
            productList.add(key);
            //buyerCombo.addItem(key);
        }
        if (productList != null && productList.size() >= 0) {
            Collections.sort(productList);

            for (String pname : productList) {
                //buyerList.add(key);
                productCombo.addItem(pname);

            }
        }

        buyerCombo.removeAllItems();
        ArrayList<String> buyerList = new ArrayList<String>();
        for (String key : buyerMap.keySet()) {
            buyerList.add(key);
            //buyerCombo.addItem(key);
        }
        if (buyerList != null && buyerList.size() >= 0) {
            Collections.sort(buyerList);

            for (String bname : buyerList) {
                //buyerList.add(key);
                buyerCombo.addItem(bname);
            }

        }

    }

    public void changeFinancialYear() {
        yearLabel.setText("Financial Year : " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
        Date firstDate = null;
        Date lastDate = null;
        Date newDate = null;
        try {
            newDate = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            firstDate = sdf.parse("01-04-" + InvoiceConstants.FIN_YEAR_FIRST_PART);

            lastDate = sdf.parse("31-03-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
        } catch (Exception ex) {
            Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (newDate.before(firstDate) || newDate.after(lastDate)) {

            dChooser.setDate(firstDate);

        } else {
            dChooser.setDate(newDate);
        }

    }

    public static void main(String[] args) {
        //  new InvoiceEditWidget().go();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        computeTotal();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (taxRateTextField.getText() != null && !taxRateTextField.getText().isEmpty()) {
            computeTotal();
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        computeTotal();
    }

    /**
     * @return tButton
     */
    public JButton getViewAllButton() {
        return viewAllButton;
    }

    class SalesTableModel extends AbstractTableModel {

        String[] columnNames = {"S.No", "Description", "Quantity", "Price", "Amount"};

        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public int getRowCount() {
            if (!(tableItemsList == null)) {
                return tableItemsList.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getColumnCount() {

            return InvoiceConstants.TOTAL_COLS;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex != InvoiceConstants.AMOUNT_COL) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex >= tableItemsList.size()) {
                return;
            }
            ArrayList one = (ArrayList) tableItemsList.get(rowIndex);
            // if (columnIndex != InvoiceConstants.AMOUNT_COL) {

            if (columnIndex == InvoiceConstants.PRICE_COL) {
                BigDecimal price = ((BigDecimal) aValue).setScale(2, RoundingMode.HALF_UP);
                one.set(columnIndex, price);
            }

            if (columnIndex == InvoiceConstants.QTY_COL) {

                one.set(columnIndex, aValue);
            }

            if (columnIndex == InvoiceConstants.PRICE_COL || columnIndex == InvoiceConstants.QTY_COL) {
                computeTotal();
                BigDecimal qty = new BigDecimal((Integer) one.get(InvoiceConstants.QTY_COL));
                BigDecimal price = ((BigDecimal) one.get(InvoiceConstants.PRICE_COL)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal amount = qty.multiply(price).setScale(2, RoundingMode.UP);
                //   one.set(InvoiceConstants.QTY_COL, )
                one.set(InvoiceConstants.AMOUNT_COL, amount);
                fireTableDataChanged();
                computeTotal();
                return;
            }
            one.set(columnIndex, aValue);
            fireTableDataChanged();
        }

        @Override
        public Class getColumnClass(int c) {
            if (c == InvoiceConstants.SL_NO_COL) {
                return Integer.class;
            }
            if (c == InvoiceConstants.DESC_COL) {
                return String.class;
            }
            if (c == InvoiceConstants.QTY_COL) {
                return Integer.class;
            }
            if (c == InvoiceConstants.PRICE_COL || c == InvoiceConstants.AMOUNT_COL) {
                return BigDecimal.class;
            }

            return getValueAt(0, c).getClass();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            ArrayList one = (ArrayList) tableItemsList.get(rowIndex);
            if (columnIndex == InvoiceConstants.SL_NO_COL) {
                return rowIndex + 1;
            }

            return one.get(columnIndex);

        }
    }

    public void fillProductAndBuyerLists() {
        try {
            String query = "SELECT `productID`,`name` from `products` ORDER BY `name`";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            productMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String name = rs.getString("name");
                productMap.put(name, rs.getInt("productID"));
            }
            for (String key : productMap.keySet()) {
                System.out.println(key + " : " + productMap.get(key));
            }
            query = "SELECT `buyerID`,`name` from `buyers` ORDER BY `name`";
            // stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            buyerMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String name = rs.getString("name");
                buyerMap.put(name, rs.getInt("buyerID"));
            }
            for (String key : buyerMap.keySet()) {
                System.out.println(key + " : " + buyerMap.get(key));
            }

        } catch (SQLException ex) {
            Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public BigDecimal roundOffToTwoDecimals(BigDecimal d) {
        BigDecimal hundred = new BigDecimal(100);
        System.out.println("d  is " + d);
        BigDecimal d1 = d.multiply((hundred));
        System.out.println("d*100  is " + d1);
        BigDecimal d2 = d1.round(new MathContext(BigDecimal.ROUND_HALF_UP));
        BigDecimal rounded = d2.divide(hundred);
        System.out.println("Rounded off value of " + d + " is " + rounded);
        // rounded.se
        return rounded;
    }

    public void computeTotal() {
        total = new BigDecimal(0);
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total = total.add((BigDecimal) tableModel.getValueAt(i, InvoiceConstants.AMOUNT_COL));
        }
        System.out.println(" total is " + total.toPlainString());
        total = total.setScale(2, RoundingMode.UP);
        BigDecimal taxRate1 = new BigDecimal(0);
        if (taxRateTextField.getText() != null && (!taxRateTextField.getText().isEmpty())) {
            try {
                System.out.println("Tax Rate in computeTotal is " + taxRateTextField.getText());
                taxRate1 = new BigDecimal(Double.parseDouble(taxRateTextField.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        taxRate = taxRate1.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = total.multiply(taxRate.divide(new BigDecimal(100.0)));
        taxAmount = tax.setScale(2, RoundingMode.UP);
        grTotal = total.add(taxAmount);
        System.out.println("Gr total is " + grTotal.toString());
        grTotalRounded = grTotal.setScale(0, RoundingMode.HALF_UP);
        System.out.println("Gr  total rounded is  " + grTotalRounded.toPlainString());
        totalTextField.setText(grTotalRounded.toString());
    }

    public void fillUpMapsFromDisplayedValues() {
        try {
            String query = "SELECT `productID`,`name` from `products`ORDER BY `name`";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            productMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String name = rs.getString("name");
                productMap.put(name, rs.getInt("productID"));
            }
            for (String key : productMap.keySet()) {
                System.out.println(key + " : " + productMap.get(key));
            }
            query = "SELECT `buyerID`,`name` from `buyers` ORDER BY `name`";
            // stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            buyerMap = new HashMap<String, Integer>();
            while (rs.next()) {
                String name = rs.getString("name");
                buyerMap.put(name, rs.getInt("buyerID"));
            }

            widgetValues = new HashMap<Integer, Object>();
            widgetValues.put(InvoiceConstants.INV_NO_KEY, invoiceNoTextField.getText());
            widgetValues.put(InvoiceConstants.INV_DATE_KEY, dChooser.getDate());
            String bName = "" + buyerCombo.getSelectedItem();
            widgetValues.put(InvoiceConstants.BUYER_NAME_KEY, bName);
            widgetValues.put(InvoiceConstants.BUYER_ID_KEY, buyerMap.get(bName));
            query = "SELECT `buyerID`,`name`,`address`,`TINNo`,`CSTNo` from `buyers` WHERE `buyerID`='"
                    + widgetValues.get(InvoiceConstants.BUYER_ID_KEY) + "'";

            rs = stmt.executeQuery(query);

            if (rs.next()) {

                widgetValues.put(InvoiceConstants.BUYER_ADDRESS_KEY, rs.getString("address"));
                widgetValues.put(InvoiceConstants.BUYER_TINNO_KEY, rs.getString("TINNo"));
                widgetValues.put(InvoiceConstants.BUYER_CSTNO_KEY, rs.getString("CSTNo"));
            }
            if (tableItemsList != null) {
                widgetValues.put(InvoiceConstants.ITEM_LIST_KEY, tableItemsList);
                ArrayList row;
                for (int i = 0; i < tableItemsList.size(); i++) {
                    row = (ArrayList) tableItemsList.get(i);
                    System.out.println();
                    for (int j = 0; j < row.size(); j++) {
                        System.out.print(row.get(j));
                    }
                }
            }

            if (formCCheckBox.getSelectedObjects() != null) {
                widgetValues.put(InvoiceConstants.FORM_C_KEY, "Y");
            } else {
                widgetValues.put(InvoiceConstants.FORM_C_KEY, "N");
            }
            String remarksString = "";
            if (remarksField.getText() != null) {
                remarksString = remarksField.getText();
            }
            widgetValues.put(InvoiceConstants.REMARKS_KEY, remarksString);
            computeTotal();
            widgetValues.put(InvoiceConstants.TOTAL_KEY, total);
            widgetValues.put(InvoiceConstants.TAX_RATE_KEY, taxRate);
            widgetValues.put(InvoiceConstants.TAX_AMOUNT_KEY, taxAmount);
            widgetValues.put(InvoiceConstants.TAX_TYPE_KEY, taxTypeCombo.getSelectedItem().toString());
            widgetValues.put(InvoiceConstants.ROUND_OFF_KEY, grTotalRounded.subtract(grTotal));
            BigDecimal t = grTotalRounded;//.setScale(2,RoundingMode.HALF_UP);
            t.setScale(2);
            widgetValues.put(InvoiceConstants.GRAND_TOTAL_KEY, t);
            for (Object key : widgetValues.keySet()) {
                System.out.println("Values " + widgetValues.get(key));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter correct values in all fields!");
            ex.printStackTrace();
            Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class AddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (InvoiceConstants.FIN_YEAR_FIRST_PART <= 0) {
                JOptionPane.showMessageDialog(null, "There are no financial years available.\nCreate/Select  financial year using Edit->Manage Years MenuItem.");
                return;
            }
            ArrayList l = new ArrayList<Object>();
            //the saved value table_row_count  is not used ;
            //saved just for postional correspondence of arraylist with table
            l.add(++table_row_count);
            l.add(productMap.keySet().toArray()[0]);
            l.add(0);
            l.add(new BigDecimal(0.00));
            l.add(new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP));
            tableItemsList.add(l);
            tableModel.fireTableDataChanged();
            // computeTotal();
        }
    }

    class SaveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (InvoiceEditWidget.this.invoiceNoTextField.getText() == null
                        || invoiceNoTextField.getText().trim() == "" || Integer.parseInt(invoiceNoTextField.getText()) <= 0) {
                    JOptionPane.showMessageDialog(null, "Cannot save.The Invoice No. should be  positive integer.\n Enter correct Invoice No. before saving.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Cannot save. The Invoice No. should be  positive integer.\n Enter correct Invoice No. before saving.");
                return;
            }
            Date newDate = (Date) dChooser.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date firstDate = null;
            Date lastDate = null;
            try {
                firstDate = sdf.parse("01-04-" + InvoiceConstants.FIN_YEAR_FIRST_PART);

                lastDate = sdf.parse("31-03-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1));
            } catch (Exception ex) {
                Logger.getLogger(InvoiceEditWidget.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (newDate.before(firstDate) || newDate.after(lastDate)) {
                JOptionPane.showMessageDialog(null, "The selected date is outside the currently active financial year "
                        + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "."
                        + "Select date in the financial year " + InvoiceConstants.FIN_YEAR_FIRST_PART + "-" + (InvoiceConstants.FIN_YEAR_FIRST_PART + 1) + "."
                        + "\n Or create/select correct financial year using Edit->Manage Years MenuItem.");
                // dChooser.setDate(firstDate);
                return;

            }
            fillUpMapsFromDisplayedValues();
            new DatabaseManager().saveValues(conn, widgetValues, productMap);
        }
    }

    class DeleteButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (JOptionPane.showConfirmDialog(null, "Delete this invoice (Invoice No.: " + Integer.valueOf(invoiceNoTextField.getText()) + ")?", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            };
            new DatabaseManager().deleteInvoice(conn, Integer.valueOf(invoiceNoTextField.getText()));
            loadValuesFromHashMaps();
        }
    }
}

class SalesTable extends JTable {

    public SalesTable(AbstractTableModel model) {
        super(model);
    }

    @Override
    public void changeSelection(int row, int column, boolean toggle, boolean extend) {
        super.changeSelection(row, column, toggle, extend);

        if (editCellAt(row, column)) {
            Component editor = getEditorComponent();
            editor.requestFocusInWindow();
        }
    }
}

class InvoicDateChooser extends JDateChooser {

    @Override
    public void actionPerformed(ActionEvent ae) {
        //To change body of generated methods, choose Tools | Templates.
        // getd
    }

}
