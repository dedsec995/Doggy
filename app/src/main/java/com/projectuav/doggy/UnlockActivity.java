package com.projectuav.doggy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.projectuav.doggy.adapters.UnlockAdapter;

import java.util.Arrays;
import java.util.Collections;

public class UnlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences prefImg = getApplicationContext().getSharedPreferences("imagesText",MODE_PRIVATE);
        final String[] myDataset = getString(R.string.dog_list).split(",");

        for (int i = 0;i<120 ; i++){
            if (pref.getBoolean(myDataset[i],false)){
                myDataset[i] = myDataset[i] + "-_-_-_-_-" + "1" + "-_-_-_-_-" + prefImg.getString(myDataset[i],null);
            }
            else{
                myDataset[i] = myDataset[i] + "-_-_-_-_-" + "0" + "-_-_-_-_-" + prefImg.getString(myDataset[i],null);
            }
        }

//        Collections.reverse(Arrays.asList(myDataset));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.unlockView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager( new GridLayoutManager(UnlockActivity.this, 2));
//        recyclerView.setLayoutManager(layoutManager);
        UnlockAdapter mAdapter = new UnlockAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnListClickListener(new UnlockAdapter.OnItemClickListener() {
            @Override
            public void onListClick(int position) {
//                Toast.makeText(UnlockActivity.this, String.valueOf(position) + myDataset[position], Toast.LENGTH_SHORT).show();
            }
        });
    }

}