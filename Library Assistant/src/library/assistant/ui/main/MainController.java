package library.assistant.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBConnection;
import library.assistant.util.LibraryAssistantUtil;

public class MainController implements Initializable {

    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
    @FXML
    private TextField bookIDInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;
    @FXML
    private TextField memberIDInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberContact;
    @FXML
    private JFXTextField bookID;
    private ListView<String> issueDataList;
    @FXML
    private StackPane rootPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Text memberNameHolder;
    @FXML
    private Text memberEmailHolder;
    @FXML
    private Text memberContactHolder;
    @FXML
    private Text bookNameHolder;
    @FXML
    private Text bookAuthorHolder;
    @FXML
    private Text bookPublisherHolder;
    @FXML
    private Text issueDateHolder;
    @FXML
    private Text numberDaysHolder;
    @FXML
    private Text fineHolder;
    
    DBConnection dc = DBConnection.getInstance();
    Boolean isReadyForSubmission = false;
    PieChart bookChart;
    PieChart memberChart;
    
    @FXML
    private AnchorPane rootAnchorPaine;
    @FXML
    private JFXButton renewButton;
    @FXML
    private JFXButton submissionButton;
    @FXML
    private HBox submissionDataContainer;
    @FXML
    private StackPane bookInfoContainer;
    @FXML
    private StackPane memberInfoContainer;
    @FXML
    private Tab bookIssueTab;
    @FXML
    private Tab bookRenewTab;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(book_info, 1);
        JFXDepthManager.setDepth(member_info, 1);
        
