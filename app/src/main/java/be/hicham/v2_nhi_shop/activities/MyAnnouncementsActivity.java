package be.hicham.v2_nhi_shop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import be.hicham.v2_nhi_shop.adapter.AnnouncementAdapter;
import be.hicham.v2_nhi_shop.adapter.ArticleAdapter;
import be.hicham.v2_nhi_shop.databinding.ActivityMyAnnouncementsBinding;
import be.hicham.v2_nhi_shop.databinding.ActivityProfileBinding;
import be.hicham.v2_nhi_shop.listeners.ArticleViewListener;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.models.User;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class MyAnnouncementsActivity extends AppCompatActivity implements ArticleViewListener {
    private ActivityMyAnnouncementsBinding binding;
    private PreferenceManager preferenceManager;
    String userConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMyAnnouncementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userConnected = preferenceManager.getString(Constants.KEY_USER_ID);
        getArticles(userConnected);
    }

    private void getArticles(String user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ARTICLES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Article> articles = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (user.equals(queryDocumentSnapshot.getString(Constants.KEY_USER_ID))){
                                Article article = new Article();
                                article.setTitle(queryDocumentSnapshot.getString(Constants.KEY_TITLE_ARTICLE));
                                article.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE_ARTICLE));
                                article.setPrice(Double.parseDouble(queryDocumentSnapshot.getString(Constants.KEY_PRICE_ARTICLE)));
                                article.setId(queryDocumentSnapshot.getId());
                                articles.add(article);
                            }

                        }
                        if (articles.size() > 0) {
                            AnnouncementAdapter announcementAdapter = new AnnouncementAdapter(articles, this);
                            binding.articleRecyclerView.setAdapter(announcementAdapter);
                            binding.articleRecyclerView.setVisibility(View.VISIBLE);

                        } else {
                            showToast("No article to show");
                        }
                    } else {
                        showToast("error");
                    }
                });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // suppression d'un article
    @Override
    public void onArticleViewClicked(Article article) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection(Constants.KEY_COLLECTION_ARTICLES).document(article.getId());

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(getIntent());
                showToast("succes delete !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("error to delete !");
            }
        });
    }
    // si il appuis sur retour il revient a la page profil
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }
}