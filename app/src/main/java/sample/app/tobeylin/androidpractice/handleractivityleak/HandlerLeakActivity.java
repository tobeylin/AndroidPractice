package sample.app.tobeylin.androidpractice.handleractivityleak;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import sample.app.tobeylin.androidpractice.R;

public class HandlerLeakActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HandlerLeakActivity.class.getSimpleName();

    private TextView countTextView;
    private Button plusButton;
    private Handler handler = new Handler();
    private LeakHandler leakHandler = new LeakHandler();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_leak);

        countTextView = (TextView) findViewById(R.id.handlerLeakActivity_countText);
        plusButton = (Button) findViewById(R.id.handlerLeakActivity_plusButton);
        plusButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countTextView = null;
        Log.i(TAG, "onDestroy");
    }

    public void updateText(){
        countTextView.setText("updateText...");
        Log.i(TAG, "updateText");
    }

    @Override
    public void onClick(View v) {
        count = count + 1;
        countTextView.setText("calculating...");
        leakHandler.sendEmptyMessage(LeakHandler.LONG_TASK);
        //handler.postDelayed(leakShowTextRunnable, 10 * 60 * 1000);
        finish();
    }

    private Runnable leakShowTextRunnable = new Runnable() {
        @Override
        public void run() {
            countTextView.setText("count = " + count);
        }
    };

    private static class NotLeakShowTextRunnable implements Runnable {

        private WeakReference<HandlerLeakActivity> activityRef;

        public NotLeakShowTextRunnable(HandlerLeakActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            activityRef.get().countTextView.setText("count = " + activityRef.get().count);
        }

    }

    private class LeakHandler extends Handler {

        public static final int LONG_TASK = 1;
        public static final int UPDATE_UI = 2;

        private boolean isCancelled = false;

        public void cancel() {
            isCancelled = true;
            leakHandler = null;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (isCancelled) {
                return;
            }

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
                    countTextView.setText("count = " + count);
                    break;
            }
        }
    }

}
