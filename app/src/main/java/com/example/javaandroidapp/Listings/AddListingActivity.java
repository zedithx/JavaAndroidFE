package com.example.javaandroidapp;
import java.util.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public AddListingActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button addListingbutton = findViewById(R.id.addListing_button);
        addListingbutton.setOnClickListener(new View.OnClickListener()){
                                           @Override
                                           public void onClick(View v) {
                                               Intent Main = new Intent(addListingActivity.this, LandingActivity.class);
                                               startActivity(Main);
                                               TestAdd.addListing(db);

}
        }
    }
}

FirebaseFirestore db = FirebaseFirestore.getInstance();
CollectionReference addListing = db.collection("Listings");

public class TestAddListing{
    public static String addListing(FirebaseFirestore db){
        Map<String, Object> listing1 = new HashMap<>();
        listing1.put("name", "kangol_tote_bag");
        listing1.put("description", "I am a cool product!");
        listing1.put("price", "69.69");
        listing1.put("expiry", "12/12/2024");
        listing1.put("currentOrder", 60 );
        listing1.put("minOrder", 60)
        listing1.put("image", "image.url");
        db.collection("Listings").add(listing1);
        return "Successful";
    }
}

public class AddListing {
    private String name;
    private double price;
    private int currentOrder;
    private int minQuantity;
    private String expiry;
    private String image;
    private String description;
    private Map<String, Object> category = new HashMap<>();
}
    



    public addListing(){}
    public addListing(String name, String description, double price, int currentOrder, int minQuantity, String expiry, String image){       
    }

    public String getName() {
        return name;
    }

    public String description(){
        return description;
    }

    public void getDescription(String description){
        this.description = description;
    }

    public void setCategory(ArrayList<String> category){
        for (String:c:category){
            this.category.put(c,1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

AddListing addListing = new AddListing("kangol_tote_bag", "This is a cool product", 69.69, 69,69, "16/03/2024", "image_url");
db.collection("Listings").document("iamcool").set(addListing);


    

