package library.assistant.ui.addbook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBConnection;
import library.assistant.ui.listbook.*;


public class BookAddController {

    @FXML
    private JFXTextField title;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField author;

    @FXML
    private JFXTextField publisher;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private AnchorPane rootPane;
    private Boolean isInEditMode = Boolean.FALSE;
    

    public void initialize(URL url, ResourceBundle rb) {

    }
    
    DBConnection dc = DBConnection.getInstance();

    @FXML
    void addBook(ActionEvent event){
        String bookTitle = title.getText();
        String bookId = id.getText();
        String bookAuthor = author.getText();
        String bookPublisher = publisher.getText();

        if (bookTitle.isEmpty() || bookId.isEmpty() || bookAuthor.isEmpty() || bookPublisher.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Remplissez all cases S.V.P");
            alert.showAndWait();
            return;
        }
        
        if(isInEditMode){
            handleEditOperation();
            return;
        }

        String query = "INSERT INTO books(book_title, book_id, book_author, book_publisher) VALUES ('" + bookTitle + "', '" + bookId + "', '" + bookAuthor + "', '" + bookPublisher + "')";
        if (dc.execAction(query)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed !");
            alert.showAndWait();
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    
    public void inflatUI(Book book){
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }

    private void handleEditOperation() {
        Book book = new Book(title.getText(), id.getText(), author.getText(), publisher.getText(), true);
        if(DBConnection.getInstance().updateBook(book)){
            AlertMaker.showSimpleAlert("Success", "Book Updated");
        }else{
            AlertMaker.showErrorMessage("Failed", "Cant update book");
        }
    }
}
