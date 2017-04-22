package blog.controller;

import blog.bindingModel.ArticleBindingModel;
import blog.entity.Article;
import blog.entity.User;
import blog.repository.ArticleRepository;
import blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {

        model.addAttribute("view", "article/create");

        return "base-layout";

    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ArticleBindingModel articleBindingModel) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(userDetails.getUsername());

        /*Image Upload Logic Begin**/

        String databaseImagePath = null;

        String[] allowedContentTypes = {
                "image/jpeg",
                "image/jpg",
                "image/png"
        };

        boolean isContentTypeAllowed =
                Arrays.asList(allowedContentTypes)
                        .contains(articleBindingModel.getImage().getContentType());

        if (isContentTypeAllowed) {

            String imagesPath = "\\src\\main\\resources\\static\\images\\";

            String imagePathRoot = System.getProperty("user.dir");

            String imageSaveDirectory = imagePathRoot + imagesPath;

            String filename = articleBindingModel.getImage().getOriginalFilename();

            String savePath = imageSaveDirectory + filename;

            File imageFile = new File(savePath);

            try {
                articleBindingModel.getImage().transferTo(imageFile);
                databaseImagePath = "/images/" + filename;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        /*Image Upload Logic END*/

        Article articleEntity = new Article(
                articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                userEntity,
                databaseImagePath
        );

        this.articleRepository.saveAndFlush(articleEntity);

        return "redirect:/";

    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id) {

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        if (!(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)) {

            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = this.userRepository.findByEmail(userDetails.getUsername());

            model.addAttribute("user", user);

        }

        Article article = this.articleRepository.findOne(id);

        model.addAttribute("view", "article/details");
        model.addAttribute("article", article);
        return "base-layout";

    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")

    public String edit(@PathVariable Integer id, Model model) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {

            return "redirect:/";
        }

        model.addAttribute("article", article);
        model.addAttribute("view", "article/edit");

        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, ArticleBindingModel model) {

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {

            return "redirect:/";
        }

        article.setTitle(model.getTitle());
        article.setContent(model.getContent());

        this.articleRepository.saveAndFlush(article);

        return "redirect:/article/" + article.getId();
    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id, Model model) {

        if (!this.articleRepository.exists(id)) {

            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {

            return "redirect:/";
        }

        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");

        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String postProcess(@PathVariable Integer id) {

        if (!this.articleRepository.exists(id)) {

            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {

            return "redirect:/";
        }

        this.articleRepository.delete(article);

        return "redirect:/";
    }

    private boolean isUserAuthorOrAdmin(Article article) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = this.userRepository.findByEmail(userDetails.getUsername());

        return user.isAdmin() || user.isAuthor(article);
    }

}
