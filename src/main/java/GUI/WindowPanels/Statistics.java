package GUI.WindowPanels;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by olekristianaune on 13.03.2016.
 */
public class Statistics {

    public Statistics(final JFormattedTextField fromDate, final JFormattedTextField toDate, JButton getStatisticsButton) {

        try {
            final MaskFormatter maskFormatter1 = new MaskFormatter("####-##-##"); // Defining format pattern
            final MaskFormatter maskFormatter2 = new MaskFormatter("####-##-##"); // Defining format pattern

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Setup date format
            Date currDate = new Date(); // Get current date
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            Date lastWeekDate = new Date(cal.getTimeInMillis());

            maskFormatter1.setPlaceholder(dateFormat.format(lastWeekDate)); // Placeholder
            maskFormatter2.setPlaceholder(dateFormat.format(currDate)); // Placeholder

            fromDate.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter1;
                }
            });

            toDate.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() { // Add format to field
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return maskFormatter2;
                }
            });

        } catch(Exception e) {
            System.err.println(e);
        }


        getStatisticsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fDate = fromDate.getText();
                String tDate = toDate.getText();

                System.out.println("From: " + fDate + " To: " + tDate);
            }
        });


    }

}
