package library.assistant.database;


import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import library.assistant.ui.listbook.Book;
import library.assistant.ui.listbook.BookListController;
import library.assistant.ui.listmember.Member;








public final class DBConnection {
    
   

    private static DBConnection dc;

    private static final String URL = "jdbc:derby:Database;create=true";
    private static Connection conn ;
    private static Statement stmt ;

	 

    private DBConnection() { 
        connect();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }

    public static DBConnection getInstance() {
        if (dc == null) {
            dc = new DBConnection();
        }
        return dc;
    }
    
    
    private static void connect() {

            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
                conn = DriverManager.getConnection(URL);
                System.out.println("Connected to Database!!!!!!!!!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } catch (InstantiationException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
	
    
    
    void setupBookTable(){
                    String TABLE8NAME = "books";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE8NAME, null);
            if(tables.next()){
                System.out.println("Table " + TABLE8NAME + "already exists. Ready for go!");
            }else{
                stmt.execute("CREATE TABLE " + TABLE8NAME + "("
                        + " book_id varchar(200) primary key, \n"
                        + " book_title varchar(200), \n"
                        + " book_author varchar(200), \n"
                        + " book_publisher varchar(100), \n"
                        + " availability boolean default true"
                        + ")");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage() + "... setupDatabase");
        }finally{
            
        }
        
    }
    

        void setupMemberTable(){
                    String TABLE8NAME = "members";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE8NAME, null);
            if(tables.next()){
                System.out.println("Table " + TABLE8NAME + "already exists. Ready for go!");
            }else{
                stmt.execute("CREATE TABLE " + TABLE8NAME + "("
                        + " id varchar(20) primary key, \n"
                        + " name varchar(200), \n"
                        + " mobile varchar(200), \n"
                        + " email varchar(100)"
                        + ")");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage() + "... setupDatabase");
        }finally{    
        }  
    }
        
        void setupIssueTable(){
                    String TABLE8NAME = "issue";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE8NAME, null);
            if(tables.next()){
                System.out.println("Table " + TABLE8NAME + "already exists. Ready for go!");
            }else{
                stmt.execute("CREATE TABLE " + TABLE8NAME + "("
                        + " bookID varchar(50) primary key, \n"
                        + " memberID varchar(50), \n"
                        + " issuTime timestamp default CURRENT_TIMESTAMP, \n"
                        + " renew_count integer default 0"
                        + ")");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage() + "... setupDatabase");
        }finally{    
        }  
    }

  
        
        
        
   
    
        private static Set<String> getDBTables() throws SQLException {
        Set<String> set = new HashSet<>();
        DatabaseMetaData dbmeta = conn.getMetaData();
        readDBTable(set, dbmeta, "TABLE", null);
        return set;
    }
        
        
    private static void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException {
        ResultSet rs = dbmeta.getTables(null, schema, null, new String[]{searchCriteria});
        while (rs.next()) {
            set.add(rs.getString("TABLE_NAME").toLowerCase());
        }
    }    
        
	
    
   private static void createTables(List<String> tableData) throws SQLException {
        Statement statement = conn.createStatement();
        statement.closeOnCompletion();
        for (String command : tableData) {
            System.out.println(command);
            statement.addBatch(command);
        }
        statement.executeBatch();
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