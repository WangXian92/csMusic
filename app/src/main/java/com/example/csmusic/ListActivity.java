package com.example.csmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import adapter.MusicAdapter;
import domian.Music;
import util.MusicList;

public class ListActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listAllMusic);

        List<Music> m = MusicList.getMusicData(getApplicationContext());

        MusicAdapter adapter = new MusicAdapter(this,m);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
                Intent intent = new Intent(ListActivity.this,MusicActivity.class);
                intent.putExtra("id",arg2);
                startActivity(intent);
            }

        });

    }
}
