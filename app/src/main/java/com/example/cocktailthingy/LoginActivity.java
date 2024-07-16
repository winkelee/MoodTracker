package com.example.cocktailthingy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    public String username;
    Uri selectedImage;
    String selectedImageString;
    public EditText usernameEdit;
    ImageView avatarImg;
    String TAG = "LoggerCustom";
    static SharedPreferences.Editor editor;
    static SharedPreferences settings;
    public CardView avatar;
    static Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();
        settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstRun = settings.getBoolean("first_run", true);
        //boolean firstRun = true;
        //boolean firstRun = false;
        username = "default_user";
        usernameEdit = findViewById(R.id.usernameEdit);
        avatar = findViewById(R.id.avatar);
        avatarImg = findViewById(R.id.avatarImg);

        if (firstRun) {
            editor = settings.edit();
            editor.putBoolean("first_run", false);
            editor.apply();
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Log.i(TAG, "onClick: AVATAR CLICKED, SENDING REQUEST 3");
                    startActivityForResult(intent, 3);
                }
            });
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }


    }
    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 1);
            Log.i(TAG, "performCrop: ");
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void toNextStep(View view){
        username = usernameEdit.getText().toString();
        if(username.length() <= 10 && !username.equals(null) && username.length() != 0){

            editor.putString("username", username);
           // if(selectedImage.toString() != null){
           //     selectedImageString = selectedImage.toString();
           //     editor.putString("image", selectedImageString);
           // }
            editor.apply();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else{
            Toast toast = Toast.makeText(this, "Whoops, the username should contain no more than 10 symbols. Try to think of a different one.", Toast.LENGTH_SHORT);
            toast.show();
        }


        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null && requestCode == 3){
            selectedImage = data.getData();
            performCrop(selectedImage);
        }
        else if (requestCode == 1){
            if (data != null){
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                selectedBitmap = extras.getParcelable("data");
                avatarImg.setImageBitmap(selectedBitmap);
                String avatar = BitMapToString(selectedBitmap);
                Log.i(TAG, "onActivityResult: BITMAP: " + avatar);
                editor.putString("avatar", avatar);
                editor.apply();
            }
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}