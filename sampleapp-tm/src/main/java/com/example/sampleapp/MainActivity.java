package com.example.sampleapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.memo.CastContext;
import com.memo.CastMediaInfo;
import com.memo.CastMediaInfoConstract;
import com.memo.sdk.MemoTVCastSDK;
import com.memo.uiwidget.CastButton;
import com.memo.uiwidget.MiniController;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    public ExecutorService mThreadPool = Executors.newFixedThreadPool(5);
    android.support.v4.app.FragmentManager mFragmentManager;
    CastMediaInfo mCastMediaInfo;
    CastButton mCastButton;
    public String[] mUrls = new String[]{
            "http://download.memohi.com/video/hello.mp4",
                "http://download.memohi.com/video/pig.mp4",
                "http://download.memohi.com/video/Sport.mp4",
                "http://download.memohi.com/video/xiaobieli.mp4",
                "http://download.memohi.com/video/X-MAN.mp4",
        };
        public CastMediaInfo getRamdomOne(){
            int randomIndex = new Random().nextInt(5);
            final String playUrl = mUrls[randomIndex];
            final String rawUrl = playUrl;
            final String videoName= "MemoHi-Test-"+randomIndex;
            final String cover = "http://your-cover-image-url";
            final String avater = "http://your-avater-image-url";
            final String author= "Tester";
            CastMediaInfo castMediaInfo = new CastMediaInfo.Builder()
                    .setPlayUrl(playUrl)
                    .setVideoName(videoName)
                    .setRawUrl(rawUrl)
                    .setVideoCover(cover)
                    .setVideoAuthorName(author)
                    .setVideoAvater(avater)
                    .build();
            return castMediaInfo;
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        mFragmentManager = getSupportFragmentManager();
        CastContext.getSharedInstance(this).checkPermission(this);
        CastContext.getSharedInstance(this).checkLocationSwitch(this);
        findViewById(R.id.btn_scan).setOnClickListener(this);
        findViewById(R.id.btn_device).setOnClickListener(this);
        mCastButton = new CastButton();
        mCastButton.prepare(this, this);
        MiniController.getInstance().onCreate(MainActivity.this);
        CastContext.getSharedInstance(this).checkPermission(this);
        CastContext.getSharedInstance(this).checkLocationSwitch(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return mCastButton.onShowMenu(this,menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }
    @Override
    public void onClick(View v) {
       if(R.id.tvcast_btn==v.getId()){
            onClickCastButton();
       }
    }

    public void onClickCastButton(){
        final String playUrl = "http://download.memohi.com/test/live-2.mp4";
        final String rawUrl = "http://your-raw-url";
        final String videoName= "your video-name";
        final String cover = "http://your-cover-image-url";
        final String avater = "http://your-avater-image-url";
        final String author= "video-author";
        final CastMediaInfoConstract.CastMediaInfoCallback callback = new CastMediaInfoConstract.CastMediaInfoCallback(){

            @Override
            public boolean isSyncPlayingInfo() {
                //if developer do not need async request,can return true
                //If the developer needs to ask the server for a play link, return false. Execute the request, please handle in getASyncCastMediaInfo method
                return true;
            }

            @Override
            public CastMediaInfo getSyncCastMediaInfo() {
//                if(mCastMediaInfo!=null)
//                {
//                    return mCastMediaInfo;
//                }else {
//                    mCastMediaInfo = new CastMediaInfo.Builder()
//                            .setPlayUrl(playUrl)
//                            .setVideoName(videoName)
//                            .setRawUrl(rawUrl)
//                            .setVideoCover(cover)
//                            .setVideoAuthorName(author)
//                            .setVideoAvater(avater)
//                            .build();
//                }
            	  mCastMediaInfo = getRamdomOne();
                return mCastMediaInfo;
            }

            @Override
            public void getASyncCastMediaInfo(final CastMediaInfoConstract.AyncMediaInfoCallback callback) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://host/get - remote-playing-url";
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        Call call = okHttpClient.newCall(request);
                        try {
                            Response response = call.execute();
                            String responseString = response.body().string();
                            JSONObject json = new JSONObject(responseString);
                            String playingUrl = json.optString("playingUrl");
                            String videoName = json.optString("videoName");
                            String rawUrl =json.optString("rawUrl");
                            String avater =json.optString("avater");
                            String cover =json.optString("cover");
                            mCastMediaInfo = new CastMediaInfo.Builder()
                                    .setPlayUrl(playingUrl)
                                    .setVideoName(videoName)
                                    .setRawUrl(rawUrl)
                                    .setVideoCover(cover)
                                    .setVideoAuthorName(author)
                                    .setVideoAvater(avater)
                                    .build();
                            callback.onResponseCastMediaInfo(mCastMediaInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };

        mCastButton.onClickCastButton(MainActivity.this,callback);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCastButton.onActivityDestroy();
        MemoTVCastSDK.exit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.media_route_menu_item){
            onClickCastButton();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        View mToolbarTb = findViewById(R.id.tb_toolbar);
        if (mToolbarTb != null) {
            TextView labelTitle = (TextView) mToolbarTb.findViewById(R.id.tv_title_toolbar);
        }
    }
}
