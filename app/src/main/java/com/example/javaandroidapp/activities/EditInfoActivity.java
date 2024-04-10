package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.example.javaandroidapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;

import io.getstream.chat.android.ui.common.state.messages.Edit;

public class EditInfoActivity extends AppCompatActivity {
    public String imgStr;
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();

        ImageButton backBtn = findViewById(R.id.backBtn);
        LinearLayout saveBtn = findViewById(R.id.saveBtn);
        LinearLayout editPic = findViewById(R.id.editPic);
        EditText editName = findViewById(R.id.editName);
        EditText editEmail = findViewById(R.id.editEmail);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateProfile = new Intent(EditInfoActivity.this, MyListingActivity.class);
                startActivity(updateProfile);
                finish();
            }
        });
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnapshot = task.getResult();
                    if (docSnapshot.exists()) {
                        name = docSnapshot.getString("name");
                        editName.setText(name);
                        editEmail.setText(fbUser.getEmail());

                    }
                }
            }
        });

        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString();
                String newEmail = editEmail.getText().toString();
                db.collection("users").whereEqualTo("name", newName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                DocumentReference docRef = db.collection("users").document(fbUser.getUid());
                                if (newName.length() > 0) {
                                    db.collection("listings").whereEqualTo("createdBy", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    for (QueryDocumentSnapshot doc : querySnapshot) {
                                                        doc.getReference().update("createdBy", newName);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    docRef.update("name", newName);
                                }
                                if (newEmail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") && newEmail.length() > 0) {
                                    fbUser.verifyBeforeUpdateEmail(newEmail);
                                }
                                //TODO: upload image to Firebase storage, update user's profileImage field
//                if (imgStr.length() > 0){
//                    docRef.update("profileImage", imgStr);
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//
//
//                }


                            } else {
                                Toast.makeText(EditInfoActivity.this, "Name already taken, choose another name.", Toast.LENGTH_LONG).show();
                            }
                            Intent updateProfile = new Intent(EditInfoActivity.this, MyListingActivity.class);
                            startActivity(updateProfile);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    ImageView profilePic = findViewById(R.id.profileImageView);
                    Uri image = o.getData().getData();
                    imgStr = image.toString();
                    new ImageLoadTask(imgStr, profilePic).execute();

                }
            } else {
                Toast.makeText(EditInfoActivity.this, "Please select an image", Toast.LENGTH_LONG).show();
            }
        }
    });

}
