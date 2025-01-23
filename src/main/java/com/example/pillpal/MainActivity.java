package com.example.pillpal;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private int dots_index = 0;
    private int total_meds;
    private File savedImageFile;
    private String med_name, med_course, med_time;
    private TextView medname, medcourse, medtime;
    private int med_index= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CardView med_reminder = findViewById(R.id.med_reminder_view);
        ImageView med_img = findViewById(R.id.med_img);
        dotsLayout = findViewById(R.id.dotsLayout);
        medname = findViewById(R.id.med_name);
        medcourse = findViewById(R.id.med_days_left);
        medtime = findViewById(R.id.med_timer);

        // test
        Button btn = findViewById(R.id.btn);
        //

        SharedPreferences sharedPreferences= getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("med", Context.MODE_PRIVATE);

        String c = sharedPreferences.getString("photocount", "0");
        total_meds = Integer.parseInt(c);


        File storageDir = getExternalFilesDir(null);
        File initial_pic = new File(storageDir, "img_captured0.jpg");
        String initital_med_name = sharedPreferences1.getString("medname1", "");
        String initital_med_course = sharedPreferences1.getString("medcourse1", "");
        String initital_med_time = sharedPreferences1.getString("medtime1", "");

        medname.setText(initital_med_name);
        medcourse.setText(initital_med_course);
        medtime.setText(initital_med_time);

        if (initial_pic.exists()) {
            // Set the saved image to the ImageView
            med_img.setImageURI(Uri.fromFile(initial_pic));

        }else {
            med_img.setImageResource(R.drawable.ic_launcher_background);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddMedsActivity.class));
            }
        });

        med_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ArrayList<Number> arr_imgs = new ArrayList<>();
//                arr_imgs.add(R.drawable.med_sample);
//                arr_imgs.add(R.drawable.ic_launcher_background);
//                arr_imgs.add(R.drawable.ic_launcher_foreground);

                String dotindex = Integer.toString(updateDotIndex());
                String dotindex_med = Integer.toString(med_index);

                med_name = sharedPreferences1.getString("medname"+dotindex_med, "");
                med_course = sharedPreferences1.getString("medcourse"+dotindex_med, "");
                med_time = sharedPreferences1.getString("medtime"+dotindex_med, "");

                savedImageFile = new File(storageDir, "img_captured"+dotindex+".jpg");

                med_img.setImageURI(Uri.fromFile(savedImageFile));

                medname.setText(med_name);
                medcourse.setText(med_course);
                medtime.setText(med_time);
//                if (savedImageFile.exists()) {
//                    // Set the saved image to the ImageView
//                    med_img.setImageURI(Uri.fromFile(savedImageFile));
////                    Toast.makeText(this, Uri.fromFile(savedImageFile).toString(), Toast.LENGTH_SHORT).show();
////                    btn.setText(savedImageFile.toString());
//
//                }
//                med_img.setImageResource((Integer) arr_imgs.get(updateDotIndex()));
                setupDots();
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("9876543210")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                Toast.makeText(MainActivity.this, document.getId(), Toast.LENGTH_SHORT).show();
                                btn.setText(document.getId());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        setupDots();
}
    private void setupDots() {
        dotsLayout.removeAllViews(); // Clear existing dots

        for (int i = 0; i < total_meds; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0); // Add spacing between dots
            dot.setLayoutParams(params);
            dot.setImageResource(i == dots_index ? R.drawable.dot_selected : R.drawable.dot_unselected);
            dotsLayout.addView(dot);
        }

    }

    private int updateDotIndex()
    {
//        SharedPreferences sharedPreferences= getSharedPreferences("photo", Context.MODE_PRIVATE);
//
//        String c = sharedPreferences.getString("photocount", "0");
//        total_meds = Integer.parseInt(c);
        if (dots_index == total_meds-1)
        {
            dots_index=0;
            med_index=1;
        }else{
            dots_index++;
            med_index++;
        }
        return dots_index;
    }
}