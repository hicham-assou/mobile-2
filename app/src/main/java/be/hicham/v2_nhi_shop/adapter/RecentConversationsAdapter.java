package be.hicham.v2_nhi_shop.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hicham.v2_nhi_shop.databinding.ItemContainerRecentConversionBinding;
import be.hicham.v2_nhi_shop.listeners.ConversionListener;
import be.hicham.v2_nhi_shop.models.ChatMessage;
import be.hicham.v2_nhi_shop.models.User;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

    private  final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @Override
    public ConversionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.imageArticle.setImageBitmap(getConversionImage(chatMessage.getImage()));
            binding.textUsername.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getConversionMessage());
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.setId(chatMessage.getConversionId());
                user.setUsername(chatMessage.conversionName);
                user.setImage(chatMessage.getImage());
                conversionListener.onConversionClicked(user, chatMessage.getArticleId());
            });
        }
    }


    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
