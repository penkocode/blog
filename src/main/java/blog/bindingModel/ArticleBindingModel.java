package blog.bindingModel;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

public class ArticleBindingModel {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @Transient
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
