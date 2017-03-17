/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package print.form;

import bala.invoice.gui.InvoiceConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author AnnaiAll
 */
public class PrintManager {

    public static int INV_NO_V_DISTANCE = 300;
    public static int INV_NO_H_DISTANCE = 380;

    public void printInForm(HashMap widgetValues, HashMap<String, Integer> productMap) {
        //PAPER SIZE 210 X 297 MM IS A4
        System.out.println("Printing Form");
        PrintPanel printPanel = new PrintPanel(widgetValues, productMap);
        printPanel.setSize(737, 842);
        JFrame f = new JFrame("Print Preview");
        f.setLayout(null);
        f.setContentPane(printPanel);//new Printadd(printPanel);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // f.pack();
        System.out.println("panel width=" + printPanel.getWidth() + " panel height= " + printPanel.getHeight());

        f.setSize(printPanel.getWidth(), printPanel.getHeight());
        f.setVisible(true);
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        FormPrinter formPrinter = new FormPrinter(widgetValues,productMap);
        PageFormat page=printerJob.defaultPage();
        Paper paper= new Paper();
        paper.setSize(737,842);
        paper.setImageableArea(10,10,717,822);
        page.setPaper(paper);
        printerJob.setPrintable(formPrinter,page);
        printerJob.setPrintable(formPrinter);
        if (printerJob.printDialog()) {
            try {
                System.out.println("Clicked ok");

                printerJob.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
                Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
