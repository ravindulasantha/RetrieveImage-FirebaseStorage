package com.example.retrieveimagefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.retrieveimagefirebase.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {

    //ActivityMainBinding binding ;

    StorageReference storageReference;
    ProgressDialog progressDialog;

    Button getimage;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        binding = ActivityMainBinding.inflate(getLayoutInflater().inflate());
//        setContentView(binding.getRoot());

        imageView = findViewById(R.id.ad_img);
        getimage = findViewById(R.id.getImage);
        getimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imageID = "2";

                storageReference = FirebaseStorage.getInstance().getReference("adimages/"+imageID+".png");

                try {
                    File localfile = File.createTempFile("tempfile",".png");
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
                                    Toast.makeText(MainActivity.this, "Failed to ads", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}