package library.assistant.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javax.swing.JOptionPane;
import library.assistant.ui.listbook.Book;
import library.assistant.ui.listbook.BookListController;
import library.assistant.ui.listmember.Member;

public class DBConnection {

    private static DBConnection dc = null;

    private final String url = "jdbc:mysql://localhost:3306/book";
    private final String user = "root";
    private final String password = "";
    private static Connection conn;
    private static Statement stmt;

    private DBConnection() {
        connect();
    }

    public static DBConnection getInstance() {
        if (dc == null) {
            dc = new DBConnection();
        }
        return dc;
    }

    private Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Cant Load Database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return null;
    }

    public boolean execAction(String query) {
        try {
            stmt = conn.createStatement();
            stmt.execute(query);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet execQuery(String qu) {
        try {
            ResultSet result;
            stmt = conn.createStatement();
            result = stmt.executeQuery(qu);
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Boolean deleteBook(Book book) {

        try {
            String deleteStatement = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            //PreparedStatement stmt = dc.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isBookAlreadyIssued(Book book) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM issue WHERE bookID = ?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return (count > 0);
            }

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    public boolean updateBook(Book book){
        try {
            String update = "UPDATE books SET book_title =? , book_author = ?, book_publisher = ? WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return(res>0);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateMember(Member member){
        try {
            String update = "UPDATE members SET name =? , mobile = ?, email = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getMobile());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return(res>0);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public ObservableList<PieChart.Data> getBooksGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM books";
            String qu2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Books (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Issued Books (" + count + ")", count));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public ObservableList<PieChart.Data> getMemberGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM members";
            String qu2 = "SELECT COUNT(DISTINCT memberID) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Members (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Members with Books (" + count + ")", count));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
