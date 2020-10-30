package com.projectuav.doggy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projectuav.doggy.adapters.ProfileAdapter;
import com.projectuav.doggy.static_classify.static_classify_Classify;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import static com.projectuav.doggy.MainActivity.REQUEST_PERMISSION;

public class ProfileActivity extends AppCompatActivity {
    final int dogTotalAmount = 120;
    int totalUnlockedNumber = 0;
    int totalLockedNumber = 0;
    Button remaining_btn,unlocked_btn,reset_btn, back_btn, snap_btn;
    TextView dogsTotal, snapsTotal, profileInfo;
    private String chosen;
    private boolean quant;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences prefImg = getApplicationContext().getSharedPreferences("imagesText",MODE_PRIVATE);
        final String snapsX = pref.getString("TotalSnaps","0");
        final String[] myDataset = getString(R.string.dog_list).split(",");
        remaining_btn = findViewById(R.id.remaining_btn);
        unlocked_btn = findViewById(R.id.unlocked_btn);
        reset_btn = findViewById(R.id.reset_btn);
        dogsTotal = findViewById(R.id.dogsTotal);
        snapsTotal = findViewById(R.id.snapsTotal);
        profileInfo = findViewById(R.id.profileInfo);
        back_btn = findViewById(R.id.profile_back);
        snap_btn = findViewById(R.id.profile_snap);
        List<String> myUnlockedDatasetList = new ArrayList<>();
        List<String> myLockedDatasetList = new ArrayList<>();
        for (int i = 0;i< dogTotalAmount ; i++){
            if (pref.getBoolean(myDataset[i],false)){
                myUnlockedDatasetList.add(myDataset[i] + "-_-_-_-_-" + "1" + "-_-_-_-_-" + prefImg.getString(myDataset[i],null));
                totalUnlockedNumber = totalUnlockedNumber + 1;
            }
            else{
                myLockedDatasetList.add(myDataset[i] + "-_-_-_-_-" + "0" + "-_-_-_-_-" + prefImg.getString(myDataset[i],null));
                totalLockedNumber = totalLockedNumber + 1;
            }
        }
        String[] myUnlockedDataset = new String[totalUnlockedNumber];
        for(int x = 0; x < totalUnlockedNumber; x++){
            myUnlockedDataset[x] = myUnlockedDatasetList.get(x);
        }
        String[] myLockedDataset = new String[totalLockedNumber];
        for(int x = 0; x < totalLockedNumber; x++){
            myLockedDataset[x] = myLockedDatasetList.get(x);
        }
        snapsTotal.setText("Total Snaps: " + String.valueOf(snapsX));
        dogsTotal.setText("Total Doggs: " + String.valueOf(totalUnlockedNumber));
        remaining_btn.setText("REMAINING("+String.valueOf(totalLockedNumber)+")");
        unlocked_btn.setText("UNLOCKED("+String.valueOf(totalUnlockedNumber)+")");
        if (totalUnlockedNumber == 0){
            Toast.makeText(ProfileActivity.this, "You have not unlocked any dogg!", Toast.LENGTH_SHORT).show();
        }
//        Collections.reverse(Arrays.asList(myDataset));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager( new GridLayoutManager(ProfileActivity.this, 2));
//        recyclerView.setLayoutManager(layoutManager);
        ProfileAdapter mAdapter = new ProfileAdapter(myUnlockedDataset);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnListClickListener(new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onListClick(int position) {
//                Toast.makeText(ProfileActivity.this, String.valueOf(position) + myDataset[position], Toast.LENGTH_SHORT).show();
            }
        });
        remaining_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remaining_btn.setBackgroundResource(R.drawable.button_shape_gold);
                remaining_btn.setTextColor(Color.parseColor("#111111"));
                unlocked_btn.setBackgroundResource(R.drawable.button_shape_dark);
                remaining_btn.setTextColor(Color.parseColor("#C3CEFD"));
                ProfileAdapter mAdapter = new ProfileAdapter(myLockedDataset);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnListClickListener(new ProfileAdapter.OnItemClickListener() {
                    @Override
                    public void onListClick(int position) {
                    }
                });
            }
        });
        unlocked_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlocked_btn.setBackgroundResource(R.drawable.button_shape_gold);
                remaining_btn.setTextColor(Color.parseColor("#222222"));
                remaining_btn.setBackgroundResource(R.drawable.button_shape_dark);
                remaining_btn.setTextColor(Color.parseColor("#C3CEFD"));
                if (totalUnlockedNumber == 0){
                    Toast.makeText(ProfileActivity.this, "You have not unlocked any dogg!", Toast.LENGTH_SHORT).show();
                }
                ProfileAdapter mAdapter = new ProfileAdapter(myUnlockedDataset);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnListClickListener(new ProfileAdapter.OnItemClickListener() {
                    @Override
                    public void onListClick(int position) {
                    }
                });
            }

        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        snap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filename in assets
                chosen = "slow.tflite";
                // model in not quantized
                quant = false;
                // open camera
                openCameraIntent();
            }
        });

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void reset() {
        new AlertDialog.Builder(this)
                .setIcon(getDrawable(R.drawable.hayekarlopehle))
                .setTitle("RESET ALL PROGRESS")
                .setMessage("Are you sure? This will delete everything and cannot be reversed.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEverything();
                    }
                })
                .show();
    }

    public void deleteEverything() {
        SharedPreferences prefX = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences prefImgX = getApplicationContext().getSharedPreferences("imagesText",MODE_PRIVATE);
        SharedPreferences.Editor prefXXX = prefX.edit();
        SharedPreferences.Editor prefImgXXX = prefImgX.edit();
        final String[] myDataset = getString(R.string.dog_list).split(",");
        final int dogTotalAmount = 120;
        for (int i = 0;i< dogTotalAmount ; i++){
            prefXXX.putBoolean(myDataset[i], false);
            prefXXX.apply();
            prefImgXXX.putString(myDataset[i],"");
            prefImgXXX.apply();
        }
        prefXXX.putString("TotalSnaps","0");
        prefXXX.apply();
        finish();
    }
    private void openCameraIntent() {
        CropImage.startPickImageActivity(ProfileActivity.this);
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @SuppressLint("ShowToast")
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(ProfileActivity.this, "This application needs read, write, and camera permissions to run. Application now closing.", Toast.LENGTH_LONG);
                System.exit(0);
            }
        }
    }

    // dictates what to do after the user takes an image, selects and image, or crops an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the camera activity is finished, obtained the uri, crop it to make it square, and send it to 'Classify' activity
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                Uri imageuri = CropImage.getPickImageResultUri(this,data);
                if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                    uri = imageuri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }else {
                    startCrop(imageuri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if cropping activty is finished, get the resulting cropped image uri and send it to 'Classify' activity
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
//            imageUri = Crop.getOutput(data);
            Intent i = new Intent(ProfileActivity.this, static_classify_Classify.class);
            // put image data in extras to send
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                int nh = (int) ( bitmap.getHeight() * (1080.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1080, nh, true);
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), scaled, "Title", "null");
                resultUri = Uri.parse(path);

//                AMIT WALA
                i.putExtra("resID_uri", resultUri);
                // put filename in extras
                i.putExtra("chosen", chosen);
                // put model type in extras
                i.putExtra("quant", quant);
                // send other required data
                startActivity(i);
            }
            catch (Exception e) {
                Toast.makeText(this, "Please try again!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void startCrop(Uri imageuri){
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

}