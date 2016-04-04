package GUI;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * Created by olekristianaune on 04.04.2016.
 */
public class HelpWindow extends JFrame{
    private JPanel mainPanel;
    Browser browser;

    public HelpWindow() {
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(browserView, BorderLayout.CENTER);

        browser.loadURL("http://byte-me.github.io/Catering-System/");

        pack();
        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
