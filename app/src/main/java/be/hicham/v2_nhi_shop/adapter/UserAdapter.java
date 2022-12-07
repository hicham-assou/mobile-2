package be.hicham.v2_nhi_shop.adapter;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hicham.v2_nhi_shop.databinding.ItemContainerUserBinding;
import be.hicham.v2_nhi_shop.listeners.ArticleChatListener;
import be.hicham.v2_nhi_shop.models.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private final List<User> users;
    private final ArticleChatListener articleChatListener;

    public UserAdapter(List<User> users, ArticleChatListener articleChatListener) {
        this.users = users;
        this.articleChatListener = articleChatListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user){
            binding.textArticle.setText(user.getEmail());
            binding.textUsername.setText(user.getUsername());
            binding.imageArticle.setImageBitmap(getUserImage(user.getImage()));
            binding.getRoot().setOnClickListener(v -> articleChatListener.onArticleChatClicked(user));

        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
