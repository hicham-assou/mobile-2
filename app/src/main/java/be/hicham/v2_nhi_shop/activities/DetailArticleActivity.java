package be.hicham.v2_nhi_shop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityDetailArticleBinding;
import be.hicham.v2_nhi_shop.fragment.MapFragment;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.models.User;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class DetailArticleActivity extends AppCompatActivity {

    private String wheaterValue = "";
    private ActivityDetailArticleBinding binding;
    private Article article;
    private User user;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        // retrieve article from mainActivity
        article = (Article) getIntent().getSerializableExtra(Constants.KEY_ARTICLE);
        setSeller();
        init();
        setListeners();


        //////// MAPS FRAGMENT CREATION///////////////
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView,fragment).commit();

    }

    private void setSeller() {
        user = new User();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null){
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                            if (article.getSellerUsername().equals(queryDocumentSnapshot.getString(Constants.KEY_USERNAME))){
                                user.setUsername(queryDocumentSnapshot.getString(Constants.KEY_USERNAME));
                                user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                                user.setId(queryDocumentSnapshot.getId());
                            }
                        }
                    } else {
                        showToast("Can't retrieve seller");
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /// initialisation
    private void init() {
        binding.imageViewArticle.setImageBitmap(getBitmapFromEncodedString(article.getImage())); // image
        binding.textViewDetailTitle.setText(article.getTitle());
        binding.textViewDetailPrice.setText(article.getPrice() + " €");
        binding.textViewDetailDate.setText(article.getDatePosted());
        binding.textViewDetailCall.setText(user.getPhoneNumber());
        System.out.println(preferenceManager.getString(Constants.KEY_USERNAME) + "ATESSTT -------------------------- " + article.getSellerUsername());
        System.out.println( article.getSellerUsername() == preferenceManager.getString(Constants.KEY_USERNAME));

        if (article.getSellerUsername().equals(preferenceManager.getString(Constants.KEY_USERNAME))){
            binding.textViewDetailMessage.setVisibility(View.GONE);
        }
    }
    private void setListeners() {

        binding.textViewDetailMessage.setOnClickListener(v-> {
            if (checkSession()){
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, user);
                intent.putExtra(Constants.KEY_ARTICLE, article);
                startActivity(intent);
                finish();
            }
        });

        getWeather();
        //binding.textViewDetailDescription.setText(article.getDescription() + "\n" + wheaterValue);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private boolean checkSession() {
        if (preferenceManager.getString(Constants.KEY_USER_ID) == null){
            startActivity(new Intent(DetailArticleActivity.this, LoginActivity.class));
            return false;
        } else {
            return true;
        }
    }

    private void getWeather() {
        System.out.println("getWeather => " );
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Bruxelles&appid=9211be69198a3f97d01c865ada5360e4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject main = jsonObject.getJSONObject("main"); // main car les info de la temperature se trouve la
                    int temperature = (int)(main.getDouble("temp")-273.15); // -273.15 pour la transformer en C°

                    int humidity = (int)(main.getDouble("humidity"));

                    wheaterValue = "temp = " + temperature + "C°, Humidity = " + humidity + "%";
                    System.out.println("temperature => " +wheaterValue);
                    binding.textViewDetailDescription.setText(article.getDescription() + "\n" + wheaterValue);
                    //JSONArray weather = jsonObject.getJSONArray("weather");// pour le changement d'icon, il se trouve dans une array dans le json


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailArticleActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        System.out.println("temperature 2222 => " +wheaterValue);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}