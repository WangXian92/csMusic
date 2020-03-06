package com.example.csmusic;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.view.animation.AnimationUtils;

import java.io.IOException;
import java.util.List;

import domian.Music;
import util.LrcView;
import util.MusicList;

//实现多线程接口
// /*
//      　　　　　　　　1、定义一个类实现Runnable接口
//
//      　　　　　　　　2、覆盖Runnable接口中的 run方法
//         　　　　　　　　     将线程要运行的代码放在run方法中
//
//     　　　　　　　　 3、同过Thread类建立线程对象
//
//     　　　　　　　　4、将Runnable接口的子类对象作为实际参数传递给Thread类的构造函数。
//             　　　　　　　　 为什么要将Runnable接口的子类对象传递给Thread的构造函数。
//           　　　　　　　　  因为，自定义的run方法所属的对象是Runnable 接口的子类对象。
//
//
//
//    　　　　　　　　  5、调用Thread类的start方法开启线程并调用Runnable接口子类的run方法。
//
//
//      　　　　　　　　　　实现方式和继承的方式有什么区别？
//
//      　　　　　　　　　　　　　　实现方法的好处：避免了单继承的钱局限性
//     　　　　　　　　　　　　　　 在定义线程过程中，建立使用实现方式
//
//
//     　　　　　　　　　　 两种方式的区别：
//   　　　　　　　　　　　　　　  继承Tread：  线程代码存放Tread子类run方法中；
//     　　　　　　　　　　　　　　实现 Runnable：线程代码在接口的子类run方法中*/
public class MusicService extends Service implements Runnable {
    public static MediaPlayer player;//音频播放器
    public static int u;
    private List<Music> lists;
    public static int _id = 0;

    public static Boolean isRun = true;

    public LrcView mLrcView;
    public static int playing_id = 0;
    public static Boolean playing = false;

//1.handler作用:
//    1）传递消息Message
//2）子线程通知主线程更新ui


    Handler  mHandler = new Handler();

    private int index = 0;
    private int CurrentTime = 0;
    private int CountTime = 0;

    public MusicService() {

    }

    public IBinder onBind(Intent arg0) {

        return null;
    }

    public void onCreate() {
        //提取歌曲
       lists = MusicList.getMusicData(this.getApplicationContext());//返回当前应用的上下文，应用销毁他才销毁


        SeekBarBroadcastReceiver receiver = new SeekBarBroadcastReceiver();//广播
        //意图筛选器 IntentFilter  歌曲进度发生变化时调用
        IntentFilter filter = new IntentFilter("cn.com.karl.seekBar");
        this.registerReceiver(receiver, filter);//动态注册
        new Thread(this).start();//启动子线程  陷入死循环  但是主线程还是继续往下执行




        super.onCreate();//调用父类

    }



    public void  onStart(Intent intent, int startId) {//启动或返回活动是调用
        super.onStart(intent,startId);
        String play = intent.getStringExtra("play");//取出play相对字符串



        _id = intent.getIntExtra("id", 1);//取出id索引数据


        if (play.equals("play")) {//比较字符串中所包含的内容是否相同 第一次开始播放
            if (this.player != null) {

                this.player.release();//释放掉相关的资源

                this.player = null;
            }

            this.playMusic(_id);//传入id

        } else if (play.equals("pause")) {
            if (this.player != null) {
                this.player.pause();//暂停播放音频
            }
        } else if (play.equals("playing")) {
            if (this.player != null) {
                this.player.start();//开始或继续播放音频
            } else {
                this.playMusic(_id);
            }
        } else if (!play.equals("replaying")) {//重新播放 ！为逻辑非 play不等于replaying
            int id;
            if (play.equals("first")) {
                id = intent.getIntExtra("id", 0);//取出id
                this.playMusic(id);
            } else if (play.equals("rewind")) {
                id = intent.getIntExtra("id", 0);
                this.playMusic(id);
            } else if (play.equals("forward")) {
                id = intent.getIntExtra("id", 0);
                this.playMusic(id);
            } else if (play.equals("last")) {
                id = intent.getIntExtra("id", 0);
                this.playMusic(id);
            }
        }

    }


