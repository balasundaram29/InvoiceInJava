
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.miginfocom.swing.MigLayout;
//import net.miginfocom.swing.MigLayout;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ANNAIENG
 */
enum Alignment {

    Horizontal, Vertical
}

public class MergableColumnsTable {

    private Alignment alignment;
    private ArrayList<ColumnGroup> groupList = new ArrayList<ColumnGroup>();
    private JTable jTable;
    private JTableHeader header;
    private final Object[] columnNames;

    public void addColumnGroup(ColumnGroup group) throws Exception {
        int x = 0;

        ArrayList<Integer> colList = group.getColList();
        int totalCols = colList.size();
        if (totalCols <= 1) {
            if (totalCols == 0) {
                return;//return 0;
            }
            groupList.add(group);
        }
        int cols[] = new int[colList.size()];
        for (int j = 0; j < totalCols; j++) {
            cols[j] = colList.get(j);
        }
        for (int col = 0; col < totalCols - 1; col++) {
            if (cols[col] != cols[col + 1] - 1) {
                throw new Exception("Merged Columns Should Contain Only AdjacentColumns");
            }
        }
        groupList.add(group);
    }

    public MergableColumnsTable(final DefaultTableModel model) {
        //  super(new MigLayout("", "[grow]", "[]0[grow]"));
        this.columnNames = new Object[model.getColumnCount()];
        this.alignment = Alignment.Vertical;
        jTable = new JTable(model) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setColumnModel(jTable.getColumnModel());
                Graphics2D g2D = (Graphics2D) g;
                g2D.setColor(Color.black);
                g2D.drawLine(0, 0, 0, getHeight());
            }
        };
        go();
    }

    public MergableColumnsTable(final Object[] columnNames, Object[][] dataObject) {
        this.columnNames = columnNames;
        this.alignment = Alignment.Vertical;
        jTable = new JTable(dataObject, columnNames) {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setColumnModel(jTable.getColumnModel());
                Graphics2D g2D = (Graphics2D) g;
                g2D.drawLine(0, 0, 0, getHeight());
                g2D.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        go();
    }

    public void go() {
        header = new JTableHeader() {

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setColumnModel(jTable.getColumnModel());
                Graphics2D g2D = (Graphics2D) g;
                g2D.setColor(Color.white);
                g2D.fillRect(0, 0, getWidth(), getHeight());
                g2D.setColor(Color.black);
                g2D.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
                int x = 0;//getInsets().left

                Font horFont = g2D.getFont();
                g2D.drawLine(0, 0, 0, getHeight());
                for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                    System.out.println("Colum No is " + i);
                    g2D.drawLine(x, 0, x + getColumnModel().getColumn(i).getWidth() - 1, 0);
                    //don't draw last column;drawn in enclosing rectangle
                    //if (i != getColumnModel().getColumnCount() - 1) {
                    g2D.drawLine(x + getColumnModel().getColumn(i).getWidth() - 1, 0, x + getColumnModel().getColumn(i).getWidth() - 1, getHeight());
                    //}
                    AffineTransform transform = new AffineTransform();
                    transform.setToRotation(-Math.PI / 2.0);
                    Font derivedFont = g2D.getFont().deriveFont(transform);
                    float offsetInc = getHeight() / 4;
                    if (alignment == Alignment.Vertical) {
                        g2D.setFont(derivedFont);
                        offsetInc = getColumnModel().getColumn(i).getWidth() / 4;
                    }
                    FontMetrics fm = g2D.getFontMetrics();
                    String[] sarray = jTable.getModel().getColumnName(i).split("<BR>");
                    float offset = 10;//fm.stringWidth("hsfjkfh");//getHeight();//float)(this.getWidth()/3.0);
                    if (getHeight() > 40 && alignment == Alignment.Horizontal) {
                        offset = getHeight() / 3;
                    }
                    for (int j = 0; j < sarray.length; j++) {
                        String s = sarray[j].replace("<HTML>", "");
                        s = s.replace("</HTML>", "");
                        if (alignment == Alignment.Vertical) {
                            g2D.drawString(s, x + offset, this.getHeight() - 2);
                        } else {
                            g2D.drawString(s, x, offset);
                        }
                        offset = offset + offsetInc;
                    }
                    x = x + getColumnModel().getColumn(i).getWidth();
                }
                g2D.setFont(horFont);
                FontMetrics fontMetrics = g2D.getFontMetrics();
                int len = 0;
                int w = 0;
                for (int gr = 0; gr < groupList.size(); gr++) {
                    ColumnGroup group = groupList.get(gr);
                    int leftCol = group.getLeftMostColumnIndex();
                    int rightCol = group.getRightMostColumnIndex();
                    x = getXLeft(columnModel, leftCol);
                    w = getMergedWidth(columnModel, group);
                    g2D.setColor(Color.white);
                    int h = group.getMergedAreaHeight();
                    g2D.clipRect(x - 1, 0, w, h + 1);
                    g2D.fillRect(x - 1, 0, w, h);
                    g2D.setColor(Color.black);
                    g2D.drawRect(x - 1, 0, w, h);
                    String text = group.getText();
                    len = fontMetrics.stringWidth(text);
                    int fontHeight = fontMetrics.getHeight();

                    g2D.drawString(text, x + w / 2 - len / 2, h / 2 + fontHeight / 2);

                    g2D.setClip(null);
                }
            }
        };
        header.setTable(jTable);
        jTable.setTableHeader(header);
        jTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    public void setMergedAreaHeight(int mergedAreaHeight) {
        if (groupList != null && groupList.size() != 0) {
            for (ColumnGroup group : groupList) {
                group.setMergedAreaHeight(mergedAreaHeight);
            }
        }
    }

    public static void main(String[] args) {
        try {
            int cols = 8;
            int rows = 6;
            Object[] colNames = new String[cols];
            Object[][] dataObject = new String[rows][cols];
            for (int col = 0; col < cols; col++) {
                colNames[col] = "Column " + (col);
                for (int row = 0; row < rows; row++) {
                    dataObject[row][col] = "row" + row + "," + "col" + col;
                }
            }
            MergableColumnsTable table = new MergableColumnsTable(colNames, dataObject);
            ColumnGroup group = new ColumnGroup();
            group.addColumn(1);
            group.addColumn(2);
            group.setText("HEAD, mWC");
            group.addColumn(3);
            table.addColumnGroup(group);
            JFrame f = new JFrame("Table - Mergable Columns");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel pane = new JPanel();
            //pane.setLayout(new GridBagLayout());
            pane.setLayout(new MigLayout("","","[grow][grow]"));
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.weighty = 1.00;
            c.weightx=0.5;
            table.getHeader().setPreferredSize(new Dimension(pane.getWidth(), 200));
            pane.add(table.getHeader(),"grow,span,wrap,gapbottom 0px");
            c.weightx=0.5;
            c.gridy = 1;
            c.weighty = 0.25;
            table.getjTable().setPreferredSize(new Dimension(pane.getWidth(), 200));
            
            pane.add(table.getjTable(),"grow,span,wrap,gaptop 0px");
            f.setContentPane(pane);//.add(table.getjTable());
            f.setSize(700, 600);
            f.setVisible(true);
            f.setAlwaysOnTop(true);
            table.getHeader().setPreferredSize(new Dimension(pane.getWidth(), 200));
            table.getjTable().setPreferredSize(new Dimension(pane.getWidth(), 200));
            // table.getjTable().setMinimumSize(new Dimension(table.getWidth(),table.getjTable().getRowCount()*table.getjTable().getRowHeight()));
        } catch (Exception ex) {
            Logger.getLogger(MergableColumnsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the jTable
     */
    public JTable getjTable() {
        return jTable;
    }

    public int getXLeft(TableColumnModel model, int column) {
        int x = 0;
        for (int col = 0; col < column; col++) {
            x = x + model.getColumn(col).getWidth();
        }
        return x;
    }

    public int getMergedWidth(TableColumnModel model, ColumnGroup group) {
        int x = 0;

        ArrayList<Integer> colList = group.getColList();
        int totalCols = colList.size();
        if (totalCols == 0) {
            return 0;
        }
        int cols[] = new int[colList.size()];
        for (int j = 0; j < totalCols; j++) {
            cols[j] = colList.get(j);
        }

        int w = 0;
        for (int col = 0; col < totalCols; col++) {
            w = w + model.getColumn(cols[col]).getWidth();
        }
        return w;
    }

    /**
     * @return the header
     */
    public JTableHeader getHeader() {
        return header;
    }

    /**
     * @param alignment the alignment to set
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
}

class ColumnGroup {

    private ArrayList<Integer> colList = new ArrayList<Integer>();
    private String text = "";
    private static int DEFAULT_MERGED_AREA_HEIGHT = 25;
    private int mergedAreaHeight;

    public void addColumn(int i) {
        if (!colList.contains(i)) {
            colList.add(i);
        }
        Collections.sort(colList);
    }

    public ColumnGroup() {
        this.mergedAreaHeight = DEFAULT_MERGED_AREA_HEIGHT;
    }

    /**
     * @return the group
     */
    public ArrayList<Integer> getColList() {
        return colList;
    }

    public int getLeftMostColumnIndex() {
        return colList.get(0);
    }

    public int getRightMostColumnIndex() {
        return colList.get(colList.size() - 1);
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the mergedAreaHeight
     */
    public int getMergedAreaHeight() {
        return mergedAreaHeight;
    }

    /**
     * @param mergedAreaHeight the mergedAreaHeight to set
     */
    public void setMergedAreaHeight(int mergedAreaHeight) {
        this.mergedAreaHeight = mergedAreaHeight;
    }
}
