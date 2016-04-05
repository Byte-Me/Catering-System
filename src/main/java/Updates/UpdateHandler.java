package Updates;

import javax.swing.*;

/**
 * Created by olekristianaune on 05.04.2016.
 */
public class UpdateHandler {
    private static Timer timer;
    private static int currTab;
    private static boolean autoUpdateStarted = false;

    private static void startTimer() {
        timer.start();
    }

    private static void restartTimer() {
        timer.restart();
    }

    // Used for updates from the GUI
    public static void updateTab(int tabIndex) {
        currTab = tabIndex;
        updateTab();
    }

    private static void updateTab() {
        switch (currTab) {
            case 0:
                // Some function
                break;
            case 1:
                // Some function
                break;
            case 2:
                // Some function
                break;
            case 3:
                // Some function
                break;
            case 4:
                // Some function
                break;
            case 5:
                // Some function
                break;
            case 6:
                // Some function
                break;
            default:
                // Something wrong??
        }
    }

    public static void startAutoUpdate(int tabIndex) {
        if(!autoUpdateStarted) {
            currTab = tabIndex;
            timer = new Timer(60, e -> {
                updateTab();
                restartTimer();
            });
            startTimer();
        }
    }

}
