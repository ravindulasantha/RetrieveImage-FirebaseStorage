package com.example.retrieveimagefirebase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.retrieveimagefirebase.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {

    //ActivityMainBinding binding ;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    ImageView imageView;

    private int imageID = 0;
    private Handler handler;
    private Runnable runnable;

    private String adClickUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.ad_img);

       // String imageID = "1";

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                imageID++;
                // Reset the imageID to 1 if it exceeds 6
                if (imageID > 6) {
                    imageID = 1;
                }

                // Update the storageReference with the new imageID
                storageReference = FirebaseStorage.getInstance().getReference("adimages/" + imageID + ".png");
                databaseReference = FirebaseDatabase.getInstance().getReference("Ad/"+imageID);
                try {
                    File localfile = File.createTempFile("tempfile", ".png");
                    storageReference.getFile(localfile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                    imageView.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to download ads, reset imageID to 1
                                    //Toast.makeText(MainActivity.this, "Failed to ads", Toast.LENGTH_SHORT).show();
                                    imageID = 1;
                                }
                            });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Schedule the next execution after 6 seconds
                handler.postDelayed(this, 10000);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    adClickUrl = dataSnapshot.child("AdClick").getValue(String.class);

                                    if (adClickUrl != null) {
                                        try {
                                            Uri uri = Uri.parse(adClickUrl);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error opening URL: " + e.getMessage());
                                            // Handle the exception, e.g., show an error message
                                        }
                                    }

                                } else {

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Firebase", "Error retrieving data from Firebase: " + databaseError.getMessage());
                            }
                        });

                    }
                });

            }
        };

        // Start the periodic task
        handler.postDelayed(runnable, 0);

    }
}