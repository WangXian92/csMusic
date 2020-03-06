package com.example.csmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import adapter.AlbumsAdapter;
import util.MusicList;

public class AlbumsActivity extends AppCompatActivity {
    private ListView albumListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        albumListView = (ListView)this.findViewById(R.id.albumListView);//

        AlbumsAdapter adapter = new AlbumsAdapter(this, MusicList.getMusicData(this));//适配器

        albumListView.setAdapter(adapter);//加载适配器
        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){

                Intent intent = new Intent(AlbumsActivity.this,MusicActivity.class);
                intent.putExtra("id",arg2);
                startActivity(intent);

            }

        });


    }
}
