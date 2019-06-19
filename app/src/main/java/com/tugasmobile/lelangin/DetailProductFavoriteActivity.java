package com.tugasmobile.lelangin;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tugasmobile.lelangin.database.DatabaseContract;
import com.tugasmobile.lelangin.database.ProductHelper;
import com.tugasmobile.lelangin.model.Product;
import com.tugasmobile.lelangin.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static com.tugasmobile.lelangin.database.DatabaseContract.CONTENT_URI;

public class DetailProductFavoriteActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    TextView name, owner, description;
    ImageView imageView;
    ImageButton imageButton;

    SQLiteDatabase database;

    Dialog dialog_bidding;

    TextView highest_bid;

    ProductHelper productHelper;
    boolean isFavorite = false;

    String TAG = "";

    public static String EXTRA_ID = "extra_id";
    public static String EXTRA_ID_OWNER = "extra_id_owner";
    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_PRICE = "extra_price";
    public static String EXTRA_OWNER = "extra_owner";
    public static String EXTRA_IMAGE = "extra_image";
    public static String EXTRA_DESCRIPTION = "extra_description";

    int tertinggi;

    String nameOwner, key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product_favorite);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        productHelper = new ProductHelper(this);
        productHelper.open();

        description = findViewById(R.id.detail_product_description);
        name = findViewById(R.id.detail_product_name);
        owner = findViewById(R.id.detail_product_owner);
        imageView = findViewById(R.id.detail_product_image);
        imageButton = findViewById(R.id.detail_product_favorite);

        dialog_bidding = new Dialog(this);

        dialog_bidding.setContentView(R.layout.bidding_auction);
        highest_bid = dialog_bidding.findViewById(R.id.highest_bid);

        name.setText("Nama Product: "+getIntent().getStringExtra(EXTRA_NAME));
        owner.setText("Penjual: "+getIntent().getStringExtra(EXTRA_OWNER));
        description.setText("Description: " + getIntent().getStringExtra(EXTRA_DESCRIPTION));

        Picasso.get().load(getIntent().getStringExtra(EXTRA_IMAGE)).into(imageView);

        ArrayList<Product> list = productHelper.query();

        for (int i = 0; i < list.size(); i++) {
            if (getIntent().getStringExtra(EXTRA_ID).equals(list.get(i).getId())) {
                isFavorite = true;
            }
        }

        if (isFavorite) {
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red_24dp));
        } else {
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(_ID, getIntent().getStringExtra(EXTRA_ID));
                contentValues.put(DatabaseContract.FavoriteColumns.product_name, getIntent().getStringExtra(EXTRA_NAME));
                contentValues.put(DatabaseContract.FavoriteColumns.product_owner, getIntent().getStringExtra(EXTRA_OWNER));
                contentValues.put(DatabaseContract.FavoriteColumns.product_description, getIntent().getStringExtra(EXTRA_DESCRIPTION));
                contentValues.put(DatabaseContract.FavoriteColumns.product_image, getIntent().getStringExtra(EXTRA_IMAGE));
                if (isFavorite) {
                    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                    productHelper.delete(String.valueOf(getIntent().getStringExtra(EXTRA_ID)));
                } else {
                    imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red_24dp));
                    getContentResolver().insert(CONTENT_URI, contentValues);
                }
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Product product = dataSnapshot.getValue(Product.class);
                tertinggi = product.getPrice();
                highest_bid.setText("Rp."+product.getPrice());
                name.setText("Nama Product: "+product.getName());
                owner.setText("Penjual: "+product.getOwner());
                description.setText("Description: " + product.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        FirebaseDatabase.getInstance().getReference("products").child(getIntent().getStringExtra(EXTRA_ID)).addValueEventListener(postListener);

        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        final String tes = key_user.getUid();
        key = tes;

        ValueEventListener postListenerKey = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                nameOwner = user.name;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        FirebaseDatabase.getInstance().getReference("users").child(tes).addListenerForSingleValueEvent(postListenerKey);
    }

}
