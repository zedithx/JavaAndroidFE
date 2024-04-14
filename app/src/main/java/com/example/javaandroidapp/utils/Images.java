package com.example.javaandroidapp.utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class Images {
    public static void addImage(Uri image, StorageReference storage, Callbacks callback) {
        if (image == null){
            callback.getResult("");
            return;
        }
        StorageReference storageRef = storage.child("/" + UUID.randomUUID().toString());
        Task<Uri> urlTask = storageRef.putFile(image).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    callback.getResult(downloadUri.toString());
                }
                else {
                    Log.d("image upload", "Failed");
                    callback.getResult("");
                }
            }
        });
    }

    public static void addImages(ArrayList<Uri> images, StorageReference storage, Callbacks callback) {
        ArrayList<String> imageList = new ArrayList<>();
        for (int imageIndex = 0; imageIndex < images.size(); imageIndex++) {
            StorageReference storageRef = storage.child("/" + UUID.randomUUID().toString());
            Task<Uri> urlTask = storageRef.putFile(images.get(imageIndex)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageList.add(downloadUri.toString());
                        if (imageList.size() == images.size()){
                            callback.getArrayListOfString(imageList);
                        }
                    }
                }
            });
        }
    }
}
