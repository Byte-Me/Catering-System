package GUI;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * Created by olekristianaune on 04.04.2016.
 */
public class HelpWindow extends JDialog{
    private JPanel mainPanel;
    Browser browser;

    /**
     *
     */
    public HelpWindow() {
        setTitle("Help");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Images/icon32.png"));
        setIconImage(icon);

        JButton backButton = new JButton("← Back");
        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(Color.WHITE);
        menu.add(backButton, BorderLayout.WEST);

        browser = new Browser();
        BrowserView browserView = new BrowserView(browser);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(menu, BorderLayout.NORTH);
        mainPanel.add(browserView, BorderLayout.CENTER);

        browser.loadURL("http://byte-me.github.io/Catering-System/");

        backButton.addActionListener(e -> {
            if (browser.getCurrentNavigationEntryIndex() > 1) {
                browser.goBack();
            }
        });

        pack();
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setVisible(true);
        setModal(true);
    }
}
