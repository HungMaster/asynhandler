package com.it.hungvt.asyntaskthreadhandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SeekBar sbTime;
    private TextView txtTime;
    private Button btnStartTime;
    private ImageView imgPhoto;
    private Button btnStartDownload;
    private Handler handler;

    private static final int MESSAGE_UPDATE_TIME = 100;
    private static final int MESSAGE_FINISH_TIME = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        sbTime = (SeekBar) findViewById(R.id.sb_time);
        btnStartTime = (Button) findViewById(R.id.btn_start_time);
        btnStartDownload = (Button) findViewById(R.id.btn_start_download);
        txtTime = (TextView) findViewById(R.id.txt_count_time);
        imgPhoto = (ImageView) findViewById(R.id.img_photo);
        btnStartTime.setOnClickListener(this);
        btnStartDownload.setOnClickListener(this);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case MESSAGE_UPDATE_TIME:
                        txtTime.setText(String.valueOf(msg.arg1));
                        break;

                    case MESSAGE_FINISH_TIME:
                        Toast.makeText(MainActivity.this, R.string.message_finish_time,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        startSongTime();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_time:
                startCountTime();
                break;

            case R.id.btn_start_download:
                ImageDownloader imageDownloader = new ImageDownloader();
                imageDownloader.execute("http://i37.photobucket.com/albums/e83/khikunz/songoku1zx.jpg");
                break;

            default:
                break;
        }
    }

    private void startCountTime() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=10;i++){
                    Message message = new Message();
                    message.what = MESSAGE_UPDATE_TIME;
                    message.arg1=i;
                    handler.sendMessage(message);
                    if (i==10){
                        handler.sendEmptyMessage(MESSAGE_FINISH_TIME);
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void startSongTime(){
        sbTime.setMax(30);
        SongUpdate songUpdate = new SongUpdate();
        songUpdate.execute();

    }

    class SongUpdate extends AsyncTask<Void,Integer,Void>{

        private int max;

        @Override
        protected void onPreExecute() {
            max = sbTime.getMax();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i=0;i<=max;i++){
                publishProgress(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            sbTime.setProgress(values[0]);
        }
    }

    class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                connection.disconnect();
                return  bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgPhoto.setImageBitmap(bitmap);
        }
    }
}
