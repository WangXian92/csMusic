package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.csmusic.R;

import java.util.List;

import domian.Music;

public class ArtistsAdapter extends BaseAdapter {
    private List<Music> listMusic;
    private Context context;

    public ArtistsAdapter(Context context, List<Music> listMusic) {//构造函数
        this.context = context;
        this.listMusic = listMusic;
    }

    public void setListItem(List<Music> listMusic) {
        this.listMusic = listMusic;
    }

    public int getCount() {
        return this.listMusic.size();
    }

    public Object getItem(int arg0) {
        return this.listMusic.get(arg0);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.music_item, null);
        }

        Music m = (Music)this.listMusic.get(position);
        TextView textMusicName = (TextView)convertView.findViewById(R.id.music_item_name);
        textMusicName.setText(m.getSinger());
        TextView textMusicSinger = (TextView)convertView.findViewById(R.id.music_item_singer);
        textMusicSinger.setText(m.getAlbum());
        TextView textMusicTime = (TextView)convertView.findViewById(R.id.music_item_time);
        textMusicTime.setText(this.toTime((int)m.getTime()));

        return convertView;
    }

    public String toTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;

        return String.format("%02d:%02d", minute, second);

    }
}