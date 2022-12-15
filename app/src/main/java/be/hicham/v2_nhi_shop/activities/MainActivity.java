package be.hicham.v2_nhi_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.adapter.ArticleAdapter;
import be.hicham.v2_nhi_shop.databinding.ActivityMainBinding;
import be.hicham.v2_nhi_shop.listeners.ArticleViewListener;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity implements ArticleViewListener{


    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private ArticleAdapter articleAdapter;
    List<Article> articlesList = null;
    // liste articles qui seront afficher dans la page d'acceuil (en fonction de la barre de recherche)
    List<Article> articlesListToShow = null;
    private static final String[] sortingBy = { "Ascending date", "Descending date", "Ascending price", "Descending price"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        setListeners();
        getArticles();
        showArticlesSortedBy();
        showArticlesAccordingToSearchBar();


    }

    private void showArticlesAccordingToSearchBar() {
        // bar de recherche
        binding.searchText.setOnClickListener(v -> {
            // si rien n'est recherché alors on affiche tous les articles
            if (binding.searchBar.getText().equals(""))
                showAllArticles("Ascending date");
            else
                showSearchArticle(binding.searchBar.getText().toString().toLowerCase());
        });
    }

    private void showArticlesSortedBy() {
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter sorting = new ArrayAdapter(this,android.R.layout.simple_spinner_item, sortingBy);
        sorting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinner.setAdapter(sorting);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedSort = adapterView.getItemAtPosition(position).toString();

                // afficher les articles selon le tri
                showAllArticles(selectedSort);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void showAllArticles(String sortedBy) {
        if (articlesList != null) {
            if (sortedBy.equals("Ascending date"))
                Collections.sort(articlesList, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            else if (sortedBy.equals("Descending date"))
                Collections.sort(articlesList, (obj2, obj1) -> obj1.dateObject.compareTo(obj2.dateObject));
            else if (sortedBy.equals("Ascending price")){
                // Sort the list of article by price in ascending order
                Collections.sort(articlesList, new Comparator<Article>() {
                    @Override
                    public int compare(Article a1, Article a2) {
                        return (int) (a1.getPrice() - a2.getPrice());
                    }
                });
            }
            else{
                // Sort the list of article by price in descending order
                Collections.sort(articlesList, new Comparator<Article>() {
                    @Override
                    public int compare(Article a1, Article a2) {
                        return (int) (a2.getPrice() - a1.getPrice());
                    }
                });

            }

            ArticleAdapter articleAdapter = new ArticleAdapter(articlesList, this);
            binding.articleRecyclerView.setAdapter(articleAdapter);
            binding.articleRecyclerView.setVisibility(View.VISIBLE);
            binding.textErrorMessage.setVisibility(View.INVISIBLE);

        } else {
            showErrorMessage();
        }
    }

    private void showSearchArticle(String searchText) {
        articlesListToShow = new ArrayList<>();
        for (Article article: articlesList) {
            if (article.getTitle().toLowerCase().contains(searchText)){
                articlesListToShow.add(article);
            }
        }
        if (articlesListToShow.size() > 0) {
            ArticleAdapter articleAdapter = new ArticleAdapter(articlesListToShow, this);
            binding.articleRecyclerView.setAdapter(articleAdapter);
            binding.articleRecyclerView.setVisibility(View.VISIBLE);
            binding.textErrorMessage.setVisibility(View.INVISIBLE);
        } else {
            showErrorMessage();
            binding.articleRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    // afficher tous les articles a partir de la firebase
    private void getArticles() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ARTICLES)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        articlesList = new ArrayList<>();
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
                            articlesList.add(article);
                        }
                        showAllArticles("Ascending date");
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
    // si il appuis sur retour il quitte l'app
    @Override
    public void onBackPressed() {
        Intent close = new Intent(Intent.ACTION_MAIN);
        close.addCategory(Intent.CATEGORY_HOME);
        close.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(close);
    }

}