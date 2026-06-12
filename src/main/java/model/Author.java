package model;

public class Author extends Person {

    public Author() {}

    public Author(String firstName, String lastName) {
        super(firstName, lastName);
    }

    @Override
    public String getDisplayName() {
        return getFullName();
    }
}