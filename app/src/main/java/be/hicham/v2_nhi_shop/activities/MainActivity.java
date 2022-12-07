package be.hicham.v2_nhi_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.adapter.ArticleAdapter;
import be.hicham.v2_nhi_shop.adapter.UserAdapter;
import be.hicham.v2_nhi_shop.databinding.ActivityMainBinding;
import be.hicham.v2_nhi_shop.fragment.MapFragment;
import be.hicham.v2_nhi_shop.listeners.ArticleViewListener;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.models.User;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity implements ArticleViewListener,
        AdapterView.OnItemSelectedListener {
        String[] sorting = { "Sort By", "Price", "Localisation", "Date"};

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
        getArticles();
        sayYes();
    }

    private void sayYes() {
        System.out.println("yessss");
    }

    private void getArticles() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ARTICLES)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Article> articles = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Article article = new Article();
                            article.setTitle(queryDocumentSnapshot.getString(Constants.KEY_TITLE_ARTICLE));
                            article.setDescription(queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION_ARTICLE));
                            article.setSeller(queryDocumentSnapshot.getString(Constants.KEY_USERNAME));
                            article.setLocalisation(queryDocumentSnapshot.getString(Constants.KEY_LOCALISATION_ARTICLE));
                            article.setDateTime(getReadableDateTime(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP_ARTICLE)));
                            article.setDateObject(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP_ARTICLE));
                            article.setPrice(Double.parseDouble(queryDocumentSnapshot.getString(Constants.KEY_PRICE_ARTICLE)));
                            article.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE_ARTICLE));
                            article.setId(queryDocumentSnapshot.getId());
                            articles.add(article);
                            System.out.println(article.getDatePosted());
                        }
                        if (articles.size() > 0) {
                            ArticleAdapter articleAdapter = new ArticleAdapter(articles, this);
                            binding.articleRecyclerView.setAdapter(articleAdapter);
                            binding.articleRecyclerView.setVisibility(View.VISIBLE);

                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No articles"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setListeners() {
        binding.searchBar.setOnClickListener(v -> binding.imageDelete.setVisibility(View.VISIBLE) );
        binding.imageDelete.setOnClickListener(v -> { binding.searchBar.getText().clear();});
        binding.spinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,sorting);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinner.setAdapter(aa);

        binding.bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_addarticles:
                        startActivity(new Intent(getApplicationContext(), AddArticlesActivity.class));
                        overridePendingTransition(0, 0);
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
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleViewClicked(Article article) {
        Intent intent = new Intent(getApplicationContext(), DetailArticleActivity.class);
        showToast("Click on " + article.getTitle());
        intent.putExtra(Constants.KEY_TITLE_ARTICLE, article);
        startActivity(intent);
        finish();
    }


    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date);
    }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            //Toast.makeText(getApplicationContext(),sorting[position] , Toast.LENGTH_LONG).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }