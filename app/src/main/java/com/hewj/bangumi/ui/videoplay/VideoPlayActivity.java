package com.hewj.bangumi.ui.videoplay;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.hewj.bangumi.R;

/**
 * Created by hewj on 2017/5/12.
 */

public class VideoPlayActivity extends AppCompatActivity {
    private VideoView videoView ;

    public static void launch(Context context,String url) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        //本地的视频 需要在手机SD卡根目录添加一个 fl1234.mp4 视频
        String videoUrl1 = Environment.getExternalStorageDirectory().getPath()+"/fl1234.mp4" ;

        Intent intent=getIntent();
        intent.getStringExtra("url");
        //网络视频
       // String videoUrl2 =intent.getStringExtra("url");
        String videoUrl2="http://static.hdslb.com/play.swf";
        Log.i("url",intent.getStringExtra("url"));
        Uri uri = Uri.parse( videoUrl2 );

        videoView = (VideoView)this.findViewById(R.id.videoView );

        //设置视频控制器
        videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());

        //设置视频路径
        videoView.setVideoURI(uri);

        //开始播放视频
        videoView.start();
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText( VideoPlayActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }
}
