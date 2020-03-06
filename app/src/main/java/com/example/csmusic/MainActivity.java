package com.example.csmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            /*
            * Android——requestWindowFeature()的应用
                Android开发中经常会在setContentView(R.layout.XXX); 前设置requestWindowFeature(XXXX)。

              他的意思是需要软件全屏显示、自定义标题（使用按钮等控件）和其他的需求

              首先介绍一个重要方法那就是requestWindowFeature(featrueId),它的功能是启用窗体的扩展特性。参数是Window类中定义的常量。
            * */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ////设置窗体全屏
        /*
        FLAG_FULLSCREEN 全屏
        Window flag: hide all screen decorations (such as the status bar) while this window is displayed.

                FLAG_KEEP_SCREEN_ON 始终点亮
        Window flag: as long as this window is visible to the user, keep the device’s screen turned on and bright.

        FLAG_BLUR_BEHIND 背景模糊
        This constant was deprecated in API level 14. Blurring is no longer supported.
        */
        //获取图片资源
        Resources res = this.getResources();
        //选项卡
        TabHost tabHost = this.getTabHost();

        //点击事件MainActivity调用ListActivity
        Intent intent = (new Intent()).setClass(this, ListActivity.class);
        TabHost.TabSpec spec = tabHost.newTabSpec("音乐")
                .setIndicator("音乐", res.getDrawable(R.drawable.item)).setContent(intent);
        tabHost.addTab(spec);

        //点击事件MainActivity调用ArtistsActivity
        intent = (new Intent())
                .setClass(this, AlbumsActivity.class);
        spec = tabHost.newTabSpec("艺术家")
                .setIndicator("艺术家", res.getDrawable(R.drawable.artist)).setContent(intent);
        tabHost.addTab(spec);

        //点击事件MainActivity调用AlbumsActivity
        intent = (new Intent())
                .setClass(this, ArtistsActivity.class);
        spec = tabHost.newTabSpec("专辑")
                .setIndicator("专辑", res.getDrawable(R.drawable.album)).setContent(intent);
        tabHost.addTab(spec);

        //点击事件MainActivity调用SongsActivity
        intent = (new Intent())
                .setClass(this, SongsActivity.class);
        spec = tabHost.newTabSpec("最近播放")
                .setIndicator("最近播放", res.getDrawable(R.drawable.album)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);



    }

}
