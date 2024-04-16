package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.User;
import com.example.javaandroidapp.utils.Images;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginSetupActivity extends AppCompatActivity {
    public User user;
    public String name;
    ImageView profileImageView;
    Uri image;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    profileImageView = findViewById(R.id.profileImageView);
                    image = o.getData().getData();
                    Log.d("image", "image" +image);
                    Glide.with(getApplicationContext()).load(image).into(profileImageView);
                }
            } else {
                Toast.makeText(LoginSetupActivity.this, "Please select an image", Toast.LENGTH_LONG).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_setup);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        Button nextBtn = findViewById(R.id.next_button);
        LinearLayout editPic = findViewById(R.id.editPic);
        EditText editName = findViewById(R.id.editName);
        ImageView profileImageView = findViewById(R.id.profileImageView);

        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString();
                DocumentReference docRef = db.collection("users").document(fbUser.getUid());
                Images.addImage(image, storageRef, new CallbackAdapter() {
                    @Override
                    public void getResult(String new_image) {
                        if (!new_image.equals("")) {
                            docRef.update("profileImage", new_image);
                        }
                        docRef.update("name", newName);
                        Intent createProfile = new Intent(LoginSetupActivity.this, TransitionLandingActivity.class);
                        startActivity(createProfile);
                        finish();

                    }
                });
            }
        });
    }

}