    public void playMusic(int id) {//播放音乐


        MusicActivity.lrc_view.setAnimation(AnimationUtils.loadAnimation(MusicService.this, R.anim.alpha));//动画


       if (player != null) {
            player.release();//释放掉相关的资源
            player = null;
        }

        if (id >= lists.size() - 1) {//id大于等于lists的元素数量-1  数组下表是从0开始所以要减去1
            _id = lists.size() - 1;//赋值给元素的数量
        } else if (id <= 0) {
            _id = 0;
        }


        Music m = (Music)this.lists.get(_id);//传入数量
        String url = m.getUrl();//获取url
        Uri myUri = Uri.parse(url);//传入url
        this.player = new MediaPlayer();//获取对象
        this.player.reset();//将MediaPlayer对象重置到刚刚创建的状态
        this.player.setAudioStreamType(3);//指定流媒体类型

        try {
            this.player.setDataSource(this.getApplicationContext(), myUri);//传入Context上下文 和需要播放的内容myUri
            this.player.prepare();//在播放之前完成准备工作
        } catch (IllegalArgumentException var6) {//感觉不同的状态响应不同的错误
            var6.printStackTrace();
        } catch (SecurityException var7) {
            var7.printStackTrace();
        } catch (IllegalStateException var8) {
            var8.printStackTrace();
        } catch (IOException var9) {
            var9.printStackTrace();
        }



        this.player.start();//开始播放音频

        if(!player.isPlaying()){
            u =_id;
            ++_id;

            MusicService.this.player.reset();//将对象重置到刚刚创建状态
            Intent intent = new Intent("cn.com.karl.completion");//传入信息
            sendBroadcast(intent);
            playMusic(_id);
        }

        //点击事件
        // 注册在媒体源结束时调用的回调

        //      在MediaPlayer播放结束时会回调
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Intent intent;
                //当歌曲播放完毕  开始下一曲 播放

                if (MusicActivity.isLoop) {

                    MusicService.this.player.reset();//将对象重置到刚刚创建状态

                    intent = new Intent("cn.com.karl.completion");//传入信息
                    sendBroadcast(intent);// 最普通的发送intent的方式，是一种无序的广播机制，理论上，所有的接受者同时获得该intent的消息， 接受者之间不存在先后顺序， 不能截断/修改intent的数据。 应用普遍使用的就是该方式。
                    ++MusicService._id;
                    MusicService.this.playMusic(MusicService._id);//回调自身

                } else {

                    MusicService.this.player.reset();

                    intent = new Intent("cn.com.karl.completion");
                    MusicService.this.sendBroadcast(intent);
                    MusicService.this.playMusic(MusicService._id);
                }

            }
        });
        //注册发生错误时要调用的回调
        //
        //*在异步操作期间。
        this.player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (MusicService.this.player != null) {
                    MusicService.this.player.release();//释放掉与对象的相关资源
                    MusicService.this.player = null;
                }

                Music m = (Music)lists.get(_id);//返回此列表中指定位置的元素。
                String url = m.getUrl();//获取url
                Uri myUri = Uri.parse(url);//返回给定Uri字符串的Uri
                MusicService.this.player = new MediaPlayer();//获取对象
                MusicService.this.player.reset();//将MediaPlayer对象重置到甘冈创建的状态
                MusicService.this.player.setAudioStreamType(3);//指定流媒体类型

                try {
                    MusicService.this.player.setDataSource(getApplicationContext(), myUri);//传入上下文Context和Uri
                    MusicService.this.player.prepare();//同步的方式装载流媒体文件
                } catch (IllegalArgumentException var8) {
                    var8.printStackTrace();
                } catch (SecurityException var9) {
                    var9.printStackTrace();
                } catch (IllegalStateException var10) {
                    var10.printStackTrace();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }

                MusicService.this.player.start();//开始播放音频

                return false;
            }
        });
    }

        //多线程函数  //子线程一直在执行  不影响主线程
    public void run() {
        while(isRun) {//
            try {
                Thread.sleep(200L);//使当前执行的线程休眠200毫秒
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }
            if (this.player != null) {


                        Intent intent = new Intent("cn.com.karl.progress");
                        this.sendBroadcast(intent);//发送系统广播
            }
/*


 */
        }


    }

    private class SeekBarBroadcastReceiver extends BroadcastReceiver {//广播
        private SeekBarBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {

            //取出歌曲进度值
            int seekBarPosition = intent.getIntExtra("seekBarPosition", 0);
            //seekBarPosition*getDuration() 获取文件的持续时间/100

            //寻找指定的时间位置
            MusicService.this.player.seekTo(seekBarPosition * MusicService.this.player.getDuration() / 100);
            /*
            * 开始或继续播放。如果先前已暂停播放，

             *播放将从暂停的位置继续。如果回放有

             *已停止或以前从未启动，播放将从

             *开始。*/
            MusicService.this.player.start();
        }
    }

}
