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

public class AlbumsAdapter extends BaseAdapter {
    private List<Music> listMusic;
    private Context context;

    public AlbumsAdapter(Context context, List<Music> listMusic) {//构造函数
        this.context = context;
        this.listMusic = listMusic;
    }

    public void setListItem(List<Music> listMusic) {//自定义函数
        this.listMusic = listMusic;
    }

    public int getCount() {//*继承自BaseAdapter的重写函数* 数据域的大小
        return this.listMusic.size();
    }

    public Object getItem(int arg0) {//*继承自BaseAdapter的重写函数* 返回每个item的数据
        return this.listMusic.get(arg0);
    }

    public long getItemId(int position) {//*继承自BaseAdapter的重写函数* 返回每个item的id
        return (long)position;
    }
    //*继承自BaseAdapter的重写函数*
    /*
        最重要的是：重绘视图，调用次数由getCount方法确定的，最后将视图返回，
        注意：有些控件必须设置成没有获利焦点与点击
        这几个方法写的内容不变，具有参考价值。

        2.重写数据区使用ArrayList实现list。
        先封装成一个数据类，再将数据类装进list集合中

        绑定数据区域与视图区域
        setlistadapter(Myadapter)Myadapter为自定义类型
    * */
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * position就是位置从0开始，convertView是spinner，listview中每一项要显示的convertView就是父窗体
         * 了，也就是spinner，listview.通常返回return的view也就是convertview
         * */
        if (convertView == null) {
            //加载控件布局
            convertView = LayoutInflater.from(this.context).inflate(R.layout.music_item, null);
        }
        //加载数据
        Music m = (Music)this.listMusic.get(position);
        //加载文本控件
        TextView textMusicName = (TextView)convertView.findViewById(R.id.music_item_name);

        textMusicName.setText(m.getSinger());//加入文字（歌曲）

        TextView textMusicSinger = (TextView)convertView.findViewById(R.id.music_item_singer);

        textMusicSinger.setText(m.getName());//加入文字（歌手）

        TextView textMusicTime = (TextView)convertView.findViewById(R.id.music_item_time);
        textMusicTime.setText(this.toTime((int)m.getTime()));

        return convertView;
    }

    //时间函数
    public String toTime(int time) {//传入进来的数据

        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;

        return String.format("%02d:%02d", minute, second);
    }

}