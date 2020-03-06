package com.example.csmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import domian.Music;
import util.LrcView;
import util.MusicList;

import static com.example.csmusic.MusicService._id;
import static com.example.csmusic.MusicService.player;

public class MusicActivity extends Activity  {

    private  TextView textSinger;
    private  TextView textName;
    private  TextView textEndTime;

    //属性
    private static TextView textStartTime;

    private ImageButton imageBtnRewind;
    public static ImageButton imageBtnPlay;//无法强制转型
    private ImageButton imageBtnForward;




    public static LrcView lrc_view;//无法强制转型(自定义view)

    private SeekBar seekBar1;
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;

    /**

     *SeekBar是ProgressBar的扩展，它添加了一个可拖动的拇指。用户可以触摸

     *拇指向左或向右拖动可设置当前进度级别或使用箭头键。

     *不鼓励在SeekBar的左侧或右侧放置可聚焦的小部件。

     *<p>

     *SeekBar的客户端可以将{@link SeekBar.OnSeekBarChangeListener}附加到

     *通知用户的操作。有三个函数要重写

     *

     *@attr ref android.R.styleable#请看大拇指

     */

    private SeekBar seekBarVolume;
    private List<Music> lists;
    private Boolean isPlaying = false;
    public static int id = 1;
    private static int currentId = 2;
    private static Boolean replaying = false;
    private MyProgressBroadCastReceiver receiver;//内部类
    private MyCompletionListner completionListner;//内部类
    public static Boolean isLoop = true;

    private boolean mRegisteredSensor;

    public MusicActivity() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);

       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //加载的布局列
        setContentView(R.layout.activity_music);

        textName = (TextView)this.findViewById(R.id.music_name);//歌名

        textSinger = (TextView)this.findViewById(R.id.music_singer);//歌手

        textStartTime = (TextView)this.findViewById(R.id.music_start_time);//前置时间

        textEndTime = (TextView)this.findViewById(R.id.music_end_time);//后置时间

        seekBar1 = (SeekBar)this.findViewById(R.id.music_seekBar);//歌曲进度条 控件

        this.imageBtnRewind = (ImageButton)this.findViewById(R.id.music_rewind);//重新播放 控件

        imageBtnPlay = (ImageButton)this.findViewById(R.id.music_play);//暂停 控件

        this.imageBtnForward = (ImageButton)this.findViewById(R.id.music_foward);//下一曲

        this.seekBarVolume = (SeekBar)this.findViewById(R.id.music_volume);//音量条背景组件

        lrc_view = (LrcView)this.findViewById(R.id.LyricShow);//LyricShow控件

        //加入点击事件

       imageBtnRewind.setOnClickListener(new MyListener());
        imageBtnPlay.setOnClickListener(new MyListener());
        imageBtnForward.setOnClickListener(new MyListener());



