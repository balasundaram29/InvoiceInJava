/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package print.form;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.HashMap;

/**
 *
 * @author AnnaiAll
 */
public class FormPrinter implements Printable {

    PrintPanel printPanel;

    public FormPrinter(PrintPanel printPanel) {
        this.printPanel = printPanel;
    }

    public FormPrinter(HashMap widgetValues, HashMap<String, Integer> productMap) {
        this.printPanel = new PrintPanel(widgetValues, productMap);
    }

    public FormPrinter() {
        // this.printPanel = printPanel;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2D = (Graphics2D) graphics;
        g2D.translate(0, -18);
        printPanel.paint(g2D);
        return PAGE_EXISTS;
    }

}
