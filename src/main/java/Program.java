import GUI.*;
import Delivery.*;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class Program {
    public static void main(String[] args) throws Exception {
        try {
            // Set system to Mac to get mac look and feel. Does this create issues?

            System.setProperty("os.name", "Mac OS X");
            System.setProperty("os.version", "10.11.3");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Does this load Mac look and feel on Windows?
            //UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.version"));
        */

        Login loginForm = new Login(); // Open login screen
    }
}
