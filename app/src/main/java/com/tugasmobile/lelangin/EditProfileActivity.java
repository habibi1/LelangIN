package com.tugasmobile.lelangin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.tugasmobile.lelangin.fragment.ProfileFragment;
import com.tugasmobile.lelangin.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    String TAG="";
    UploadTask uploadTask;
    String nameOwner;
    String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl;

    private View mProgressView;
    private View mEditFormView;

    private ActionBar toolbar;

    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_EMAIL = "extra_email";
    public static String EXTRA_IMAGES = "extra_images";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference PostsImageReference;


    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    EditText edit_name, edit_email;
    ImageView imageView;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = getSupportActionBar();

        mEditFormView = findViewById(R.id.form_edit);
        mProgressView = findViewById(R.id.add_progress);

        imageView = findViewById(R.id.edit_image);
        edit_name = findViewById(R.id.edit_name);
        btnSave = findViewById(R.id.edit_save_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        PostsImageReference = FirebaseStorage.getInstance().getReference();

        edit_name.setText(getIntent().getStringExtra(EXTRA_NAME));
        Picasso.get().load(getIntent().getStringExtra(EXTRA_IMAGES)).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
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

        final StorageReference filepath = PostsImageReference.child("Post Image").child(postRandomName+".jpg");

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
                    SaveDataWithImage();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void SaveData(){
        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        String tes = key_user.getUid();

        mDatabase.child("users").child(tes).child("name").setValue(edit_name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finishAffinity();
                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SaveDataWithImage(){
        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        String tes = key_user.getUid();

        DatabaseReference hopperRef = mDatabase.child("users").child(tes);
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("name", edit_name.getText().toString());
        hopperUpdates.put("photo", downloadUrl);

        hopperRef.updateChildren(hopperUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finishAffinity();
                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Save() {
        edit_name.setError(null);

        String textName = edit_name.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(textName)) {
            edit_name.setError(getString(R.string.error_field_required));
            focusView = edit_name;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            if (imageUri!=null){
                StoringImageToFirebaseStorage();
            } else {
                SaveData();
            }

            FirebaseUser user = mAuth.getCurrentUser();


        }
    }

    private void OpenGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();

            imageView.setImageURI(imageUri);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
