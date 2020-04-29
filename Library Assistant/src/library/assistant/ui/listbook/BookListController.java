package library.assistant.ui.listbook;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBConnection;
import library.assistant.ui.addbook.BookAddController;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;

public class BookListController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;
   
    DBConnection dc = DBConnection.getInstance();
    ObservableList<Book> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
    }

    private void loadData() {
        list.clear();
        try {
            ResultSet rs = dc.execQuery("SELECT * FROM books");
            while (rs.next()) {
                String titlex = rs.getString("book_title");
                String id = rs.getString("book_id");
                String author = rs.getString("book_author");
                String publisher = rs.getString("book_publisher");
                Boolean availability = rs.getBoolean("availability");
                list.add(new Book(titlex, id, author, publisher, availability));
            }
        } catch (SQLException ex) {
            System.err.print("Error" + ex);
        }

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        tableView.setItems(list);
        //tableView.setItems(list);
    }

    @FXML
    private void handleBookDeleteOption(ActionEvent event) {
        // Fetch the selected row
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion == null){
            AlertMaker.showErrorMessage("No book selected", "Please select a book for deletion");
            return;
        }
        if(DBConnection.getInstance().isBookAlreadyIssued(selectedForDeletion)){
           AlertMaker.showErrorMessage("Cant be deleted", "This book is already issued and cant be deleted");
           return;     
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting book");
        alert.setContentText("Are you sure want to delete the book " + selectedForDeletion.getTitle() + " ?" );
        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == ButtonType.OK){
            //Do rest of the processing
            Boolean result = DBConnection.getInstance().deleteBook(selectedForDeletion);
            if(result){
                AlertMaker.showSimpleAlert("Book deleted", selectedForDeletion.getTitle() + " was deleted successfully.");
                list.remove(selectedForDeletion);
            }else{
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getTitle() + " could not be deleted.");
            }
        }else{
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void handleBookEditOption(ActionEvent event) {
        // Fetch the selected row
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null){
            AlertMaker.showErrorMessage("No book selected", "Please select a book for deletion");
            return;
        } 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"));
            Parent parent = loader.load();
            
            BookAddController controller = (BookAddController)loader.getController();
            controller.inflatUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
            
            stage.setOnCloseRequest((e) -> { 
                handleRefresh(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }
  
    
}