        initDrawer();
        initGraphs();
    }

    

    

    @FXML
    private void loadBookInformation(ActionEvent event) {
        clearBookCache();
        enableDisableGraphs(false);
        String id = bookIDInput.getText();

        String qu = "SELECT * FROM books WHERE book_id = '" + id + "' ";
        try {
            ResultSet rs = dc.execQuery(qu);
            Boolean flag = false;
            while (rs.next()) {
                String bName = rs.getString("book_title");
                String bAuthor = rs.getString("book_author");
                Boolean bStatus = rs.getBoolean("availability");

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus) ? "Availible" : "Not Availible";
                bookStatus.setText(status);
                flag = true;

            }
            if (!flag) {
                bookName.setText("No Such Book Availabile");
            }
        } catch (SQLException ex) {
            System.err.print("Error" + ex);

        }

    }

    void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    void clearMemberCache() {
        memberName.setText("");
        memberContact.setText("");
    }

    @FXML
    private void loadMemberInformation(ActionEvent event) {
        clearMemberCache();
        enableDisableGraphs(false);
        String id = memberIDInput.getText();
        String qu = "SELECT * FROM members WHERE id = '" + id + "' ";
        try {
            ResultSet rs = dc.execQuery(qu);
            Boolean flag = false;
            while (rs.next()) {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                memberName.setText(mName);
                memberContact.setText(mMobile);
                flag = true;
            }
            if (!flag) {
                memberName.setText("No Such Member Availabile");
            }
        } catch (SQLException ex) {
            System.err.print("Error" + ex);
        }
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();

        JFXButton yesButton = new JFXButton("YES");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
        
            String str = "INSERT INTO issue (memberID, bookID) VALUES ('" + memberID + "', '" + bookID + "')";
            String str2 = "UPDATE books SET availability = false WHERE book_id = '" + bookID + "'";
            if (dc.execAction(str) && dc.execAction(str2)) {
                
            JFXButton button = new JFXButton("Done!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(button), "Book Issue Complete", null);
            refreshGraphs();
            } else {
                
            JFXButton button = new JFXButton("Okay. I'll Check");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(button), "Issue Operation Failed", null);

            }
            clearIssueEntries();
            
        });
        
        JFXButton noButton = new JFXButton("NO");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
        
            JFXButton button = new JFXButton("That's Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(button), "Issue Operation Canceled", null);
            clearIssueEntries();
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(yesButton, noButton), "Confirm Issue", "Are you sure want to issu the book " + bookName.getText() + " to " + memberName.getText() + " ?");
    
        
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearEntries();
       ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;
        String id = bookID.getText();
        
        try{
        String myQuery = "SELECT * "

                + "FROM issue "
                + "LEFT JOIN members "
                + "ON issue.memberID = members.id "
                + "LEFT JOIN books "
                + "ON issue.bookID = books.book_id "
                + "WHERE issue.bookID = '" + id + "'";

        ResultSet rs = dc.execQuery(myQuery);
        if(rs.next()){
            memberNameHolder.setText(rs.getString("name"));
            memberEmailHolder.setText(rs.getString("email"));
            memberContactHolder.setText(rs.getString("mobile"));
            
            bookNameHolder.setText(rs.getString("book_title"));
            bookAuthorHolder.setText(rs.getString("book_author"));
            bookPublisherHolder.setText(rs.getString("book_publisher"));
            
            
            Timestamp mIssueTime = rs.getTimestamp("issuTime");
            Date dateOfIssue = new Date(mIssueTime.getTime());
            issueDateHolder.setText(rs.getString("issuTime"));
            Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
            Long daysElapsed = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS);
            numberDaysHolder.setText(daysElapsed.toString());
            fineHolder.setText("Not Supported Yet");
            
            isReadyForSubmission = true;
            disableEnableControls(true);
            submissionDataContainer.setOpacity(1);
        }else{
            JFXButton button = new JFXButton("Okay. I'll Check");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(button), "No suach Vook Exists in Issue Database", null);
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    @FXML
    private void loadSubmissionOperation(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(btn), "Please select a book to submit", "Cant simply submit a null book :-)");
            return;
        }
        
        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            
            String id = bookID.getText();
            String ac1 = "DELETE FROM issue WHERE bookID = '" + id + "'";
            String ac2 = "UPDATE books SET availability = true WHERE book_id = '" + id + "'";

            if (dc.execAction(ac1) && dc.execAction(ac2)) {
                
                JFXButton btn = new JFXButton("Done!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(btn), "Book Has Been Submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay. I'll check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(btn), "Submission Has Failed", null);
                
            }
            
        });
        JFXButton noButton = new JFXButton("NO");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
         
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(btn), "Submission Operation Canceled", null);

         
        });
        
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPaine, Arrays.asList(yesButton, noButton), "Confirm Submission Operation", "Are you sure want to return the book ");
    }

    @FXML
    private void loadRenewOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to renew");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to renew the book ");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String ac = "UPDATE issue Set issuTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE bookID = '" + bookID.getText() + "'";
            if (dc.execAction(ac)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Has Been Renew");
                alert1.showAndWait();
                loadBookInfo(null);
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Renew Has Failed");
                alert1.showAndWait();
            }

        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Canceled");
            alert1.setHeaderText(null);
            alert1.setContentText("Renew Operation Canceled");
            alert1.showAndWait();
        }
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"), "Add New Book" , null );

    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"), "Add New Member" , null );
    }

    @FXML
    private void handleMenuViewBooks(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/listbook/book_list.fxml"), "Book List" , null );
    }

    @FXML
    private void handleMenuViewMembers(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/listmember/member_list.fxml"), "Member List" , null );
    }

   
    
    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage) rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void initDrawer() {
        try {
            VBox toolbar = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/toolbar/toolbar.fxml"));
            drawer.setSidePane(toolbar);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) ->{

                task.setRate(task.getRate()* -1);
                task.play();
                if(drawer.isClosed()){
                    drawer.open();
                }else{
                    drawer.close();
                }
            
        });
    }

    private void clearEntries() {
        memberNameHolder.setText("");
        memberContactHolder.setText("");
        memberEmailHolder.setText("");
        
        bookNameHolder.setText("");
        bookAuthorHolder.setText("");
        bookPublisherHolder.setText("");
        
        issueDateHolder.setText("");
        numberDaysHolder.setText("");
        fineHolder.setText("");
        
        disableEnableControls(false);
        submissionDataContainer.setOpacity(0);
    }
    
    private void disableEnableControls(Boolean enableFlag){
        if(enableFlag){
            renewButton.setDisable(false);
            submissionButton.setDisable(false);
        }else{
            renewButton.setDisable(true);
            submissionButton.setDisable(true);
        }
    }

    private void clearIssueEntries() {
        
        bookIDInput.clear();
        memberIDInput.clear();
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
        memberName.setText("");
        memberContact.setText("");
        enableDisableGraphs(true);
    }

    private void initGraphs() {
        bookChart = new PieChart(dc.getBooksGraphStatistics());
        memberChart = new PieChart(dc.getMemberGraphStatistics());
        bookInfoContainer.getChildren().add(bookChart);
        memberInfoContainer.getChildren().add(memberChart); 
        
        bookIssueTab.setOnSelectionChanged((Event event) -> {
            clearIssueEntries();
            if(bookIssueTab.isSelected()){
                refreshGraphs();
            }
        });
    }
    
    private void refreshGraphs(){
       memberChart.setData(dc.getMemberGraphStatistics());
       bookChart.setData(dc.getBooksGraphStatistics());
    }
    
    private void enableDisableGraphs(Boolean status){
        if(status){
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        }else{
           bookChart.setOpacity(0);
           memberChart.setOpacity(0); 
        }
    }

}
