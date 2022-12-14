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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

public class MainActivity extends AppCompatActivity implements ArticleViewListener{


    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<Article> articles;
    private ArticleAdapter articleAdapter;
    private static final String[] sortingBy = { "Sort By Date", "Sort By Price", "Sort By Localisation"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        setListeners();
        getArticles();
    }


    private void getArticles() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ARTICLES)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        articles = new ArrayList<>();
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

                        Collections.sort(articles, (obj2, obj1) -> obj1.dateObject.compareTo(obj2.dateObject));

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
        binding.imageDelete.setOnClickListener(v -> { binding.searchBar.getText().clear(); binding.imageDelete.setVisibility(View.INVISIBLE);});

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter sorting = new ArrayAdapter(this,android.R.layout.simple_spinner_item, sortingBy);
        sorting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinner.setAdapter(sorting);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedSort = adapterView.getItemAtPosition(position).toString();

                if (selectedSort.equals("Sort By Localisation")){
                    Collections.sort(articles, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
                    articleAdapter.notifyDataSetChanged();
                    startActivity(getIntent());

                } else if (selectedSort.equals("Sort By Price")){
                    Collections.sort(articles, (obj2, obj1) -> obj1.dateObject.compareTo(obj2.dateObject));
                    startActivity(getIntent());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //// nav bar
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

    /// test to show article that we clcked on
    @Override
    public void onArticleViewClicked(Article article) {
        Intent intent = new Intent(getApplicationContext(), DetailArticleActivity.class);
        intent.putExtra(Constants.KEY_ARTICLE, article);
        startActivity(intent);
        finish();
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(date);
    }
    /// si il appuis sur retour il revient a la page home(mainActivity)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}