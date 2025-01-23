package com.example.pillpal;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class AddMedsActivity extends AppCompatActivity {

    private ImageCapture imageCapture;
    private ImageView img;
    private File savedImageFile;
    private int photo_count;
    private Button btn;
    String time;
    EditText med_name, med_course;
    private int btn_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_meds);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        img = findViewById(R.id.img);
        btn = findViewById(R.id.img_btn);
        Button time_picker = findViewById(R.id.time_pick);
        med_name =findViewById(R.id.med_name_input);
        med_course = findViewById(R.id.med_course_input);


        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // Hour in 24-hour format
                int minute = calendar.get(Calendar.MINUTE); // Current minute

                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddMedsActivity.this,
                        (v, hourOfDay, minuteOfHour) -> {
                            // Format the time and display it
                            time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                            time_picker.setText(time);
//                            timeTextView.setText("Selected Time: " + time);
                        },
                        hour, minute, true // true for 24-hour format, false for 12-hour format
                );

                // Show the time picker dialog
                timePickerDialog.show();
            }
        });


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Initialize camera
//            startCamera();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

//        int btn_state = 0;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if (btn_state == 0) {
                    btn_state = 1;
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Start the activity with camera_intent, and request pic id
                    startActivityForResult(camera_intent, 123);
                    btn.setText("ADD");
//                    btn_state=1;
                }
                else {
                    SharedPreferences sharedPreferences1 = getSharedPreferences("med", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    String mname = String.valueOf(med_name.getText());
                    String mcourse = String.valueOf(med_course.getText());
                    editor.putString("medname"+ photo_count, mname).apply();
                    editor.putString("medcourse"+photo_count, mcourse).apply();
                    editor.putString("medtime"+photo_count, time).apply();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == 123) {
            // BitMap is data structure of image file which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
//            Toast.makeText(this, photo.toString(), Toast.LENGTH_SHORT).show();
//            img.setImageBitmap(photo);
            saveImageToFile(photo);
            loadSavedImage();

        }
    }

    private void saveImageToFile(Bitmap photo) {
        // Create a directory to save the photo (you can change the directory path as needed)

        SharedPreferences sharedPreferences= getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString()
        String c = sharedPreferences.getString("photocount", "0");

//        if (c==null){photo_count=0;}
//        else{photo_count=Integer.parseInt(c);}
        photo_count=Integer.parseInt(c);
        File storageDir = getExternalFilesDir(null);
        if (storageDir != null) {
            savedImageFile = new File(storageDir, "img_captured"+photo_count+".jpg");
            photo_count+=1;
            editor.putString("photocount", Integer.toString(photo_count)).apply();
            try {
                // Open a file output stream to write the bitmap to the file
                FileOutputStream fos = new FileOutputStream(savedImageFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadSavedImage() {
        if (savedImageFile != null && savedImageFile.exists()) {
            // Set the saved image to the ImageView
            img.setImageURI(Uri.fromFile(savedImageFile));
            Toast.makeText(this, Uri.fromFile(savedImageFile).toString(), Toast.LENGTH_SHORT).show();
            btn.setText(savedImageFile.toString());

        } else {
            Toast.makeText(this, "No saved image found.", Toast.LENGTH_SHORT).show();
        }
    }


}