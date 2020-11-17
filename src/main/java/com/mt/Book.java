package com.mt;

import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;

@JsonPropertyOrder({"id"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    public Book(String title, String author, String isbn, Date published, String id) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.published = published;
        this.id = id;
    }

    public Book(String title, String author, String isbn, String id) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.published = new Date();
        this.id = id;
    }

    @NotNull(message = "title is required field")
    private String title;
    @NotNull(message = "author is required field")
    private String author;
    private String isbn;
    private Date published;
    private String id;
    private HashMap<String, Object> extras = new HashMap<String, Object>();

    public Book() {
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonAnyGetter
    public HashMap<String, Object> getExtras() {
        return extras;
    }

    @JsonAnySetter
    public void setExtras(String key, Object value) {
        this.extras.put(key,value);
    }

    public String toString() {
        return "id=" + id + "\ttitle=" + title + "\tauthor=" + author + "\tisbn=" + isbn + "\tpublished=" + published;
    }
}
