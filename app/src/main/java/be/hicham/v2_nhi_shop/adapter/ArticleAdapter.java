package be.hicham.v2_nhi_shop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import be.hicham.v2_nhi_shop.databinding.ItemContainerArticleBinding;
import be.hicham.v2_nhi_shop.listeners.ArticleViewListener;
import be.hicham.v2_nhi_shop.models.Article;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>  {

    private final List<Article> articleModelList;
    private final ArticleViewListener articleViewListener;


    public ArticleAdapter(List<Article> articleModelList, ArticleViewListener articleViewListener) {
        this.articleModelList = articleModelList;
        this.articleViewListener = articleViewListener;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemContainerArticleBinding itemContainerArticleBinding = ItemContainerArticleBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ArticleAdapter.ArticleViewHolder(itemContainerArticleBinding);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        holder.setArticleData(articleModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return articleModelList.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        ItemContainerArticleBinding binding;

        ArticleViewHolder(ItemContainerArticleBinding itemContainerArticleBinding) {
            super(itemContainerArticleBinding.getRoot());
            binding = itemContainerArticleBinding;

        }

        void setArticleData(Article article){

            binding.title.setText(article.getTitle());
            binding.seller.setText(article.getSellerUsername());
            binding.localisation.setText(article.getLocalisation());
            binding.date.setText(article.getDatePosted());
            binding.price.setText(article.getPrice() + " €");
            binding.imageArticle.setImageBitmap(getArticleImage(article.getImage()));
            binding.getRoot().setOnClickListener(v -> articleViewListener.onArticleViewClicked(article) );

        }

        private Bitmap getArticleImage(String encodedImage){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

    }
}