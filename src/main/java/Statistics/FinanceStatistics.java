package Statistics;

import Database.StatisticsManagement;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Evdal on 09.04.2016.
 */
public class FinanceStatistics {
    private static StatisticsManagement stat = new StatisticsManagement();
    public static double[] findFinanceStats(String dateFromS, String dateToS){ //[0] = income, [1] = outcome, [2]= net profit
        ArrayList<double[]> incomeOutcome = stat.getFinanceInfo(dateFromS,dateToS);
        double[] out = new double[3];
        for(int i = 0; i<incomeOutcome.size();i++){
            out[0] += incomeOutcome.get(i)[0];
            out[1] += incomeOutcome.get(i)[1];
        }
        out[2] = out[0]-out[1];
        return out;
    }
}
