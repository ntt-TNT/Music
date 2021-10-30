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
//        String[] selectionArgs = new String[]{"%Music%"};
//        String selection = MediaStore.Audio.Media.DATA + " like ? ";
//        // 媒体库查询语句（写一个工具类MusicUtils）
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
//                selectionArgs, MediaStore.Audio.AudioColumns.IS_MUSIC
//        );
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                SongModel songModel = new SongModel();
//                songModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
//                songModel.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
//                songModel.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
//                songModel.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
//                songModel.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
//                Log.d(TAG, "getMusicData: "+songModel.getName());
//                if (songModel.getSize() > 1000 * 800) {
//                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
//                    String name = songModel.getName();
//                    if (name != null && name.contains("-")) {
//                        String[] str = name.split("-");
//                        songModel.setSinger(str[0]);
//                        songModel.setName(str[1]);
//                    }
//                    list.add(songModel);
//                }
//            }
//            // 释放资源
//            cursor.close();
//        }
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