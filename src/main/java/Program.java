import GUI.Login;
import com.apple.eawt.Application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by olekristianaune on 07.03.2016.
 */
public class Program {
    public static void main(String[] args) throws Exception {
        try {
            // Load system Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (!System.getProperty("os.name").equals("Mac OS X")) { // Check OS is not Mac OS X
                UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel"); // Better look for windows (works on linux?)
            } else {
                // Mac Stuff

                // Set Dock Icon
                Image icon = Toolkit.getDefaultToolkit().getImage(Program.class.getResource("/Images/appIcon.png"));
                Application.getApplication().setDockIconImage(icon);

                // Custom content in Mac About Menu Item
                class AboutWindow extends JFrame {
                    public AboutWindow() {
                        JPanel p = new JPanel();
                        JLabel l = new JLabel("\u00A9 Healthy Catering LTD, 2016");
                        p.add(l);
                        add(p);
                        p.setBorder(new EmptyBorder(10, 10, 10, 10));
                        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        pack();
                        setLocationRelativeTo(getParent());
                        setVisible(true);
                    }
                }

                Application.getApplication().setAboutHandler(paramAboutEvent -> {
                    new AboutWindow();
                });
            }

        } catch (Exception e) { // Should not fail, will only fall back to system UI
            e.printStackTrace();
        }

        new Login();
    }
}
