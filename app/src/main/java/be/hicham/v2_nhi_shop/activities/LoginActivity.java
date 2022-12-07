package be.hicham.v2_nhi_shop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityLoginBinding;
import be.hicham.v2_nhi_shop.utilities.Constants;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        /// renvoie a l'activity register
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInput()) {
                signIn();
            }
        });
    }

    /// si il appuis sur retour il revient a la page home(mainActivity)
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    /// requirements pour pouvoir se connecter sinn il peut pas
    private boolean isValidSignInput() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            binding.inputEmail.setError("Email is required!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            binding.inputEmail.setError("Enter valid email!");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            binding.inputPassword.setError("Enter password!");
            binding.inputPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    /// vérification avec la DB
    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        System.out.println(preferenceManager.getString(Constants.KEY_USERNAME));
                        System.out.println(preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                        System.out.println(preferenceManager.getString(Constants.KEY_USER_ID));

                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_USERNAME, documentSnapshot.getString(Constants.KEY_USERNAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        getToken();
                        System.out.println(preferenceManager.getString(Constants.KEY_USERNAME));
                        System.out.println(preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                        System.out.println(preferenceManager.getString(Constants.KEY_USER_ID).toString());


                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign up");
                    }
                });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token updated successfully!"))
                .addOnFailureListener(e -> showToast("Unable to update Token"));
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}