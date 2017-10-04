package com.jbuss.timerapp.tasks;

import android.os.AsyncTask;

import com.jbuss.timerapp.interfaces.TimerView;

/**
 * Created by jbuss on 10/3/17.
 */

public class TimerTask extends AsyncTask<Void, Float, Void> {
    private TimerView timerView;
    private float totalSeconds;

    public TimerTask(TimerView timerView, float totalSeconds) {
        this.timerView = timerView;
        this.totalSeconds = totalSeconds;
    }

    @Override
    protected Void doInBackground(Void... params) {
        float remainingSeconds = totalSeconds;

        while (remainingSeconds > 0) {
            if (isCancelled()) {
                break;
            }
            try {
                int TENTH_OF_A_SECOND = 100;
                Thread.sleep(TENTH_OF_A_SECOND);
                remainingSeconds -= .1;
                remainingSeconds = Math.round(remainingSeconds * 10) / 10f; // avoid bad float rounding
                publishProgress(remainingSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        if (!isCancelled()) {
            timerView.updateView(values[0]);
        }
    }
}
