package database;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Database {
 
    private Connection  db;
    private Statement st;
    private ResultSet rs;
    
    public Database(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            db = DriverManager.getConnection("jdbc:postgresql://195.150.230.210:5434/2019_koziol_wojciech", "2019_koziol_wojciech", "wojtek9");
            st = db.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void result(){
        try {
            rs = st.executeQuery("SELECT 1");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public Boolean closeConnection(){
        try {
            st.close();
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
