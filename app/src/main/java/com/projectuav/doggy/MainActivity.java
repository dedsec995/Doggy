package com.projectuav.doggy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projectuav.doggy.static_classify.static_classify_Classify;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // button for each available classifier
    Uri uri;
    public static final String SHARED_PREFS = "doggy";
    public static final String LOCKLIST = "LOCKLIST";

    // for permission requests
    public static final int REQUEST_PERMISSION = 300;

    // request code for permission requests to the os for image
    public static final int REQUEST_IMAGE = 100;

    // will hold uri of image obtained from camera
    private Uri imageUri;

    // string to send to next activity that describes the chosen classifier
    private String chosen;

    //boolean value dictating if chosen model is quantized version or not.
    private boolean quant;
    Button static_btn, profile_btn;
    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences temp = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences.Editor tempEditor = temp.edit();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        Boolean appOpenedFirstTime = pref.getBoolean("isIt",true);

        final String[] myDataset = getString(R.string.dog_list).split(",");

        if (appOpenedFirstTime){
            Toast.makeText(this, "If is Executed", Toast.LENGTH_SHORT).show();
            for (int i=0; i < 120; i++){
                tempEditor.putBoolean(myDataset[i], false);
                tempEditor.apply();
            }
            prefEditor.putBoolean("isIt", false);
            prefEditor.apply();

        }

        static_btn = (Button) findViewById(R.id.static_button);
        static_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // request permission to use the camera on the user's phone
//                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_PERMISSION);
//                }
//
//                // request permission to write data (aka images) to the user's external storage of their phone
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQUEST_PERMISSION);
//                }
//
//                // request permission to read data (aka images) from the user's external storage of their phone
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            REQUEST_PERMISSION);
//                }
                // filename in assets
                chosen = "10Animals.tflite";
                // model in not quantized
                quant = false;
                // open camera
                openCameraIntent();
            }
        });
        profile_btn = findViewById(R.id.profile_button);
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        scoreText = findViewById(R.id.scoreText);
        int scoreTotal =0;
        for (int i = 0;i< 120 ; i++){
            if (temp.getBoolean(myDataset[i],false)){
                scoreTotal = scoreTotal + 1;
            }
        }
        scoreText.setText("Current Score: "+String.valueOf(scoreTotal)+"/120");
    }


    // opens camera for user
    private void openCameraIntent() {
        CropImage.startPickImageActivity(MainActivity.this);
    }

    // checks that the user has allowed all the required permission of read and write and camera. If not, notify the user and close the application
    @SuppressLint("ShowToast")
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "This application needs read, write, and camera permissions to run. Application now closing.", Toast.LENGTH_LONG);
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
            Intent i = new Intent(MainActivity.this, static_classify_Classify.class);
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

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(getDrawable(R.drawable.hayekarlopehle))
                .setTitle("Quit " + getString(R.string.app_name) + "?")
                .setMessage("Are you sure you want to go?")
                .setNegativeButton("Not Yet", null)
                .setPositiveButton("Yes it's Time", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .show();
    }

    private void startCrop(Uri imageuri){
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

}
