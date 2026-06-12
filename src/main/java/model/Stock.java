package model;

public class Stock{
    private int id;
    private int bookId;
    private int quantity;
    private String bookTitle;

    public Stock() {}

    public Stock(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    @Override
    public String toString() {
        return bookTitle + " (остаток: " + quantity + " шт.)";
    }
}