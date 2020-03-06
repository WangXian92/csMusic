package com.example.csmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import adapter.ArtistsAdapter;
import util.MusicList;

public class ArtistsActivity extends AppCompatActivity {
    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        listview = findViewById(R.id.artistListView);

        ArtistsAdapter artistsAdapter =
                new  ArtistsAdapter(this, MusicList.getMusicData(this));
        listview.setAdapter(artistsAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
                Intent intent = new Intent(ArtistsActivity.this,MusicActivity.class);
                intent.putExtra("id",arg2);
                startActivity(intent);
            }

        });

    }
}
