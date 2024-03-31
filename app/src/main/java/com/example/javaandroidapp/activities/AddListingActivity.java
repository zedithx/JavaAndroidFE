package com.example.javaandroidapp.activities;

import static android.app.ProgressDialog.show;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.fragments.DatePickerFragment;
import com.example.javaandroidapp.fragments.TimePickerFragment;
import com.example.javaandroidapp.utils.Categories;
import com.example.javaandroidapp.utils.Images;
import com.example.javaandroidapp.utils.Listings;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Value;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddListingActivity extends AppCompatActivity {
    ImageButton addImageButton;
    LinearLayout addListingLayout;
    Uri image;
    ArrayList<String> uriArrList = new ArrayList<>();
    LinearLayout displayImageLayout;

    private List<String> categories = new ArrayList<String>();

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    displayImageLayout = findViewById(R.id.displayImageLayout);
                    image = o.getData().getData();
                    uriArrList.add(String.valueOf(image));
//                    Glide.with(getApplicationContext()).load(image).apply(new RequestOptions().override(100, 100)).into(addImageButton);
                    displayImageLayout.addView(addNewImageButton(image));
                    //Used to create a new button dynamically
//                    ImageButton button = new ImageButton(getApplicationContext());
//                    addListingLayout.addView(button);
                }
            } else {
                Toast.makeText(AddListingActivity.this, "Please select an image", Toast.LENGTH_LONG).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser == null) {
            Intent notSignedIn = new Intent(AddListingActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_listing);
        addImageButton = findViewById(R.id.addImageButton);
        addListingLayout = (LinearLayout) findViewById(R.id.addListingLayout);
        TextView addDate = findViewById(R.id.addDate);
        TextView addTime = findViewById(R.id.addTime);
        EditText productName = findViewById(R.id.addProductName);
        EditText description = findViewById(R.id.addDescription);
        EditText oldPrice = findViewById(R.id.addOldPrice);
        EditText newPrice = findViewById(R.id.addNewPrice);
        EditText minOrder = findViewById(R.id.addMinOrder);
        Spinner category = findViewById(R.id.addCategory);
        TextView addListingButton = findViewById(R.id.addListingButton);
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(AddListingActivity.this, MenuActivity.class);
                startActivity(Main);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);
        Categories.getCategoryString(db, new CallbackAdapter() {
            @Override
            public void getListOfString(List<String> categories_new) {
                categories.clear();
                categories.addAll(categories_new);
                adapter.notifyDataSetChanged();
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog newDatePickerDialog = new DatePickerDialog(
                        AddListingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        addDate.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day
                );
                newDatePickerDialog.show();
            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar timeCalendar = Calendar.getInstance();
                int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
                int min = timeCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(AddListingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int setHour, int setMin) {
                        addTime.setText(String.format("%02d:%02d", setHour, setMin));
                    }
                }, hour, min, true);
                mTimePicker.show();
            }
        });

        addListingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Images.addImages(image, storageRef, new CallbackAdapter() {
                    @Override
                    public void getResult(String uri) {
                        //TODO - change to list of Image
//                        ArrayList<String> uri_list = new ArrayList<>();
//                        uri_list.add(uri);
                        //TODO - change to hashmap population from edittext
                        ArrayList<String> variantNames = new ArrayList<>();
                        variantNames.add("Small");
                        variantNames.add("Medium");
                        variantNames.add("Large");
                        ArrayList<Double> variantAdditionalPrice = new ArrayList<>();
                        variantAdditionalPrice.add(2.0);
                        variantAdditionalPrice.add(4.0);
                        variantAdditionalPrice.add(6.0);
                        String datetime = addDate.getText().toString() + " " + addTime.getText().toString();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy H:mm");
                        LocalDate parsedDateTime = LocalDate.parse(datetime, formatter);
                        Date parsedDate = Date.from(parsedDateTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                        Listings.addListing(db, fbUser, Double.parseDouble(newPrice.getText().toString()), productName.getText().toString(),
                                Integer.parseInt(minOrder.getText().toString()), 0, parsedDate, uriArrList, description.getText().toString(),
                                Double.parseDouble(oldPrice.getText().toString()), category.getSelectedItem().toString(), variantNames, variantAdditionalPrice, new CallbackAdapter() {
                            @Override
                            public void onResult(boolean isSuccess) {
                                if (isSuccess) {
                                    Toast.makeText(getApplicationContext(), "Added Listing!", Toast.LENGTH_LONG).show();
                                    Intent Main = new Intent(AddListingActivity.this, TransitionLandingActivity.class);
                                    startActivity(Main);
                                }
                            }
                        });
                    }
                });

            }
        });


    }

    MaterialCardView addNewImageButton(Uri image) {
        MaterialCardView newMatCard = new MaterialCardView(this);
        newMatCard.setStrokeWidth(0);
        LinearLayout.LayoutParams cardMatParams = new LinearLayout.LayoutParams(200, 200);
        cardMatParams.setMargins(15, 0, 0, 0);
        newMatCard.setLayoutParams(cardMatParams);
        ImageView newImage = new ImageView(this);
        newImage.setImageURI(image);

        newImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        CardView newCard = new CardView(this);
        newCard.addView(newImage);
        newCard.setCardElevation(0);
        newMatCard.addView(newCard);
        return newMatCard;
    }
}