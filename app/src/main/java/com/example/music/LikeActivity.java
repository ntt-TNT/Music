package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LikeActivity extends AppCompatActivity {
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
    private RadioButton check_circle;
    private RadioButton check_random;
    private RadioButton check_repeat;
    private WebView web;
    private String status = "check_circle";;
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
        setContentView(R.layout.activity_like);

        player = new MediaPlayer();
        //            songsList = ScanMusicUtils.getMusicData(this);
        songsList = LikeMusicUtils.getObject(this,new TypeToken<List<SongModel>>(){}.getType());

//        initData();
        initView();
        initListener();

        mRecyclerView = findViewById(R.id.mRecyclerView);
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
        check_circle = (RadioButton) findViewById(R.id.check_circle);
        check_random = (RadioButton) findViewById(R.id.check_random);
        check_repeat = (RadioButton) findViewById(R.id.check_repeat);

        helper = new MusicPlayerHelper(seekbar,tvSongName,final_position,current_position);
        helper.setOnCompletionListener(mp -> {next();});
        MusicAdapter adapter = new MusicAdapter(songsList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnClickListener(new MusicAdapter.OnClickListener() {
            @Override
            public void onClick(SongModel songModel, int position) {
                mPosition = position;
                Log.d(TAG, "播放："+mPosition);
                //播放歌曲
                play(songsList.get(mPosition), true);
            }

        });

    }

    /**
     * 设置监听
     */
    public void initListener() {
        btnStart.setOnClickListener(this::onClick);
        btnStop.setOnClickListener(this::onClick);
        btnLast.setOnClickListener(this::onClick);
        btnNext.setOnClickListener(this::onClick);

        check_circle.setOnCheckedChangeListener(this::onCheckedChanged);
        check_random.setOnCheckedChangeListener(this::onCheckedChanged);
        check_repeat.setOnCheckedChangeListener(this::onCheckedChanged);
    }


    public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
        switch (checkBox.getId()){
            case R.id.check_circle:
                if(checked) {
                    status = "check_circle";
                }
                break;
            case R.id.check_random:
                if (checked){
                    status = "check_random";
                }
                break;
            case R.id.check_repeat:
                if (checked){
                    status = "check_repeat";
                }
                break;
        }

    }


    public void onDeleteClick(View view){
        stop();
        for (int i=0; i<songsList.size();i++){
            if (songsList.get(i).getName().equals(songsList.get(mPosition).getName())){
                songsList.remove(i);
            }
        }
        LikeMusicUtils.putObject(this,songsList,new TypeToken<List<SongModel>>(){}.getType());
        Log.d(TAG, "onDeleteClick: "+songsList.size());
        initView();
    }

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
            Toast.makeText(LikeActivity.this,"当前的播放地址无效",Toast.LENGTH_LONG).show();
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
        if (status == "check_circle"){
            mPosition++;
        }else if (status == "check_random"){
            mPosition = (int) (Math.random()*songsList.size());
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    //当OptionsMenu被选中的时候处理具体的响应事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_1:
                Intent intent = new Intent(LikeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_2:
                return true;
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

}