package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SeekBar seekbar;
    private TextView tvSongName;
    private Button btnLast;
    private Button btnStar;
    private Button btnStop;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        btnLast = (Button) findViewById(R.id.btnLast);
        btnStar = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnNext = (Button) findViewById(R.id.btnNext);
    }

    /**
     * 设置监听
     */
    public void initListener() {
        btnStar.setOnClickListener(this::onClick);
        btnStop.setOnClickListener(this::onClick);
        btnLast.setOnClickListener(this::onClick);
        btnNext.setOnClickListener(this::onClick);
    }


    /**
     * 处理点击事件
     */
    private void onClick(View v) {
        switch (v.getId()) {
            // 上一曲
            case R.id.btnLast:

                break;
            // 播放/暂停
            case R.id.btnStart:

                break;
            // 停止
            case R.id.btnStop:

                break;
            // 下一曲
            case R.id.btnNext:

                break;
            default:
                break;
        }
    }


}