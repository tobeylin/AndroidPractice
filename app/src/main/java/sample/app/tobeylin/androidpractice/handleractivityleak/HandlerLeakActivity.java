package sample.app.tobeylin.androidpractice.handleractivityleak;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import sample.app.tobeylin.androidpractice.R;

public class HandlerLeakActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HandlerLeakActivity.class.getSimpleName();

    private TextView countTextView;
    private LeakHandler leakHandler = new LeakHandler();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_leak);

        countTextView = findViewById(R.id.handlerLeakActivity_countText);
        countTextView.setText(R.string.handler_leak_title);
        Button plusButton = findViewById(R.id.handlerLeakActivity_plusButton);
        plusButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countTextView = null;
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onClick(View v) {
        count = count + 1;
        countTextView.setText(R.string.handler_leak_calculating);
        leakHandler.sendEmptyMessage(LeakHandler.LONG_TASK);
        finish();
    }

    @SuppressLint("HandlerLeak")
    private class LeakHandler extends Handler {

        static final int LONG_TASK = 1;
        static final int UPDATE_UI = 2;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LONG_TASK:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10 * 60 * 1000);
                                sendEmptyMessage(UPDATE_UI);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case UPDATE_UI:
                    countTextView.setText(getResources().getString(R.string.handler_leak_count_text, count));
                    break;
            }
        }
    }

}
