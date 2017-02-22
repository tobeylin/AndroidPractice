package sample.app.tobeylin.androidpractice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sample.app.tobeylin.androidpractice.asynclistutil.AsyncListUtilActivity;
import sample.app.tobeylin.androidpractice.handleractivityleak.HandlerLeakActivity;
import sample.app.tobeylin.androidpractice.viewpager.ViewPagerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button asyncListUtilSampleButton = (Button) findViewById(R.id.main_asyncListUtilButton);
        asyncListUtilSampleButton.setOnClickListener(this);

        Button handlerActivityLeakButton = (Button) findViewById(R.id.main_handlerActivityLeakButton);
        handlerActivityLeakButton.setOnClickListener(this);

        Button viewPagerActivityButton = (Button) findViewById(R.id.main_viewPagerActivityButton);
        viewPagerActivityButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_asyncListUtilButton:
                startActivity(new Intent().setClass(getApplicationContext(), AsyncListUtilActivity.class));
                break;
            case R.id.main_handlerActivityLeakButton:
                startActivity(new Intent().setClass(getApplicationContext(), HandlerLeakActivity.class));
                break;
            case R.id.main_viewPagerActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), ViewPagerActivity.class));
                break;
            default:
        }
    }
}
