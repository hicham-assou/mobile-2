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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityAddArticlesBinding;
import be.hicham.v2_nhi_shop.databinding.ActivityDetailArticleBinding;
import be.hicham.v2_nhi_shop.fragment.MapFragment;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.utilities.Constants;

public class DetailArticleActivity extends AppCompatActivity {

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

    private void setListeners() {
        binding.imageViewArticle.setImageBitmap(getBitmapFromEncodedString(article.getImage())); // image
        binding.textViewDetailTitle.setText(article.getTitle());
        binding.textViewDetailPrice.setText(article.getPrice() + " €");
        binding.textViewDetailDate.setText(article.getDatePosted());
        binding.textViewDetailCall.setText("0465754813");
        binding.textViewDetailDescription.setText(article.getDescription());

    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}