package base.plugin.zhs;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintManager;


public class PrintHelper {

    public static void doPrintPdf(Context context,String filePath) {
        String jobName = "jobName";
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        base.plugin.zhs.MyPrintPdfAdapter myPrintAdapter = new base.plugin.zhs.MyPrintPdfAdapter(filePath);
        printManager.print(jobName, myPrintAdapter, null);
    }
}