/*获取系统服务的方法context.getSystemService()
传入的字符串就是对应服务的key值，系统服务创建之后都要添加到seviceManager中去，
getSystemService的内部实现就是使用ServiceManager通过Binder机制实现的，
同时服务创建之后也会缓存到到一个hashMap中，对应的AudioManager对应的key就是andio，
这个在SystemServiceRegistry.java中写的很清楚
 */


        //获取手机里面的歌曲信息
        this.lists = MusicList.getMusicData(this);//传入数组长度  并得到数据的引用（数据的各样信息）

        this.audioManager = (AudioManager)this.getSystemService("audio");//系统服务  音频


        this.maxVolume = this.audioManager.getStreamMaxVolume(3);////获取系统最大音量
        this.currentVolume = this.audioManager.getStreamVolume(3);//获取设备当前音量


        //进度条 属性以及赋值
        this.seekBarVolume.setMax(this.maxVolume);//加入数据  获取最大范围值
        this.seekBarVolume.setProgress(this.currentVolume);//获取当前进度值
        //监听器 以及响应事件  音量进度
        this.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {//拖动停止

            }

            public void onStartTrackingTouch(SeekBar seekBar) {//开始拖动

            }
            //当前进度
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MusicActivity.this.audioManager.setStreamVolume(3, progress, 2);//通知音量发生变化
            }

        });

        /*TelephonyManager类简介

        TelephonyManager类位于android.telephony包下，主要提供了一系列用于访问与手机通信相关的状态和信息的get方法。
        其中包括手机SIM的状态和信息、电信网络的状态及手机用户的信息。在应用程序中可以使用这些get方法获得相关数据。

        TelephonyManager类的对象可以通过Context.getSystemService(Context.TELEPHONY_SERVICE)方法来获得，
        需要注意的是有些通信信息的获取对应用程序的权限有一定的限制，在开发的时候需要为其添加相应的权限。

        * */
        //实现手机电话状态的监听，主要依靠两个类：TelephoneManger和PhoneStateListener。
        TelephonyManager telManager = (TelephonyManager)this.getSystemService("phone");// 系统服务 电话

        telManager.listen(new MobliePhoneStateListener(), 32);//首先要创建一个新的PhoneStateListener对象和获取TelephonyManager服务
        //歌曲播放进度
        this.seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //拖动停止
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicActivity.this.seekBar1.setProgress(seekBar.getProgress());//设置当前进度
                Intent intent = new Intent("cn.com.karl.seekBar");
                intent.putExtra("seekBarPosition", seekBar.getProgress());//当前进度
                // 最普通的发送intent的方式，是一种无序的广播机制，理论上，所有的接受者同时获得该intent的消息， 接受者之间不存在先后顺序， 不能截断/修改intent的数据。 应用普遍使用的就是该方式。
                MusicActivity.this.sendBroadcast(intent);//发送广播
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

        });

        this.completionListner = new MyCompletionListner();//内部类对象
        //IntentFilter翻译成中文就是“意图过滤器”，主要用来过滤隐式意图。当用户进行一项操作的时候，Android系统会根据配置的 “意图过滤器” 来寻找可以响应该操作的组件，服务。
        //
        //例如：当用户点击PDF文件的时候，Android系统就会通过设定好的意图过滤器，进行匹配测试。找到能够打开PDF文件的APP程序。
        //————————————————

        //响应cn.com.karl.completion
        IntentFilter filter = new IntentFilter("cn.com.karl.completion");
        this.registerReceiver(this.completionListner, filter);//加入意图过滤器

    }

    protected void onStart() {//onStart()通常就是onStop()(用户按下home键，activity变为后台)之后用户再切回这个activity就会调用onRestart()然后调用onStart();
        super.onStart();
        this.receiver = new MyProgressBroadCastReceiver();//内部类对象  //广播
        IntentFilter filter = new IntentFilter("cn.com.karl.progress");//意图过滤器
        this.registerReceiver(this.receiver, filter);//加入过滤器并注册广播 调用Context的registerReceiver函数注册BroadcastReceiver； 当应用程序不再需要监听广播时，则需要调用unregisterReceiver函数进行反注册
        //取出第几首歌的位置
        id = this.getIntent().getIntExtra("id", 1);//该方法中的 defaultValue 表示name对应的putExtra中没有传入有效的int类型值

        //就将defaultValue的值作为默认值传入。

        //其中name作为发送方中putExtra(String,int)中所对应的String。
        Music m;
        Intent intent;

        if (id == currentId) {//对比
            m = (Music)this.lists.get(id);//2
            this.textName.setTextColor(0xFFFFFFFF);
            this.textName.setText(m.getTitle());//设置标题
            this.textSinger.setTextColor(0xFFFFFFFF);
            this.textSinger.setText(m.getSinger());//设置歌手
            this.textEndTime.setTextColor(0xFFFFFFFF);//设置字体颜色
            this.textEndTime.setText(this.toTime((int)m.getTime()));//设置时间

            intent = new Intent(this, MusicService.class);
            intent.putExtra("play", "replaying");
            intent.putExtra("id", id);
            this.startService(intent);//启动服务MusicService.

            if (replaying) {//对比是否已经进入过场景
                imageBtnPlay.setImageResource(R.drawable.pause1);//图标 播放
                this.isPlaying = true;
            } else {
                imageBtnPlay.setImageResource(R.drawable.play1);//图标 停止
                this.isPlaying = false;
            }

        } else {

            m = (Music)this.lists.get(id);//第几位数据
            //插入数据

            this.textName.setTextColor(0xFFFFFFFF);
            this.textName.setText(m.getTitle());//设置标题
            this.textSinger.setTextColor(0xFFFFFFFF);
            this.textSinger.setText(m.getSinger());//设置歌手
            this.textEndTime.setTextColor(0xFFFFFFFF);//设置字体颜色
            this.textEndTime.setText(this.toTime((int)m.getTime()));//设置时间
            imageBtnPlay.setImageResource(R.drawable.pause1);
            // 播放 图标
            intent = new Intent(this, MusicService.class);
            intent.putExtra("play", "play");
            intent.putExtra("id", id);
            this.startService(intent);//启动服务
            this.isPlaying = true;//第一次登陆
            replaying = true;
            currentId = id;

        }

    }

    protected void onResume() {//(activity被另一个透明或者Dialog样式的activity覆盖了）之后dialog取消，activity回到可交互状态，调用onResume();
        super.onResume();
        /*
        //获取传感器列表
        List<Sensor> sensors = this.sensorManager.getSensorList(1);//数据链
        if (sensors.size() > 0) {//大于0
            //获取Sensor传感器
            Sensor sensor = (Sensor)sensors.get(0);//返回此列表中指定位置的元素。
            //SensorManager是Android手机传感器的管理器类，它是一个抽象类。 使用传感器服务添加传感器的监听器
            this.mRegisteredSensor = this.sensorManager.registerListener(this, sensor, 0);
            Log.e("--------------", sensor.getName());//返回传感器的名字
        }
*/
    }

    protected void onPause() {//(返回上个活动时调用)

        if (this.mRegisteredSensor) {//是否为true
            //this.sensorManager.unregisterListener(this);//注销传感器监听器
            this.mRegisteredSensor = false;
        }

        super.onPause();
    }

    protected void onDestroy() {//活动被销毁时调用
        this.unregisterReceiver(this.receiver);//注销监听器
        this.unregisterReceiver(this.completionListner);//注销监听器
        super.onDestroy();
    }

    //添加歌曲时间函数
    public static String toTime(int time) {
        time /= 1000;//把左边的变量除于右边变量的值赋予右边的变量，例如：a/=b等价于a=a/b
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }





