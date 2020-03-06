package util;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import domian.Music;
//提取歌曲
public class MusicList {

    public static List<Music> getMusicData(Context context){
            List<Music> musicList = new ArrayList<Music>();

        ContentResolver cx = context.getContentResolver();

        if (cx != null) {

            Cursor gqcx = cx.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            if (null == gqcx) {
                return null;
            }
            if (gqcx.moveToFirst()) {

                do {
                    Music m = new Music();

                    String title = gqcx
                            .getString(gqcx.getColumnIndex(MediaStore.Audio.Media.TITLE));

                    String singer = gqcx
                            .getString(gqcx.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                    if ("<unknown>".equals(singer)) {//对比是否有歌手
                        singer = "未知艺术家";
                    }

                    String album = gqcx
                            .getString(gqcx.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                    long size = gqcx.getLong(gqcx.getColumnIndex(MediaStore.Audio.Media.SIZE));

                    long time = gqcx.getLong(gqcx.getColumnIndex(MediaStore.Audio.Media.DURATION));

                    String url = gqcx.getString(gqcx.getColumnIndex(MediaStore.Audio.Media.DATA));

                    String name = gqcx.getString(gqcx.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                    String sbr = name.substring(name.length() - 3, name.length());

                    if (sbr.equals("mp3")) {//如果是mp3格式   传入所有的信息到music
                        m.setTitle(title);
                        m.setSinger(singer);
                        m.setAlbum(album);
                        m.setSize(size);
                        m.setTime(time);
                        m.setUrl(url);
                        m.setName(name);
                        musicList.add(m);//传入自定义list
                    }
                }
                while (gqcx.moveToNext());
            }
        }
        return musicList;
    }
}
