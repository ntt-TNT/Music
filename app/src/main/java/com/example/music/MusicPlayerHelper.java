package com.example.music;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    public static String TAG = "LIFE";
    private static int MSG_CODE = 0x01;
    private static long MSG_TIME = 1_000L;

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

    private TextView final_position;
    private TextView current_position;


    private OnCompletionListener mOnCompletionListener;

    public MusicPlayerHelper(SeekBar seekBar, TextView text, TextView final_position, TextView current_position) {
        mHandler = new MusicPlayerHelperHandler(this);
        player = new MediaPlayer();
        // 设置媒体流类型
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnBufferingUpdateListener(this);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        this.seekBar = seekBar;
        this.seekBar.setOnSeekBarChangeListener(this);
        this.text = text;
        this.final_position = final_position;
        this.current_position = current_position;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
        int currentProgress =
                seekBar.getMax() * player.getCurrentPosition() / player.getDuration();
        Log.e(TAG, currentProgress + "% play --> " + percent + "% buffer");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion");
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(TAG, "onPrepared");
        mp.start();
    }


    public void setOnCompletionListener(@NonNull OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when playback of
     * a media source has completed.
     */
    interface OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        void onCompletion(MediaPlayer mp);
    }


    /**
     * 播放
     *
     * @param songModel    播放源
     * @param isRestPlayer true 切换歌曲 false 不切换
     */
    public void playBySongModel(@NonNull SongModel songModel, @NonNull Boolean isRestPlayer,Context context) {
        this.songModel = songModel;
        Log.e(TAG, "playBySongModel Url: " + songModel.getPath());
        if (isRestPlayer) {
            //重置多媒体
            player.reset();
            // 设置数据源
            if (!TextUtils.isEmpty(songModel.getPath())) {
                try {
                    AssetManager assetManager = context.getAssets();
                    AssetFileDescriptor assetFileDescriptor = assetManager.openFd("music/"+songModel.getPath());
                    Log.d(TAG, "正在播放"+songModel.getName());
                    player.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());
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
        //发送更新命令
        mHandler.sendEmptyMessage(MSG_CODE);
    }

    /**
     * 暂停
     */
    public void pause() {
        Log.e(TAG, "pause");
        if (player.isPlaying()) {
            player.pause();
        }
        //移除更新命令
        mHandler.removeMessages(MSG_CODE);
    }

    /**
     * 停止
     */
    public void stop() {
        Log.e(TAG, "stop");
        player.stop();
        seekBar.setProgress(0);
        text.setText("停止播放");
        final_position.setText("0:00");
        current_position.setText("0:00");

        //移除更新命令
        mHandler.removeMessages(MSG_CODE);
    }

    /**
     * 是否正在播放
     */
    public Boolean isPlaying() {
        return player.isPlaying();
    }

    /**
     * 消亡 必须在 Activity 或者 Frament onDestroy() 调用 以防止内存泄露
     */
    public void destroy() {
        // 释放掉播放器
        player.release();
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 用于监听SeekBar进度值的改变
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * 用于监听SeekBar开始拖动
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeMessages(MSG_CODE);
    }

    /**
     * 用于监听SeekBar停止拖动  SeekBar停止拖动后的事件
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        Log.i(TAG, "onStopTrackingTouch " + progress);
        // 得到该首歌曲最长秒数
        int musicMax = player.getDuration();
        // SeekBar最大值
        int seekBarMax = seekBar.getMax();
        //计算相对当前播放器歌曲的应播放时间
        float msec = progress / (seekBarMax * 1.0F) * musicMax;
        // 跳到该曲该秒
        player.seekTo((int) msec);
        mHandler.sendEmptyMessageDelayed(MSG_CODE, MSG_TIME);
    }

    private String getCurrentPlayingName() {
        String info = String.format("正在播放:  %s\t\t", songModel.getName());
        return info;
    }

    private String getCurrentPlayingFinalTime(int maxTime) {
        return String.format("%s", ScanMusicUtils.formatTime(maxTime));
    }

    private String getCurrentPlayingCurrentTime(int currentTime) {
        return String.format("%s", ScanMusicUtils.formatTime(currentTime));
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
            if (msg.what == MSG_CODE) {
                int pos = 0;
                //如果播放且进度条未被按压
                if (weakReference.get().player.isPlaying() && !weakReference.get().seekBar.isPressed()) {
                    int position = weakReference.get().player.getCurrentPosition();
                    int duration = weakReference.get().player.getDuration();
                    if (duration > 0) {
                        // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                        pos = (int) (weakReference.get().seekBar.getMax() * position / (duration * 1.0f));
                    }
                    weakReference.get().current_position.setText(weakReference.get().getCurrentPlayingFinalTime(position));
                    weakReference.get().final_position.setText(weakReference.get().getCurrentPlayingFinalTime(duration));
                    weakReference.get().text.setText(weakReference.get().getCurrentPlayingName());
                }
                weakReference.get().seekBar.setProgress(pos);
                sendEmptyMessageDelayed(MSG_CODE, MSG_TIME);
            }
        }
    }
}