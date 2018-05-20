package cn.edu.gdmec.android.webweatheractivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.VersionedPackage;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity  {

    private ProgressBar pb;
    private TextView tvSplashVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pb = (ProgressBar) findViewById(R.id.pb);
        tvSplashVersion = (TextView) findViewById(R.id.tv_version);

        try {
            PackageInfo packageInfo= getPackageManager().getPackageInfo(getPackageName(),0);
            tvSplashVersion.setText("版本号：" + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tvSplashVersion.setText("V");
        }
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        };
        timer.schedule(timerTask,3000);
    }

}
