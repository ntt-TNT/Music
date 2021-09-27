package com.example.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MusicPlayerHelper implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    private static String TAG = "LIFE";

    private MusicPlayerHelperHandler mHandler;
    /**
     * 进度条
     */
    private SeekBar seekBar;

    /**
     * 显示播放信息
     */
    private TextView text;

    /**
     * 播放器
     */
    private MediaPlayer player;

    /**
     * 当前的播放歌曲信息
     */
    private SongModel songModel;

    public MusicPlayerHelper(SeekBar seekBar, TextView text) {
        mHandler = new MusicPlayerHelperHandler(this);
        player = new MediaPlayer();
        // 设置媒体流类型
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnBufferingUpdateListener(this);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        this.seekBar = seekBar;
        this.text = text;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }



    /**
     * 播放
     *
     * @param songModel    播放源
     * @param isRestPlayer true 切换歌曲 false 不切换
     */
    public void playBySongModel(@NonNull SongModel songModel, @NonNull Boolean isRestPlayer) {
        this.songModel = songModel;
        Log.e(TAG, "playBySongModel Url: " + songModel.getPath());
        if (isRestPlayer) {
            //重置多媒体
            player.reset();
            // 设置数据源
            if (!TextUtils.isEmpty(songModel.getPath())) {
                try {
                    player.setDataSource(songModel.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 准备自动播放 同步加载，阻塞 UI 线程
            // player.prepare()
            // 建议使用异步加载方式，不阻塞 UI 线程
            player.prepareAsync();
        } else {
            player.start();
        }

    }

    /**
     * 暂停
     */
    public void pause() {
        Log.e(TAG, "pause");
        if (player.isPlaying()) {
            player.pause();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        Log.e(TAG, "stop");
        player.stop();
        text.setText("停止播放");
    }


    static class MusicPlayerHelperHandler extends Handler {

        WeakReference<MusicPlayerHelper> weakReference;

        public MusicPlayerHelperHandler(MusicPlayerHelper helper) {
            super(Looper.getMainLooper());
            this.weakReference = new WeakReference<>(helper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    }
}