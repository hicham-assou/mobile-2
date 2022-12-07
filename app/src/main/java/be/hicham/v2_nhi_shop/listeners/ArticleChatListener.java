package be.hicham.v2_nhi_shop.listeners;

import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.models.User;

public interface ArticleChatListener {
    void onArticleChatClicked(User user);
}
