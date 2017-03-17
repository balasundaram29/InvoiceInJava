package util;


import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
public class NumberToWordsConverter {

    static HashMap<Long, String> alphaDict;

    public static void  initNumberToWordsConverter() {
        alphaDict = new HashMap<Long, String>();
        String dict = " 0:'zero',1:'one',2:'two',3:'three',4:'four',5:'five',6:'six',7:'seven',8:'eight',9:'nine',"
                + "10:'ten',11:'eleven',12:'twelve',13:'thirteen',14:'fourteen',15:'fifteen',16:'sixteen',"
                + "17:'seventeen',18:'eighteen',19:'nineteen',20:'twenty',30:'thirty',40:'forty',50:'fifty',"
                + "60:'sixty',70:'seventy',80:'eighty',90:'ninety'";
        String[] pairs = dict.split(",");
        for (int i = 0; i < pairs.length; i++) {
            String[] parts = pairs[i].split(":");
            alphaDict.put(Long.parseLong(parts[0].trim()), parts[1].replace("'", ""));
        }

    }

   

    public static String convertToWords(long n) {
        initNumberToWordsConverter();
        String s = convertAnywhere(n);
        s = s.trim();
        if (s.startsWith("and")) {
            s = s.replaceFirst("and", "");
        }
        if(s==null||s.isEmpty()){
            return "zero";
        }
        return s.trim();
    }

    private static String convertWithinHundred(long n) {
        String s="";
        if (n <= 20) {
            s=alphaDict.get(n);
            return s;//alphaDict.get(n);
        } else {
            long ones = n % 10;
            long tens = (n / 10) * 10;
            s=alphaDict.get(tens) ;
            //we do not want zero printed anywhere in-between
            if(ones!=0)
            s=s+" "+ alphaDict.get(ones);
        }
        return s;
    }

     private static String convertWithinThousand(long n) {
        long hundreds = n / 100;
        long remainder = n % 100;
        String s = "";
        if (hundreds > 0) {
            s = alphaDict.get(hundreds) + " " + "hundred";
        }
        if (remainder != 0) {
            s = s + " and " + convertWithinHundred(remainder);
        }
        return s;
    }

     private static String convertWithinLakh(long n) {
        long thousands = n / 1000;
        long remainder = n % 1000;
        String s = "";

        if (thousands > 0) {
            s = convertWithinHundred(thousands) + "  " + "thousand ";
            if (remainder != 0) {
                s = s + convertWithinThousand(remainder);
            }
        } else {
               s = convertWithinThousand(n);
        }
        return s;
    }
     private static String convertWithinCrore(long n) {
        long lakhs = n / 100000;
        long remainder = n % 100000;
        String s = "";

        if (lakhs > 0) {
            s = convertWithinHundred(lakhs) + "  " + "lakh ";
            if (remainder != 0) {
                s = s + convertWithinLakh(remainder);
            }
        } else {
               s = convertWithinLakh(n);
        }
        return s;
    }
      private static String convertAnywhere(long n) {
         long crores = n / 10000000;
        long remainder = n % 10000000;
        String s = "";

        if (crores > 0) {
            s = convertAnywhere(crores) + "  " + "crore ";
            if (remainder != 0) {
                s = s + convertWithinCrore(remainder);
            }
        } else {
               s = convertWithinCrore(n);
        }
        return s;
    }

    

}
