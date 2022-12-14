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

    private String weatherValue = "";
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

        loadArticleDetails();
        loadSellerDetails();
        init();
        setListeners();

        //////// MAPS FRAGMENT CREATION///////////////
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView,fragment).commit();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_ARTICLE, article);
        fragment.setArguments(bundle);
    }

    //Init layout data
    private void init() {
        binding.imageViewArticle.setImageBitmap(getBitmapFromEncodedString(article.getImage())); // image
        binding.textViewDetailTitle.setText(article.getTitle());
        binding.textViewDetailPrice.setText(article.getPrice() + " €");
        binding.textViewDetailDate.setText(article.getDatePosted());

        //If it's my article, i can't myself a message
        if (article.getSellerUsername().equals(preferenceManager.getString(Constants.KEY_USERNAME))){
            binding.textViewDetailMessage.setVisibility(View.GONE);
        }
        getWeather();

    }

    private void loadArticleDetails() {
        article = (Article) getIntent().getSerializableExtra(Constants.KEY_ARTICLE);
    }

    private void loadSellerDetails() {

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

        //binding.textViewDetailDescription.setText(article.getDescription() + "\n" + wheaterValue);

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
        String city = "Bruxelles";
        double lat= 50.850346; // faut recup de la db//////////////////////////////////////
        double lng= 4.351721; // faut recup de la db//////////////////////////////////////
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lng+"&appid=9211be69198a3f97d01c865ada5360e4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject main = jsonObject.getJSONObject("main"); // main car les info de la temperature se trouve la
                    int temperature = (int)(main.getDouble("temp")-273.15); // -273.15 pour la transformer en C°

                    int humidity = (int)(main.getDouble("humidity"));

                    weatherValue = "temp = " + temperature + "C°, Humidity = " + humidity + "%";
                    System.out.println("temperature => " +weatherValue);
                    binding.textViewDetailDescription.setText(article.getDescription() + "\n" + weatherValue);
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
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}