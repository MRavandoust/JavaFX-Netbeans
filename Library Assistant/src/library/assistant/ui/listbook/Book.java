package library.assistant.ui.listbook;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author MASOUD
 */
public class Book {

    private final SimpleStringProperty title;
    private final SimpleStringProperty id;
    private final SimpleStringProperty author;
    private final SimpleStringProperty publisher;
    private final SimpleBooleanProperty availability;

    public Book(String title, String id, String author, String publisher, Boolean availability) {
        this.title = new SimpleStringProperty(title);
        this.id = new SimpleStringProperty(id);
        this.author = new SimpleStringProperty(author);
        this.publisher = new SimpleStringProperty(publisher);
        this.availability = new SimpleBooleanProperty(availability);
    }



    public String getTitle() {
        return title.get();
    }

    public String getId() {
        return id.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public String getPublisher() {
        return publisher.get();
    }

    public boolean getAvailability() {
        return availability.get();
    }

}
