package be.hicham.v2_nhi_shop.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityAddArticlesBinding;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class AddArticlesActivity extends AppCompatActivity implements LocationListener {

    private String encodedImage;
    private ActivityAddArticlesBinding binding;
    private PreferenceManager preferenceManager;
    private ProgressDialog progressDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    private Button button_localisation;
    // initialise LocationManager class
    private LocationManager locationManager;
    private String address;

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
            if(checkAndRequestPermissions(AddArticlesActivity.this)){
                chooseImage(AddArticlesActivity.this);
            }
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

        // ADRESSE BOUTTON
        binding.buttonLocalisation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Runtime permissions
                if (ContextCompat.checkSelfPermission(AddArticlesActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                        !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddArticlesActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },100);
                }
                getLocation();
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        // LocationManager class provides the facility to get latitude and longitude coordinates of current location
        try {
            locationManager =(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // foto ou gallery
    // function to check permission
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                chooseImage(AddArticlesActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        encodedImage = encodeImage(selectedImage);
                        binding.textAddImage.setVisibility(View.GONE);
                        binding.imageButton.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri imageUri = data.getData();
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
                    break;
            }
        }
    }
    // fin foto ou gallery

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

    // requirements pour pouvoir ajouter un article (titre, prix, ...)
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

    // si il est pas connecté alors on le renvoie a l'activity login
    private void checkSession() {

        if (preferenceManager.getString(Constants.KEY_USER_ID) == null) {
            startActivity(new Intent(AddArticlesActivity.this, LoginActivity.class));
        }

    }

    // ajouter des article a la DB
    private void addArticle() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> article = new HashMap<>();
        article.put(Constants.KEY_TITLE_ARTICLE, binding.titleArticle.getText().toString());
        article.put(Constants.KEY_DESCRIPTION_ARTICLE, binding.description.getText().toString());
        article.put(Constants.KEY_USERNAME, preferenceManager.getString(Constants.KEY_USERNAME));
        article.put(Constants.KEY_LOCALISATION_ARTICLE, binding.localisation.getText().toString());
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
                    preferenceManager.putString(Constants.KEY_LOCALISATION_ARTICLE, binding.localisation.getText().toString());
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
    //permet de redimensionner l'image
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap prewiewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        prewiewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date);
    }
    //affiche toast
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    // implement LocationListener and ovverride all its abstracts methods
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Test to see lat and long
        try {
            //Gecoder Class for refers to transform street adress or any adress into lat and long
            Geocoder geocoder = new Geocoder(this,Locale.getDefault());
            // Adress class helps in fetching the street adresse,locality,city,country etc
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            address = addresses.get(0).getAddressLine(0);
            binding.localisation.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
    /// si il appuis sur retour il revient a la page home(mainActivity)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}