package model;

import java.math.BigDecimal;

public class Book extends Product {
    private int authorId;
    private int publisherId;
    private int categoryId;

    private String authorName;
    private String publisherName;
    private String categoryName;

    public Book() {
        super();
    }

    public Book(String title, BigDecimal price, int quantity,
                int authorId, int publisherId, int categoryId) {
        super(title, price, quantity);
        this.authorId = authorId;
        this.publisherId = publisherId;
        this.categoryId = categoryId;
    }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public int getPublisherId() { return publisherId; }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String getType() {
        return "Книга";
    }

    @Override
    public String getDisplayName() {
        String title = getTitle();
        if (publisherName != null && !publisherName.isEmpty()) {
            return title + " (" + publisherName + ")";
        }
        if (title != null) {
            return title;
        }
        return "Книга без названия";
    }
}