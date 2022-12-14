package be.hicham.v2_nhi_shop.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hicham.v2_nhi_shop.databinding.ItemContainerAnnoncementBinding;
import be.hicham.v2_nhi_shop.listeners.ArticleViewListener;
import be.hicham.v2_nhi_shop.models.Article;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private final List<Article> articleAnnouncementlList;
    private final ArticleViewListener articleViewListener;


    public AnnouncementAdapter(List<Article> articleAnnouncementlList, ArticleViewListener articleViewListener) {
        this.articleAnnouncementlList = articleAnnouncementlList;
        this.articleViewListener = articleViewListener;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerAnnoncementBinding itemContainerAnnoncementBinding = ItemContainerAnnoncementBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AnnouncementAdapter.AnnouncementViewHolder(itemContainerAnnoncementBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        holder.setArticleData(articleAnnouncementlList.get(position));
    }

    @Override
    public int getItemCount() {
        return articleAnnouncementlList.size();
    }

    class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        ItemContainerAnnoncementBinding binding;

        AnnouncementViewHolder(ItemContainerAnnoncementBinding itemContainerAnnoncementBinding) {
            super(itemContainerAnnoncementBinding.getRoot());
            binding = itemContainerAnnoncementBinding;

        }

        void setArticleData(Article article){

            binding.titleAnnouncement.setText(article.getTitle());
            binding.price.setText(article.getPrice() + " €");
            binding.imageArticleAnnouncement.setImageBitmap(getArticleImage(article.getImage()));
            binding.buttonDelete.setOnClickListener(v -> articleViewListener.onArticleViewClicked(article));

        }

        private Bitmap getArticleImage(String encodedImage){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

    }
}
