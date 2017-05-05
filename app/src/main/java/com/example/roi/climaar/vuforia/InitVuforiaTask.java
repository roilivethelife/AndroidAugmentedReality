package com.example.roi.climaar.vuforia;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.vuforia.Vuforia;

/**
 * Created by roi on 21/11/16.
 */
// An async task to initialize Vuforia asynchronously.
public class InitVuforiaTask extends AsyncTask<Void, Integer, Boolean>
{
    // Initialize with invalid value:
    private int mProgressValue = -1;
    private Activity mActivity;
    private int mVuforiaFlags;

    public  InitVuforiaTask(Activity mActivity,int mVuforiaFlags){
        this.mActivity=mActivity;
        this.mVuforiaFlags=mVuforiaFlags;
    }

    protected Boolean doInBackground(Void... params)
    {
        // Prevent the onDestroy() method to overlap with initialization:
            Vuforia.setInitParameters(mActivity, mVuforiaFlags, "AWpyQvb/////AAAAGZx5ZAoRvk6msRcJbt6TCoh4ZH7rhIazxFYCsaoEBwNs9ZZxKeK5+QPR3HTN3Bq3wr1VwQWjMW+75kbrMEG4Jwzu7EAjn4vVw9MZZKuJMn0CThCtV6EeYwRXfsoB8E+vILrv6885BpcZ9ytCaOjZYsKBYS370c739mpOmCjMP8ksBarHN52ZwFjLYW/K6VOHCbSNrHzEOy0AAtPT3RDMbC1crImFUlCIIPCqalThOP9t1llOZlR5WUddD9Ee4A18pV+Pytz24Qtkkpz+eBrVmbA6yzhjDW+O5TpNJID9wUnuvjVvL9ysROBIwofPb6yyyNye/gHlkLSKvR9rabm2bzNf2xS5OPk2ua/3sdoOkDw1");

            do
            {
                // Vuforia.init() blocks until an initialization step is
                // complete, then it proceeds to the next step and reports
                // progress in percents (0 ... 100%).
                // If Vuforia.init() returns -1, it indicates an error.
                // Initialization is done when progress has reached 100%.
                mProgressValue = Vuforia.init();

                // Publish the progress value:
                publishProgress(mProgressValue);

                // We check whether the task has been canceled in the
                // meantime (by calling AsyncTask.cancel(true)).
                // and bail out if it has, thus stopping this thread.
                // This is necessary as the AsyncTask will run to completion
                // regardless of the status of the component that
                // started is.
            } while (!isCancelled() && mProgressValue >= 0
                    && mProgressValue < 100);

            return (mProgressValue > 0);
    }


    protected void onProgressUpdate(Integer... values)
    {
        // Do something with the progress value "values[0]", e.g. update
        // splash screen, progress bar, etc.
    }


    protected void onPostExecute(Boolean result)
    {
        // Done initializing Vuforia, proceed to next application
        // initialization status:


        if (result)
        {
            Log.d("INIT_VuforiaTask", "InitVuforiaTask.onPostExecute: Vuforia "
                    + "initialization successful");
        }
    }
}
