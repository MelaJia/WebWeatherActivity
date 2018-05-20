package cn.edu.gdmec.android.webweatheractivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.sax.Element;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
               /// showDialog();
                Toast.makeText(MainActivity.this, "正在进行天气查询，请稍后。。。", Toast.LENGTH_SHORT).show();
                Thread th=new Thread(MainActivity.this);
                th.start();

            }
        });
    }




    @Override
    public void run() {
        cityName.removeAllElements();
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
        handler.sendMessage(message);


    }
//下载图片
    private void downImages() {
        //获取天气情况图标
        int i=0;
        for (i=0;i<icon.size();i++){
            try{
                URL url=new URL(icon.elementAt(i));
                httpURLConnection= (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("GET");
                is=httpURLConnection.getInputStream();
                bitmap.addElement(BitmapFactory.decodeStream(httpURLConnection.getInputStream()));
             //   bitmap.addElement(BitmapFactory.decodeStream(is));




            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //释放链接
                try{
                   is.close();
                   httpURLConnection.disconnect();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


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
            httpURLConnection.setRequestMethod("GET");
            is=httpURLConnection.getInputStream();
            InputStreamReader in=new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader br=new BufferedReader(in);
           // BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String inputLine="";
            String resultData="";
            //用循环来读取获得的天气数据
            while ((inputLine=br.readLine())!=null){
                resultData+=inputLine+"\n";
                 Log.i("info", resultData);

            }
//            获得XmlPullParser解析器
            XmlPullParser xmlPullParser= Xml.newPullParser();
            ByteArrayInputStream tInputStream=null;
            tInputStream=new ByteArrayInputStream(resultData.getBytes());
            xmlPullParser.setInput(tInputStream,"UTF-8");
            //或得到解析的事件类别
            int evtType=xmlPullParser.getEventType();
           //不能使用 if ()     //一直循环知道文档结束
            while (evtType!=XmlPullParser.END_DOCUMENT)
            {
               Log.i("evtType", String.valueOf(evtType));
                switch (evtType){
                    case XmlPullParser.START_TAG:
                        String tag=xmlPullParser.getName();
                        Log.i("tag1", tag);
                        //如果从city开始，这说明有一条新的城市信息
                        if (tag.equalsIgnoreCase("city")){
                            cityName.addElement(xmlPullParser.getAttributeValue(null,"cityname"));
                            Log.i("cityName", String.valueOf(cityName));
                            //天气情况概述
                            summary.addElement(""+xmlPullParser.getAttributeValue(null,"stateDetailed"));
                            //最低温度
                            low.addElement("最低"+xmlPullParser.getAttributeValue(null,"tem2"));
                            //最高温度
                            high.addElement("最高"+xmlPullParser.getAttributeValue(null,"tem1"));
                            //天气情况图标网址
                            icon.addElement(weatherIcon+xmlPullParser.getAttributeValue(null,"state1")+".gif");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //标签结束
                    default:break;
                }
                //如果标签没有结束，移到下一个点
                evtType=xmlPullParser.next();
                //System.out.print(evtType);
              //  Log.i("jj", String.valueOf(evtType));

            }



        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
    }
    private final Handler handler=new Handler(){
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
    public void showData() {
        body.removeAllViews();//清除存储原有的查询结果的组件
        body.setOrientation(LinearLayout.VERTICAL);
       LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight=80;
        params.height=50;
        for (int i=0;i<cityName.size();i++){
            Log.i("cittyname", String.valueOf(cityName));
            LinearLayout linearLayout=new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            //城市
            TextView cityView=new TextView(this);
            cityView.setLayoutParams(params);
            cityView.setText(cityName.elementAt(i));
            linearLayout.addView(cityView);
            //天气描述
            TextView summaryView=new TextView(this);
            summaryView.setLayoutParams(params);
            summaryView.setText(summary.elementAt(i));
            linearLayout.addView(summaryView);
            //图标
            ImageView iconView=new ImageView(this);
            iconView.setLayoutParams(params);
            iconView.setImageBitmap(bitmap.elementAt(i));
            linearLayout.addView(iconView);
            //最低温度
            TextView lowView=new TextView(this);
            lowView.setLayoutParams(params);
            lowView.setText(low.elementAt(i));
            linearLayout.addView(lowView);
            //最高温度
            TextView highView=new TextView(this);
            highView.setLayoutParams(params);
            highView.setText(high.elementAt(i));
            linearLayout.addView(highView);
//body为布局参数中用于显示天气预报顶层LinearLayout
            body.addView(linearLayout);


        }



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
