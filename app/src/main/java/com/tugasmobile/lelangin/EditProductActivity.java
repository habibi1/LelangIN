package com.tugasmobile.lelangin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tugasmobile.lelangin.database.ProductHelper;
import com.tugasmobile.lelangin.model.Product;
import com.tugasmobile.lelangin.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProductActivity extends AppCompatActivity {

    String TAG = "";
    UploadTask uploadTask;
    String nameOwner, idOwner;
    String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl;
    private ImageButton addPhoto;
    private EditText name, price, description;
    private Button bAdd;
    private View mProgressView;
    private View mAddFormView;
    private String thumb_downloadUrl;

    CardView galery, camera;

    Dialog select_method;

    private static final int REQUEST_CAPTURE_IMAGE = 100;

    byte[] thumb_byte;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference PostsImageReference;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String imageFilePath;

    static final int REQUEST_TAKE_PHOTO = 1;
    ImageButton imageView;
    ImageButton imageButton;

    SQLiteDatabase database;

    Dialog dialog_bidding;

    Button setBidding;

    TextView highest_bid;

    ProductHelper productHelper;
    boolean isFavorite = false;


    public static String EXTRA_ID = "extra_id";
    public static String EXTRA_ID_OWNER = "extra_id_owner";
    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_PRICE = "extra_price";
    public static String EXTRA_OWNER = "extra_owner";
    public static String EXTRA_IMAGE = "extra_image";
    public static String EXTRA_DESCRIPTION = "extra_description";

    int tertinggi;

    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        name = findViewById(R.id.add_product_name);
        price = findViewById(R.id.add_product_price);
        description = findViewById(R.id.add_product_description);
        addPhoto = findViewById(R.id.add_product_image);
        bAdd = findViewById(R.id.add_product_button);
        mAddFormView = findViewById(R.id.add_form);
        mProgressView = findViewById(R.id.add_progress);

        name.setText(getIntent().getStringExtra(EXTRA_NAME));
        price.setText(getIntent().getStringExtra(EXTRA_PRICE));
//        owner.setText(getIntent().getStringExtra(EXTRA_OWNER));
        description.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));

        Picasso.get().load(getIntent().getStringExtra(EXTRA_IMAGE)).into(addPhoto);


        select_method = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        PostsImageReference = FirebaseStorage.getInstance().getReference();

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopUp();
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

    }

    public void ShowPopUp() {
        select_method.setContentView(R.layout.choose_app);

        galery = select_method.findViewById(R.id.galery);
        camera = select_method.findViewById(R.id.camera);

        galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_method.dismiss();
                OpenGallery();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_method.dismiss();
                openCameraIntent();
            }
        });

        select_method.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        select_method.show();
    }

    private void openCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tugasmobile.lelangin.images",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void addProduct() {
        name.setError(null);
        price.setError(null);
        description.setError(null);

        String textName = name.getText().toString();
        String textPrice = price.getText().toString();
        String textDescription = description.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(textName)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        if (TextUtils.isEmpty(textPrice)) {
            price.setError(getString(R.string.error_field_required));
            focusView = price;
            cancel = true;
        }

        if (TextUtils.isEmpty(textDescription)) {
            description.setError(getString(R.string.error_field_required));
            focusView = description;
            cancel = true;
        }

        if (imageUri == null) {
            cancel = true;
            Toast.makeText(this, "Harap lampirkan Foto!", Toast.LENGTH_SHORT).show();
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            StoringImageToFirebaseStorage();

            FirebaseUser user = mAuth.getCurrentUser();


        }
    }

    private void Save() {
        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        String tes = key_user.getUid();
        idOwner = tes;

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                nameOwner = user.name;
                SaveData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        FirebaseDatabase.getInstance().getReference("users").child(tes).addListenerForSingleValueEvent(postListener);

    }

    private void SaveData() {
        String key = FirebaseDatabase.getInstance().getReference("products").push().getKey();
        Product newProduct = new Product(getIntent().getStringExtra(EXTRA_ID), name.getText().toString(), downloadUrl, Integer.parseInt(price.getText().toString()), nameOwner, idOwner, description.getText().toString(), "-", "-");
        mDatabase.child("products").child(getIntent().getStringExtra(EXTRA_ID)).setValue(newProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                Intent intent = new Intent(EditProductActivity.this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(EditProductActivity.this, "Product Edited.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void StoringImageToFirebaseStorage() {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = PostsImageReference.child("Post Image").child(postRandomName + ".jpg");

        uploadTask = filepath.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    downloadUrl = downloadUri.toString();
                    Save();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void OpenGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();

            addPhoto.setImageURI(imageUri);
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            File f = new File(imageFilePath);
            imageUri = Uri.fromFile(f);

            addPhoto.setImageURI(imageUri);


        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
