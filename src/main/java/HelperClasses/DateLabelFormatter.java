package HelperClasses;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by olekristianaune on 14.04.2016.
 */
public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    /**
     * Parse text string with format yyyy-MM-dd to Date object
     *
     * @param text              Input date text string
     * @return                  Date object
     * @throws ParseException
     */
    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    /**
     * Format Calendar object to text string (yyyy-MM-dd)
     *
     * @param value             Calendar object
     * @return                  Text string of type yyyy-MM-dd
     * @throws ParseException
     */
    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }

}
