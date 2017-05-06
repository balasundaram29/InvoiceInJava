package bala.invoice.gui;

import bala.invoice.db.DBUtilities;
import bala.invoice.db.DatabaseManager;
import bala.invoice.db.ScriptRunner;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import net.miginfocom.swing.MigLayout;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author AnnaiAll
 */
public class SalesGui extends JFrame implements WindowListener {

    int INV_NO_KEY = 0;
    int INV_DATE_KEY = 1;
    int BUYER_ID_KEY = 2;
    int TAX_RATE_KEY = 3;
    int ROW_LIST_KEY = 4;
    int SUM_KEY = 5;
    int TAX_AMOUNT_KEY = 6;
    int GRAND_TOTAL_KEY = 7;
    int tableRowCount = 0;
    HashMap<Integer, Object> widgetValues = new HashMap<Integer, Object>();
    HashMap<String, Integer> productDict = new HashMap<String, Integer>();
    HashMap<String, Object> buyerDict = new HashMap<String, Object>();
    InvoiceEditWidget invoiceEditWidget;
    InvoiceListWidget invoiceListWidget;
    BuyerWidget buyerWidget;
    ProductWidget productWidget;
    YearWidget yearWidget;

    JTable table;
    // buyerDict={}

    public SalesGui() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newInvMenuItem = new JMenuItem("New Invoice");
        fileMenu.add(newInvMenuItem);
        newInvMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        if (JOptionPane.showConfirmDialog(null, "Proceed to new Invoice? Click 'Yes' only if you have no unsaved work.", "New Invoice", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                            return;
                        }
                        invoiceEditWidget.populateWidgets();
                        invoiceEditWidget.loadValuesFromHashMaps();
                    }
                }
        //ae->System.out.println("");
        //JOptionPane.showConfirmDialog(null,"", null, WIDTH);
        );
        JMenu editMenu = new JMenu("Edit");
        JMenuItem productMenuItem = new JMenuItem("Manage Products");
        editMenu.add(productMenuItem);
        JMenuItem buyerMenuItem = new JMenuItem("Manage Buyers");
        editMenu.add(buyerMenuItem);
        JMenuItem yearMenuItem = new JMenuItem("Manage Years");
        editMenu.add(yearMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        Rectangle bounds = null;
        setTitle("Invoice - Sales ");
        invoiceEditWidget = InvoiceEditWidget.getInstance();
        setContentPane(invoiceEditWidget);
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream("frame_bounds");
            ois = new ObjectInputStream(fis);
            bounds = (Rectangle) ois.readObject();
            setBounds(bounds);

        } catch (Exception ex) {
            ex.printStackTrace();
            pack();
        }
        addWindowListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
        int cols = invoiceEditWidget.salesTable.getColumnCount();
        for (int col = 0; col < cols; col++) {
            try {
                invoiceEditWidget.salesTable.getColumnModel().getColumn(col).setPreferredWidth(ois.readInt());
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(SalesGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        invoiceListWidget = new InvoiceListWidget();
        invoiceListWidget.getBackButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //SalesGui.this.getContentPane().removeAll();
                //SalesGui.this.getContentPane().invalidate();
                //invoiceEditWidget=new InvoiceEditWidget();
                // SalesGui.this.setContentPane(invoiceEditWidget);
                // SalesGui.this.validate();
                //SalesGui.this.repaint();
                // invoiceEditWidget = new InvoiceEditWidget();
                //setContentPane(invoiceEditWidget);
                //validate();

                SalesGui.this.setContentPane(invoiceEditWidget);
                SalesGui.this.validate();
            }
        });
        JButton viewAllButton = invoiceEditWidget.getViewAllButton();
        viewAllButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // SalesGui.this.getContentPane().removeAll();
                //SalesGui.this.getContentPane().invalidate();
                //invoiceListWidget= new InvoiceListWidget();
                invoiceListWidget.refresh();
                setContentPane(invoiceListWidget);
                validate();
                //SalesGui.this.repaint();
            }
        });

        table = invoiceListWidget.getTable();
        table.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    goToSalesEntry(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            public void goToSalesEntry(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                // int col=table.columnAtPoint(e.getPoint());
                String invnoS = "" + table.getValueAt(row, 0);
                invoiceEditWidget.invoiceNoTextField.setText(invnoS);
                invoiceEditWidget.loadValuesFromHashMaps();
                setContentPane(invoiceEditWidget);
                validate();
            }
        });
        buyerWidget = new BuyerWidget();
        buyerMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(buyerWidget);
                validate();
            }
        });
        buyerWidget.getBackButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // invoiceEditWidget = new InvoiceEditWidget();
                invoiceEditWidget.refresh();
                SalesGui.this.setContentPane(invoiceEditWidget);
                SalesGui.this.validate();
            }
        });
        productWidget = new ProductWidget();
        productMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(productWidget);
                validate();
            }
        });

        productWidget.getBackButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //invoiceEditWidget = new InvoiceEditWidget();
                // invoiceEditWidget.fillProductAndBuyerLists();
                invoiceEditWidget.refresh();
                SalesGui.this.setContentPane(invoiceEditWidget);
                SalesGui.this.validate();
            }
        });
        yearWidget = new YearWidget();
        yearMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(yearWidget);
                validate();
            }
        });
        yearWidget.getBackButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //invoiceEditWidget = new InvoiceEditWidget();
                // invoiceEditWidget.fillProductAndBuyerLists();
                invoiceEditWidget.refresh();
                SalesGui.this.setContentPane(invoiceEditWidget);
                SalesGui.this.validate();
            }
        });
    }

    public static void main(String[] args) {
        //DatabaseManager.createDatabaseAndTables();
        //DatabaseManager.createTriggersOnly(DBUtilities.getConnection());
        try {
            /*String databaseName = "InvoiceDB";
             String userName = "root";
             String password = "";

             String url = "jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=convertToNull";
             Connection connection = DriverManager.getConnection(url, userName, password);
             // Give the input file to Reader
             Reader reader = new BufferedReader(
             new FileReader("InvoiceDBWithTriggers.sql"));
             ScriptRunner scriptRunner=new ScriptRunner(connection,false,true);
             scriptRunner.runScript(reader);
             */
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SalesGui();
            }
        });

    }

    @Override
    public void windowOpened(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowClosing(WindowEvent e) {

        FileOutputStream fs = null;
        ObjectOutputStream os = null;
        try {
            fs = new FileOutputStream("frame_bounds");
            os = new ObjectOutputStream(fs);
            Rectangle bounds = getBounds();//getSize();

            os.writeObject(bounds);
            int cols = invoiceEditWidget.salesTable.getColumnCount();
            for (int col = 0; col < cols; col++) {
                int i = invoiceEditWidget.salesTable.getColumnModel().getColumn(col).getWidth();
                os.writeInt(i);
            }
            //throw new UnsupportedOperationException("Not supported yet.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(SalesGui.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.close();
                fs.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(SalesGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
