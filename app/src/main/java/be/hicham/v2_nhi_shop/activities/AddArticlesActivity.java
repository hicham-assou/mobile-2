package be.hicham.v2_nhi_shop.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityAddArticlesBinding;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class AddArticlesActivity extends AppCompatActivity {

    private String encodedImage;
    private ActivityAddArticlesBinding binding;
    private PreferenceManager preferenceManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddArticlesBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        checkSession();
        setListeners();
    }

    private void setListeners() {
        binding.imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            pickImage.launch(intent);
        });

        //// mettre la navigation bar qu'on vois en bas de l'écran /////
        binding.bottomNavigationView.setSelectedItemId(R.id.navigation_addarticles);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_addarticles:
                        return true;
                    case R.id.navigation_message:
                        startActivity(new Intent(getApplicationContext(), MessageryActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.navigation_account:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });
        ///// nav bar fin /////

        binding.buttonAddArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInput()) {
                    addArticle();
                }
            }
        });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageButton.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    ///// requirements pour pouvoir ajouter un article (titre, prix, ...)
    private boolean isValidInput() {

        if (binding.titleArticle.getText().toString().trim().isEmpty()) {
            binding.titleArticle.setError("Enter title!");
            binding.titleArticle.requestFocus();
            return false;
        } else if (binding.description.getText().toString().trim().isEmpty()) {
            binding.description.setError("Description is required!");
            binding.description.requestFocus();
            return false;
        } else if (binding.price.getText().toString().trim().isEmpty()) {
            binding.price.setError("Enter price!");
            binding.price.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    //// si il est pas co alors on le renvoie a l'activity login
    private void checkSession() {

        if (preferenceManager.getString(Constants.KEY_USER_ID) == null) {
            startActivity(new Intent(AddArticlesActivity.this, LoginActivity.class));
        }

    }

    /// ajouter des article a la DB du coup a l'application
    private void addArticle() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> article = new HashMap<>();
        article.put(Constants.KEY_TITLE_ARTICLE, binding.titleArticle.getText().toString());
        article.put(Constants.KEY_DESCRIPTION_ARTICLE, binding.description.getText().toString());
        article.put(Constants.KEY_USERNAME, preferenceManager.getString(Constants.KEY_USERNAME));
        article.put(Constants.KEY_LOCALISATION_ARTICLE, "Molenbeek Saint-Jean");
        article.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        article.put(Constants.KEY_PRICE_ARTICLE, binding.price.getText().toString());
        article.put(Constants.KEY_IMAGE_ARTICLE, encodedImage);
        article.put(Constants.KEY_TIMESTAMP_ARTICLE, new Date());
        database.collection(Constants.KEY_COLLECTION_ARTICLES)
                .add(article)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putString(Constants.KEY_ARTICLE_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_TITLE_ARTICLE, binding.titleArticle.getText().toString());
                    preferenceManager.putString(Constants.KEY_DESCRIPTION_ARTICLE, binding.description.getText().toString());
                    preferenceManager.putString(Constants.KEY_USERNAME, preferenceManager.getString(Constants.KEY_USERNAME));
                    preferenceManager.putString(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                    preferenceManager.putString(Constants.KEY_LOCALISATION_ARTICLE, "Molenbeek Saint-Jean");
                    preferenceManager.putString(Constants.KEY_TIMESTAMP_ARTICLE, getReadableDateTime(new Date()));
                    preferenceManager.putString(Constants.KEY_PRICE_ARTICLE, binding.price.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE_ARTICLE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });

    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    ///// méthodes nécesaires a addArticle()

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap prewiewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        prewiewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date);
    }
}