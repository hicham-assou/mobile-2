package be.hicham.v2_nhi_shop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.adapter.RecentConversationsAdapter;
import be.hicham.v2_nhi_shop.adapter.UserAdapter;
import be.hicham.v2_nhi_shop.databinding.ActivityMessageryBinding;
import be.hicham.v2_nhi_shop.listeners.ArticleChatListener;
import be.hicham.v2_nhi_shop.listeners.ConversionListener;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.models.ChatMessage;
import be.hicham.v2_nhi_shop.models.User;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class MessageryActivity extends AppCompatActivity implements  ConversionListener {

    private ActivityMessageryBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        checkSession();
        setListeners();
    }

    private void setListeners() {
        binding.bottomNavigationView.setSelectedItemId(R.id.navigation_message);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_addarticles:
                        startActivity(new Intent(getApplicationContext(),AddArticlesActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_message:

                        return true;
                    case R.id.navigation_account:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });
    }

    private void checkSession() {
        if (preferenceManager.getString(Constants.KEY_USER_ID) == null){
            startActivity(new Intent(MessageryActivity.this, LoginActivity.class));
        } else {
            init();
            listenConversations();
        }
    }

    private void init(){
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    // Permet de retrouver les conversations entre utilisateurs
    private void listenConversations(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return ;
        }
        if (value != null){
            loading(true);
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.setImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_TITLE_ARTICLE) + " - " + documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    } else {
                        chatMessage.setImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_TITLE_ARTICLE) + " - " + documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    }
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.setArticleId(documentChange.getDocument().getString(Constants.KEY_ARTICLE_ID));
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i < conversations.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)){
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            loading(false);

            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);

            if (conversations.size() == 0){
                showErrorMessage();
            }
        }
    };

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No conversions"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConversionClicked(User user, String articleId) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        intent.putExtra(Constants.KEY_ARTICLE_ID, articleId);
        startActivity(intent);
    }

    private Article setArticle(String articleId) {

        Article articleNew = new Article();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ARTICLES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (articleId.equals(queryDocumentSnapshot.getId())) {
                                articleNew.setTitle(queryDocumentSnapshot.getString(Constants.KEY_TITLE_ARTICLE));
                                articleNew.setDescription(queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION_ARTICLE));
                                articleNew.setSeller(queryDocumentSnapshot.getString(Constants.KEY_USERNAME));
                                articleNew.setLocalisation(queryDocumentSnapshot.getString(Constants.KEY_LOCALISATION_ARTICLE));
                                articleNew.setDateTime(getReadableDateTime(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP_ARTICLE)));
                                articleNew.setDateObject(queryDocumentSnapshot.getDate(Constants.KEY_TIMESTAMP_ARTICLE));
                                articleNew.setPrice(Double.parseDouble(queryDocumentSnapshot.getString(Constants.KEY_PRICE_ARTICLE)));
                                articleNew.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE_ARTICLE));
                                articleNew.setId(queryDocumentSnapshot.getId());
                            }
                        }
                    } else {
                        showToast("Can't retrieve article");
                    }
                });

        return articleNew;
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    /// si il appuis sur retour il revient a la page home(mainActivity)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}