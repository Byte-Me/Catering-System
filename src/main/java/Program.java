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
            // Load system Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (!System.getProperty("os.name").equals("Mac OS X")) { // Check if OS is Mac OS X
                UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel"); // Better look for windows (works on linux?)
            }

        } catch (Exception e) { // Should not fail, will only fall back to system UI
            e.printStackTrace();
        }

        Login loginForm = new Login(); // Open login screen - FIXME: If not used, remove reference to object
    }
}
