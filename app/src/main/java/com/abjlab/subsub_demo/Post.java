package com.abjlab.subsub_demo;


public class Post {

    private String title;
    private String desc;
    private String author;
    private int id;

    public Post(){}

    public Post(String title, String desc, String author, int id) {
        this.title = title;
        this.desc = desc;
        this.author = author;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}
