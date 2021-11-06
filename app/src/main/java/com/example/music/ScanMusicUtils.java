package com.example.music;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanMusicUtils {
    private static String TAG = "LIFE";

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<SongModel> getMusicData(Context context) throws IOException {
        List<SongModel> list = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        String[] selectionArgs = assetManager.list("music");

        for (int i=0 ;i<selectionArgs.length; i++){
            String name = selectionArgs[i];
            SongModel songModel = new SongModel();
            songModel.setPath(name);
            if (name != null && name.contains("-")) {
                String[] str = name.split("-");
                songModel.setSinger(str[1]);
                songModel.setName(str[0]);
            }
            list.add(songModel);
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return (time / 1000 / 60) + ":0" + time / 1000 % 60;
        } else {
            return (time / 1000 / 60) + ":" + time / 1000 % 60;
        }
    }
}