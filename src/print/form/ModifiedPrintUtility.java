package print.form;



import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import jxl.format.Border;
import net.miginfocom.swing.MigLayout;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bala
 */
public class ModifiedPrintUtility implements Printable {

    Component componentToBePrinted;
   
    int originalW, originalH;
    Rectangle rect;
    Container frame;

    public ModifiedPrintUtility() {
       // this.entryPanel = entryPanel;
        //this.type = type;

    }



    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        PrinterJob pj = PrinterJob.getPrinterJob();
        File file = new File("resources//myPFValues.pf");
        PageFormat pf = pj.defaultPage();
        Paper paper= new Paper();
        paper.setSize(210,307);
        paper.setImageableArea(20,20, 700, 1200);
       
        pf.setPaper(paper);
         //  pf.
      //  PageFormat pageFormat = pj.pageDialog(pf);

        // PageFormat pageFormat = pj.pageDialog(pj.defaultPage());
        int paperWidth =700;// (int) pageFormat.getWidth();
        int paperHeight =300;// (int) pageFormat.getHeight();
        int prnW = 650;//(int) pageFormat.getImageableWidth();
        int prnH = 250;//(int) pageFormat.getImageableHeight();
        int imX = 45;//(int) pageFormat.getImageableX();
        int imY = 40;//(int) pageFormat.getImageableY();
        

        JFrame f = new JFrame("Print Preview");
        f.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        f.setSize(paperWidth, paperHeight + 8);
        f.setLayout(null);
        // f.setLayout(new MigLayout("insets 0 0 0 0","",""));
        f.setBackground(Color.white);
      
       printJob.setPrintable(this, pf);
        if (printJob.printDialog()) {
            try {
                printJob.print();

            } catch (PrinterException pe) {
                //   System.out.println("Error printing: " + pe);
            }
        }
        f.dispose();
        //restore graphPanel to original size
        //    gPanel.setWAndH(originalW, originalH);
       // gPanel.setSize(originalW, originalH);
//        gPanel.setBounds(rect);
        //the graph pnel has been removed from original OpeningFrame object when attaching to printable Frame.
        //Now we can restore it 
   //     if (type == ComponentToBePrintedType.GRAPH || type == ComponentToBePrintedType.ECONOMY_REPORT) {
     //       frame.add(gPanel);

       //     gPanel.repaint();
        //}
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {

        if (pageIndex > 0) {
            return (NO_SUCH_PAGE);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            // g2d.drawLine(0, 0, 400,500);
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            //  componentToBePrinted.setSize((int) pageFormat.getPaper().getImageableWidth(), (int) pageFormat.getPaper().getImageableHeight());
            disableDoubleBuffering(componentToBePrinted);
            // g2d.setClip(1,1,(int)pageFormat.getImageableWidth()-1,(int)pageFormat.getImageableHeight()-1);
            componentToBePrinted.paint(g2d);
          //  g2d.translate(1, 0);
              componentToBePrinted.paint(g2d);
/*            ReportPanel rp = (ReportPanel) componentToBePrinted;
            int x = rp.getReportTable().getX();
            int y = rp.getReportTable().getY();
            int w = rp.getReportTable().getWidth();
            int h = rp.getReportTable().getHeight();*/
            // g2d.drawRect(1,1,(int)pageFormat.getImageableWidth()-1,(int)pageFormat.getImageableHeight()-1);
           // g2d.drawLine(x, y, w + x, h + y);
            //g2d.drawLine(x, y + h, x + w, y);
            //g2d.setClip(null);
            enableDoubleBuffering(componentToBePrinted);
            return (PAGE_EXISTS);
        }
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
