package com.example.javaandroidapp.activities;

import static android.app.ProgressDialog.show;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.health.connect.datatypes.HeightRecord;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.request.RequestOptions;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.fragments.DatePickerFragment;
import com.example.javaandroidapp.fragments.TimePickerFragment;
import com.example.javaandroidapp.modals.CategoryModel;
import com.example.javaandroidapp.modals.User;
import com.example.javaandroidapp.utils.Categories;
import com.example.javaandroidapp.utils.Images;
import com.example.javaandroidapp.utils.Listings;
import com.example.javaandroidapp.utils.Users;
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

import io.getstream.chat.android.ui.common.state.messages.Edit;

public class AddListingActivity extends AppCompatActivity {
    ImageButton addImageButton;
    ImageButton addVariantButton;
    LinearLayout addListingLayout;
    Uri image;
    ArrayList<Uri> uriArrList = new ArrayList<>();
    LinearLayout displayImageLayout;
    User user;
    ArrayList<Double> variantAdditionalPriceList = new ArrayList<>();
    ArrayList<EditText> variantAdditionalPriceInputs = new ArrayList<>();
    ArrayList<String> variantNameList = new ArrayList<>();
    ArrayList<EditText> variantNameInputs = new ArrayList<>();

    private List<String> categories = new ArrayList<String>();

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    displayImageLayout = findViewById(R.id.displayImageLayout);
                    image = o.getData().getData();
                    uriArrList.add(image);
                    Log.d("uriArr", "uriArr" + uriArrList);
//                    Glide.with(getApplicationContext()).load(image).apply(new RequestOptions().override(100, 100)).into(addImageButton);
                    displayImageLayout.addView(addNewImageButton(image));
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
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Users.getUser(db, fbUser, new CallbackAdapter() {
            @Override
            public void getUser(User new_user) {
                user = new_user;
            }
        });
        if (fbUser == null) {
            Intent notSignedIn = new Intent(AddListingActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_listing);
        // get Buttons
        addImageButton = findViewById(R.id.addImageButton);
        addVariantButton = findViewById(R.id.addVariantButton);
        addListingLayout = (LinearLayout) findViewById(R.id.addListingLayout);
        TextView addDate = findViewById(R.id.addDate);
        TextView addTime = findViewById(R.id.addTime);
        //Retrieve all the edit text to get data later when add button is pressed
        EditText productName = findViewById(R.id.addProductName);
        EditText description = findViewById(R.id.addDescription);
        EditText oldPrice = findViewById(R.id.addOldPrice);
        EditText newPrice = findViewById(R.id.addNewPrice);
        EditText minOrder = findViewById(R.id.addMinOrder);
        EditText variantName = findViewById(R.id.variantName);
        EditText additionalVariantPrice = findViewById(R.id.additionalVariantPrice);
        // store this to check later to send to db
        variantAdditionalPriceInputs.add(additionalVariantPrice);
        variantNameInputs.add(variantName);
        Spinner category = findViewById(R.id.addCategory);
        // For processing animation
        LinearLayout addListingButton = findViewById(R.id.addListingButton);
        ProgressBar loadSpinner = findViewById(R.id.loadSpinner);
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        // to add image and dynamically generate new image
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        // to add new variant edit text
        addVariantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout variantLayout = findViewById(R.id.variantLayout);
                //generate new variant edit text and add the text to button
                TextView newVariantName = new TextView(getApplicationContext());
                LinearLayout.LayoutParams newVariantNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newVariantNameParams.setMargins(0, 30, 0, 0);
                newVariantName.setLayoutParams(newVariantNameParams);
                newVariantName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                newVariantName.setText("Variation Name:");
                newVariantName.setTextColor(Color.BLACK);
//                newVariantName.setTypeface(Typeface.defaultFromStyle(R.font.interfont));
                EditText newVariantNameInput = new EditText(getApplicationContext());
                int widthNameInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                int heightNameInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams newNameInputParams = new LinearLayout.LayoutParams(widthNameInDp, heightNameInDp);
                newVariantNameInput.setLayoutParams(newNameInputParams);
                newVariantNameInput.setEllipsize(TextUtils.TruncateAt.START);
                newVariantNameInput.setGravity(Gravity.CENTER);
                newVariantNameInput.setHint("e.g Small/Medium");
                newVariantNameInput.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
                newVariantNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                newVariantNameInput.setTextSize(14);
                TextView newVariantAdditionalPrice = new TextView(getApplicationContext());
                LinearLayout.LayoutParams newVariantPriceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newVariantPriceParams.setMargins(0, 15, 0, 0);
                newVariantAdditionalPrice.setLayoutParams(newVariantPriceParams);
                newVariantAdditionalPrice.setTextSize(15);
                newVariantAdditionalPrice.setText("Additional Price:");
                newVariantAdditionalPrice.setTextColor(Color.BLACK);
//                newVariantAdditionalPrice.setTypeface(Typeface.defaultFromStyle(R.font.interfont));
                EditText newVariantAdditionalPriceInput = new EditText(getApplicationContext());
                int widthPriceInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                int heightPriceInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams newVariantInputParams = new LinearLayout.LayoutParams(widthPriceInDp, heightPriceInDp);
                newVariantAdditionalPriceInput.setLayoutParams(newVariantInputParams);
                newVariantAdditionalPriceInput.setEllipsize(TextUtils.TruncateAt.START);
                newVariantAdditionalPriceInput.setGravity(Gravity.CENTER);
                newVariantAdditionalPriceInput.setHint("e.g 10.00");
                newVariantAdditionalPriceInput.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
                newVariantAdditionalPriceInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                newVariantAdditionalPriceInput.setTextSize(14);
                variantAdditionalPriceInputs.add(newVariantAdditionalPriceInput);
                variantNameInputs.add(newVariantNameInput);
                variantLayout.addView(newVariantName);
                variantLayout.addView(newVariantNameInput);
                variantLayout.addView(newVariantAdditionalPrice);
                variantLayout.addView(newVariantAdditionalPriceInput);
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
                        addDate.setText(String.format("%02d/%02d/%04d", day, (month % 13) + 1, year));
                    }
                }, year, month, day
                );
                newDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
                addListingButton.setVisibility(View.GONE);
                loadSpinner.setVisibility(View.VISIBLE);
                boolean variantNameEmpty = false;
                boolean variantPriceEmpty = false;
                for (EditText input : variantNameInputs) {
                    if (TextUtils.isEmpty(input.getText())){
                        variantNameEmpty = true;
                    }
                }
                for (EditText input : variantAdditionalPriceInputs){
                    if (TextUtils.isEmpty(input.getText())){
                        variantPriceEmpty = true;
                    }
                }

                if (uriArrList.isEmpty()) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please add at least one listing image.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productName.getText())) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please input listing name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(oldPrice.getText())) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please add valid original price.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newPrice.getText())) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please add valid discounted price.", Toast.LENGTH_SHORT).show();
                } else if (variantNameEmpty) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please add at least one variant.", Toast.LENGTH_SHORT).show();
                } else if (variantPriceEmpty) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please add additional variant price.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(minOrder.getText())) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please input valid minimum order amount", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(addDate.getText())) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please input valid expiry date.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(addTime.getText())) {
                    addListingButton.setVisibility(View.VISIBLE);
                    loadSpinner.setVisibility(View.GONE);
                    Toast.makeText(AddListingActivity.this, "Please add valid expiry time.", Toast.LENGTH_SHORT).show();
                } else {

                    Images.addImages(uriArrList, storageRef, new CallbackAdapter() {
                        @Override
                        public void getArrayListOfString(ArrayList<String> imageList) {
                            // if null it will just return empty variant list
                            for (EditText input : variantNameInputs) {
                                if (input.getText() != null) {
                                    variantNameList.add(input.getText().toString());
                                }
                            }
                            for (EditText input : variantAdditionalPriceInputs) {
                                if (input.getText() != null) {
                                    variantAdditionalPriceList.add(Double.valueOf(input.getText().toString()));
                                }
                            }
                            String datetime = addDate.getText().toString() + " " + addTime.getText().toString();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm");
                            LocalDate parsedDateTime = LocalDate.parse(datetime, formatter);
                            Date parsedDate = Date.from(parsedDateTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                            Listings.addListing(db, user, Double.parseDouble(newPrice.getText().toString()), productName.getText().toString(),
                                    Integer.parseInt(minOrder.getText().toString()), parsedDate, imageList, description.getText().toString(),
                                    Double.parseDouble(oldPrice.getText().toString()), category.getSelectedItem().toString(), variantNameList, variantAdditionalPriceList, new CallbackAdapter() {
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

            }
        });


    }

    MaterialCardView addNewImageButton(Uri image) {
        MaterialCardView newMatCard = new MaterialCardView(this);
        newMatCard.setStrokeWidth(0);
        LinearLayout.LayoutParams cardMatParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardMatParams.setMargins(10, 20, 30, 0);
        newMatCard.setLayoutParams(cardMatParams);
        ImageView newImage = new ImageView(this);
        newImage.setImageURI(image);
        newImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        newImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageView removeButton = new ImageView(this);
        removeButton.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.close_image));
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(60, 60);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        buttonParams.setMargins(0, 10, 0, 0);
        removeButton.setLayoutParams(buttonParams);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the index of the image URI in the list
                int index = uriArrList.indexOf(image);
                if (index != -1) {
                    // Remove the image URI from the list
                    uriArrList.remove(index);
                    // Remove both the image view and the button from the layout
                    displayImageLayout.removeView(newMatCard);
                    displayImageLayout.removeView(removeButton);
                }
            }
        });
        RelativeLayout newCard = new RelativeLayout(this);
        RelativeLayout.LayoutParams newCardParams = new RelativeLayout.LayoutParams(200, 200);
        newCard.setLayoutParams(newCardParams);
        newCard.addView(newImage);
        newCard.addView(removeButton);
        newMatCard.addView(newCard);
        return newMatCard;
    }
}