//内部类
    /*
     * 类：PhoneStateListener
     * /**
     *用于监视特定电话状态中的更改的侦听器类
     *在设备上，包括服务状态、信号强度、信息
     *等待指示器（语音邮件）和其他。
     *<p>
     *重写要接收更新的状态的方法，以及
     *将PhoneStateListener对象与侦听的按位或一起传递_
     *{@link TelephonyManager\listen TelephonyManager.listen（）的标志。
     *<p>
     *请注意，访问某些电话信息是
     *权限受保护。您的应用程序将不会接收受保护的更新
     *信息，除非它具有在
     *它的清单文件。如果应用权限，则在
     *适
     *
     * */
    private class MobliePhoneStateListener extends PhoneStateListener {
        private MobliePhoneStateListener() {
        }

        //来电话时 将暂停
        //回调函数onCallStateChanged 参数一个为状态，一个是号码
        public void onCallStateChanged(int state, String incomingNumber)
        {
            switch(state) {
                case 0:
                    Intent intent = new Intent(MusicActivity.this, MusicService.class);
                    intent.putExtra("play", "playing");
                    intent.putExtra("id", MusicActivity.id);
                    MusicActivity.this.startService(intent);//启动服务

                    MusicActivity.this.isPlaying = true;
                    MusicActivity.imageBtnPlay.setImageResource(R.drawable.pause1);//加载图片
                    MusicActivity.replaying = true;
                    break;
                case 1:
                case 2:

                    Intent intent2 = new Intent(MusicActivity.this, MusicService.class);
                    intent2.putExtra("play", "pause");
                    MusicActivity.this.startService(intent2);
                    MusicActivity.this.isPlaying = false;
                    MusicActivity.imageBtnPlay.setImageResource(R.drawable.pause1);
                    MusicActivity.replaying = false;
            }

        }
    }
    //Android四大组件之一，没有可视化界面，用于不同组件和多线程之间的通信。
    /*
    * 1）新建类继承BroadcastReceiver
（2）实现onReceive抽象方法【由于BroadcastReceiver是一个抽象类，定义了一个onReceive】
（3）在AndroidManifest中注册BroadcastReceiver*/

    private class MyCompletionListner extends BroadcastReceiver {
        private MyCompletionListner() {

        }

        public void onReceive(Context context, Intent intent) {

            if(_id == MusicService.u){
                ++_id;
            }

            Music m = (Music)lists.get(_id);//返回此列表中指定位置的元素。
            MusicActivity.this.textName.setText(m.getTitle());//
            MusicActivity.this.textSinger.setText(m.getSinger());
            MusicActivity.this.textEndTime.setText(MusicActivity.this.toTime((int)m.getTime()));
            MusicActivity.imageBtnPlay.setImageResource(R.drawable.pause1);

        }
    }

    private class MyListener implements View.OnClickListener {
        private MyListener() {

        }

        //根据不同的按钮响应不同的点击事件
        public void onClick(View v) {



                Intent intentxx;
                int id;
                Music m;
                if (v == MusicActivity.this.imageBtnRewind) {
                    id = MusicService._id - 1;
                    if (id >= MusicActivity.this.lists.size() - 1) {
                        id = MusicActivity.this.lists.size() - 1;
                    } else if (id <= 0) {
                        id = 0;
                    }

                    m = (Music) MusicActivity.this.lists.get(id);
                    MusicActivity.this.textName.setText(m.getTitle());
                    MusicActivity.this.textSinger.setText(m.getSinger());
                    MusicActivity.this.textEndTime.setText(MusicActivity.this.toTime((int) m.getTime()));
                    MusicActivity.imageBtnPlay.setImageResource(R.drawable.pause1);
                    intentxx = new Intent(MusicActivity.this, MusicService.class);
                    intentxx.putExtra("play", "rewind");
                    intentxx.putExtra("id", id);
                    MusicActivity.this.startService(intentxx);
                    MusicActivity.this.isPlaying = true;
                } else if (v == MusicActivity.imageBtnPlay) {
                    Intent intent;
                    if (MusicActivity.this.isPlaying) {
                        intent = new Intent(MusicActivity.this, MusicService.class);
                        intent.putExtra("play", "pause");
                        MusicActivity.this.startService(intent);
                        MusicActivity.this.isPlaying = false;
                        MusicActivity.imageBtnPlay.setImageResource(R.drawable.play1);
                        MusicActivity.replaying = false;
                    } else {
                        intent = new Intent(MusicActivity.this, MusicService.class);
                        intent.putExtra("play", "playing");
                        intent.putExtra("id", MusicActivity.id);
                        MusicActivity.this.startService(intent);
                        MusicActivity.this.isPlaying = true;
                        MusicActivity.imageBtnPlay.setImageResource(R.drawable.pause1);
                        MusicActivity.replaying = true;
                    }
                } else if (v == MusicActivity.this.imageBtnForward) {
                    id = MusicService._id + 1;
                    if (id >= MusicActivity.this.lists.size() - 1) {
                        id = MusicActivity.this.lists.size() - 1;
                    } else if (id <= 0) {
                        id = 0;
                    }

                    m = (Music) MusicActivity.this.lists.get(id);
                    MusicActivity.this.textName.setText(m.getTitle());
                    MusicActivity.this.textSinger.setText(m.getSinger());
                    MusicActivity.this.textEndTime.setText(MusicActivity.this.toTime((int) m.getTime()));
                    MusicActivity.imageBtnPlay.setImageResource(R.drawable.pause1);
                    intentxx = new Intent(MusicActivity.this, MusicService.class);
                    intentxx.putExtra("play", "forward");
                    intentxx.putExtra("id", id);
                    MusicActivity.this.startService(intentxx);
                    MusicActivity.this.isPlaying = true;
                }

            }
        }
    //广播
    public class MyProgressBroadCastReceiver extends BroadcastReceiver {
        public MyProgressBroadCastReceiver() {

        }

        public void onReceive(Context context, Intent intent) {
            int  position = player.getCurrentPosition();//获取当前播放位置。
            int total = player.getDuration();//获取文件的持续时间

            int progress = position * 100 / total;
            String str = String.valueOf(position);
            Log.d("position:",str);




            MusicActivity.this.textStartTime.setTextColor(0xFFFFFFFF);//设置字体颜色
            MusicActivity.this.textStartTime.setText(MusicActivity.this.toTime(position));//设置时间
            MusicActivity.this.seekBar1.setProgress(progress);//进度
            MusicActivity.this.seekBar1.invalidate();//如果视图可见，使整个视图无效。
        }
    }

}