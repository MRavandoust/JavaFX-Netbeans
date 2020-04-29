
package library.assistant.ui.listmember;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBConnection;
import library.assistant.ui.addbook.BookAddController;
import library.assistant.ui.addmember.MemberAddController;
import library.assistant.ui.listbook.Book;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;


public class MemberListController implements Initializable {
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member , String> nameCol;
    @FXML
    private TableColumn<Member , String> idCol;
    @FXML
    private TableColumn<Member , String> mobileCol;
    @FXML
    private TableColumn<Member , String> emailCol;
    
    DBConnection dc = DBConnection.getInstance();
    ObservableList<Member> list = FXCollections.observableArrayList();


    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadmembers();
    } 
    
    private void loadmembers() {
        list.clear();
        try {
            ResultSet rs = dc.execQuery("SELECT * FROM members");
            while (rs.next()) {
                String Name = rs.getString("name");
                String Id = rs.getString("id");
                String Mobile = rs.getString("mobile");
                String Email = rs.getString("email");

                list.add(new Member(Name, Id, Mobile, Email));
            }
        } catch (SQLException ex) {
            System.err.print("Error" + ex);
        }

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));


        tableView.setItems(null);
        tableView.setItems(list);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadmembers();
    }

    @FXML
    private void handleMemberEditOption(ActionEvent event) {
        
        // Fetch the selected row
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null){
            AlertMaker.showErrorMessage("No book selected", "Please select a member for Edit");
            return;
        } 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();
            
            MemberAddController controller = (MemberAddController)loader.getController();
            controller.inflatUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
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
    private void handleMemberDeleteOption(ActionEvent event) {
    }
    
    
    
}
