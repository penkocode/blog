package blog.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "article")
public class Article {

    private Integer id;

    private String title;

    private String content;

    private Date date;

    private User author;

    private String imagePath;

    public Article(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.imagePath = "";

        this.date = new Date();
    }

    public Article() {
    }

    public Article(String title, String content, User author, String imagePath) {

        this.title = title;
        this.content = content;
        this.author = author;
        this.imagePath = imagePath;

        this.date = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "content", nullable = false, columnDefinition = "text")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(nullable = false, name = "author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "imagePath", nullable = true)
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Transient
    public String getSummary() {
        return this.getContent()
                .substring(0, 150) + "...";


    }


}
