package com.example.sam.communify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void mapsIntent(View view){
        Intent goToMaps = new Intent(this, MapsActivity.class);
        startActivity(goToMaps);
    }
    public void takePhotoIntent(View view){
        Intent takePhoto = new Intent(this, Camera.class);
        startActivity(takePhoto);
    }
}
