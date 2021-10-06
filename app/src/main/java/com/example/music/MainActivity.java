package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "LIFE";

    private RecyclerView mRecyclerView;
    private SeekBar seekbar;
    private TextView tvSongName;
    private Button btnLast;
    private Button btnStart;
    private Button btnStop;
    private Button btnNext;
    private MusicPlayerHelper helper;
    private MediaPlayer player;
    private TextView final_position;
    private TextView current_position;
    /**
     * 歌曲数据源
     */
    private List<SongModel> songsList = new ArrayList<>();
    /**
     * 当前播放歌曲游标位置
     */
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = new MediaPlayer();
        try {
            songsList = ScanMusicUtils.getMusicData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        initData();
        initView();
        initListener();
    }
    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        btnLast = (Button) findViewById(R.id.btnLast);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnNext = (Button) findViewById(R.id.btnNext);
        final_position = (TextView) findViewById(R.id.main_final_position);
        current_position = (TextView) findViewById(R.id.main_current_position);

        helper = new MusicPlayerHelper(seekbar,tvSongName,final_position,current_position);
        helper.setOnCompletionListener(mp -> {next();});
    }

    /**
     * 设置监听
     */
    public void initListener() {
        btnStart.setOnClickListener(this::onClick);
        btnStop.setOnClickListener(this::onClick);
        btnLast.setOnClickListener(this::onClick);
        btnNext.setOnClickListener(this::onClick);
    }


    /**
     * 初始化数据局
     */
//    public void initData() {
//        // 请求读写权限
//        RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions.request(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ).subscribe(aBoolean -> {
//            if (!aBoolean) {
//                Toast.makeText(MainActivity.this,"缺少存储权限，将会导致部分功能无法使用",Toast.LENGTH_LONG).show();
//            } else {
////                showInitLoadView();
////                List<SongModel> musicData = ScanMusicUtils.getMusicData(mContext);
////                if (!musicData.isEmpty()) {
////                    hideNoDataView();
////                    songsList.addAll(musicData);
////                    mAdapter.refresh(songsList);
////                } else {
////                    showNoDataView();
////                }
////                hideInitLoadView();
//            }
//        });
//    }


    /**
     * 处理点击事件
     */
    private void onClick(View v) {
        switch (v.getId()) {
            // 上一曲
            case R.id.btnLast:
                last();
                break;
            // 播放/暂停
            case R.id.btnStart:
                play(songsList.get(mPosition),false);
//                if (!player.isPlaying()){
//                    run();
//                }else {
//                    pause();
//                }

                break;
            // 停止
            case R.id.btnStop:
                stop();
                break;
            // 下一曲
            case R.id.btnNext:
                next();
                break;
            default:
                break;
        }
    }


    private void run(){
        Log.d(TAG, "run: ");
        try {
            btnStart.setText(R.string.btn_pause);
            player.reset();
            AssetManager assetManager = getAssets();
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd("music/阿肆、郭采洁 - 世界上的另一个我.mp3");
            Log.d(TAG, "run: "+assetFileDescriptor.getLength());
            player.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());
            player.prepare();
            player.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 播放歌曲
     *
     * @param songModel    播放源
     * @param isRestPlayer true 切换歌曲 false 不切换
     */
    private void play(SongModel songModel, Boolean isRestPlayer) {
        if (!TextUtils.isEmpty(songModel.getPath())) {
            Log.e(TAG, String.format("当前状态：%s  是否切换歌曲：%s", helper.isPlaying(), isRestPlayer));
            // 当前若是播放，则进行暂停
            if (!isRestPlayer && helper.isPlaying()) {
                btnStart.setText(R.string.btn_start);
                pause();
            } else {
                //进行切换歌曲播放
                helper.playBySongModel(songModel, isRestPlayer,this);
                btnStart.setText(R.string.btn_pause);
                // 正在播放的列表进行更新哪一首歌曲正在播放 主要是为了更新列表里面的显示
                for (int i = 0; i < songsList.size(); i++) {
                    songsList.get(i).setPlaying(mPosition == i);
                }
//                mAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(MainActivity.this,"当前的播放地址无效",Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 上一首
     */
    private void last() {
        mPosition--;
        //如果上一曲小于0则取最后一首
        if (mPosition < 0) {
            mPosition = songsList.size() - 1;
        }
        play(songsList.get(mPosition), true);
    }

    /**
     * 下一首
     */
    private void next() {
        mPosition++;
        //如果下一曲大于歌曲数量则取第一首
        if (mPosition >= songsList.size()) {
            mPosition = 0;
        }
        play(songsList.get(mPosition), true);
    }

    /**
     * 暂停播放
     */
    private void pause() {
        btnStart.setText(R.string.btn_start);
        helper.pause();
    }

    /**
     * 停止播放
     */
    private void stop() {
        helper.stop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.destroy();
    }

}