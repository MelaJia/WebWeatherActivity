package cn.edu.gdmec.android.webweatheractivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements Runnable {
    HttpURLConnection httpURLConnection=null;
    InputStream is=null;
    Vector<String> cityName=new Vector<String>();
    Vector<String> low=new Vector<String>();
    Vector<String> icon=new Vector<String>();
    Vector<Bitmap> bitmap=new Vector<Bitmap>();
    Vector<String> summary=new Vector<String>();
    Vector<String> high=new Vector<String>();

    int weatherIndex[]=new int[20];
    String city="guangzhou";
    boolean bPress=false;
    boolean bHasData=false;

    private EditText value;
    private Button find;
    private LinearLayout body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("天气查询");

        value = (EditText) findViewById(R.id.value);
        find = (Button) findViewById(R.id.find);
        body = (LinearLayout) findViewById(R.id.body);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body.removeAllViews();//移除当前所有结果
                city=value.getText().toString();
                showDialog();

            }
        });
    }




    @Override
    public void run() {
        low.removeAllElements();
        high.removeAllElements();
        icon.removeAllElements();
        bitmap.removeAllElements();
        summary.removeAllElements();
        //获取数据
        parseData();
        //下载图片
        downImages();
        //通知Url线程显示结果
        Message message=new Message();
        message.what=1;


    }

    private void downImages() {

    }
//获取数据
    private void parseData() {
        int i=0;
        String sValues;
        String weatherUrl="http://flash.weather.com.cn/wmaps/xml/"+city+".xml";
        //表示天气情况图标的基础网络
        String weatherIcon="http://m.weather.com.cn/img/c";
        try{
            URL url=new URL(weatherUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //采用get请求

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showData();
                    break;
            }
            super.handleMessage(msg);

        }
    };
//显示结果
    private void showData() {

    }


    public void showDialog(){
            /* 等待Dialog具有屏蔽其他控件的交互能力
       * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
       * 下载等事件完成后，主动调用函数关闭该Dialog
       */
            ProgressDialog waitingDialog=
                    new ProgressDialog(MainActivity.this);
            waitingDialog.setTitle("我是一个等待Dialog");
            waitingDialog.setMessage("等待中...");
            waitingDialog.setIndeterminate(true);
            waitingDialog.setCancelable(false);
            waitingDialog.show();
        }



}
