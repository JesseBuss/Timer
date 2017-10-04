package com.jbuss.timerapp.activities;

import java.util.Locale;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.jbuss.timerapp.R;
import com.jbuss.timerapp.interfaces.TimerView;
import com.jbuss.timerapp.tasks.TimerTask;
import com.ohoussein.playpause.PlayPauseView;

public class MainActivity extends AppCompatActivity implements TimerView {
    private static final String EXTRA_SECONDS = "seconds";
    private static final String EXTRA_PLAYING = "playing";

    private float secondsRemaining = 0f;

    private TextView secondsRemainingTextView;
    private TimerTask timerTask;
    private PlayPauseView playPauseView;
    private EditText durationEditText;

    //region lifecycle callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        durationEditText = (EditText) findViewById(R.id.duration);
        durationEditText.addTextChangedListener(durationTextWatcher);

        secondsRemainingTextView = (TextView) findViewById(R.id.seconds_remaining);

        playPauseView = (PlayPauseView) findViewById(R.id.play_pause_view);
        playPauseView.setOnClickListener(playPauseClickListener);

        boolean isPlaying = false;
        if (savedInstanceState != null) {
            secondsRemaining = savedInstanceState.getFloat(EXTRA_SECONDS, 0f);
            isPlaying = savedInstanceState.getBoolean(EXTRA_PLAYING, false);
        }

        if (secondsRemaining != 0f && isPlaying) {
            handlePlay();
        }
        setSecondsRemaining();
    }

    private void handlePlay() {
        durationEditText.setText("");
        timerTask = new TimerTask(MainActivity.this, secondsRemaining);
        timerTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void setSecondsRemaining() {
        String formattedString = String.format(Locale.US, getString(R.string.seconds_remaining), this.secondsRemaining);
        secondsRemainingTextView.setText(formattedString);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putFloat(EXTRA_SECONDS, secondsRemaining);
        outState.putBoolean(EXTRA_PLAYING, !playPauseView.isPlay());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerTask != null) {
            timerTask.cancel(false);
            timerTask = null;
        }
    }

    //endregion

    //region interface callbacks
    @Override
    public void updateView(float secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
        setSecondsRemaining();

        if (secondsRemaining <= 0) {
            playPauseView.toggle();
        }
    }
    //endregion

    //region view listeners
    private OnClickListener playPauseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            playPauseView.toggle();

            if (playPauseView.isPlay()) {
                handlePlay();
            } else {
                timerTask.cancel(false);
            }
        }
    };

    private TextWatcher durationTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String intString = s.toString();

            if (!TextUtils.isEmpty(intString) && playPauseView.isPlay()) {
                secondsRemaining = Integer.parseInt(intString);
            }
        }
    };
    //endregion
}
