package be.hicham.v2_nhi_shop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityAddArticlesBinding;
import be.hicham.v2_nhi_shop.databinding.ActivityDetailArticleBinding;
import be.hicham.v2_nhi_shop.fragment.MapFragment;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.utilities.Constants;

public class DetailArticleActivity extends AppCompatActivity {

    String wheaterValue = "";
    private ActivityDetailArticleBinding binding;
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // retrieve article from mainActivity
        article = (Article) getIntent().getSerializableExtra(Constants.KEY_TITLE_ARTICLE);
        setListeners();

        //////// MAPS FRAGMENT CREATION///////////////
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapView,fragment).commit();

    }

    /// inisialiasation
    private void setListeners() {
        binding.imageViewArticle.setImageBitmap(getBitmapFromEncodedString(article.getImage())); // image
        binding.textViewDetailTitle.setText(article.getTitle());
        binding.textViewDetailPrice.setText(article.getPrice() + " €");
        binding.textViewDetailDate.setText(article.getDatePosted());
        binding.textViewDetailCall.setText("0465754813");
        System.out.println("before => " );
        getWeather();
        //binding.textViewDetailDescription.setText(article.getDescription() + "\n" + wheaterValue);

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