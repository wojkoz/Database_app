package database;
import ExceptionHandlers.LoginException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import mailSender.MailSender;
import model.UserData;



public class Database {
 
    private final Connection  db;
    private final Statement st;
    private ResultSet rs;
    private final ArrayList<String> schemas, tables, columns;
    private final HashMap<String, HashMap<String, ArrayList<String>>> table_column_data;
    private String currentScheme;
    private final DatabaseMetaData md;
    //mail
    
    
    
    public Database(UserData user) throws ClassNotFoundException, SQLException{

        Class.forName("org.postgresql.Driver");

        try{
            db = DriverManager.getConnection("jdbc:postgresql://"+ user.getIp() +":"+ user.getPort() +"/"+ user.getUsername(), user.getUsername(), user.getPassword());
        }catch(Exception e){
            throw new LoginException("Cannot login user: " + user.getUsername(), e);
        }
        //db = DriverManager.getConnection("jdbc:postgresql://195.150.230.210:5434/2019_koziol_wojciech", "2019_koziol_wojciech", password);
        st = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
        schemas = new ArrayList<>();
        tables = new ArrayList<>();
        columns = new ArrayList<>();
        table_column_data = new HashMap<>();
        
        
        md = db.getMetaData();
        setFirstConnection();
    }
    
    private void setFirstConnection() throws SQLException{

        
        setSchemasName();
        
        currentScheme = schemas.get(0);
        
        updateData();
        
  
    }
  
    public void updateData() throws SQLException{
        setTablesName(currentScheme);    
        
        //pobieranie danych do kolumn z tabel currentScheme
        for(int i=0; i < tables.size(); i++){
            
            setColumnsName(currentScheme, tables.get(i));
            HashMap<String, ArrayList<String>> tmp = new HashMap<>();
            
            
            for(int j=0; j < columns.size(); j++){
                tmp.put(columns.get(j), setColumnData(currentScheme, tables.get(i), columns.get(j)));
                
            }
            table_column_data.put(tables.get(i), tmp);
            columns.clear();
        }
    }
    
    private ArrayList<String> setColumnData(String scheme_name, String table_name, String column_name) throws SQLException{
        
        ArrayList<String> data = new ArrayList();
        
        rs = st.executeQuery("SELECT * FROM "+ scheme_name +"." + table_name);
        rs.beforeFirst();
        while(rs.next()){
            data.add(rs.getString(column_name));
        }
        return data;
    }
    
    private void setColumnsName(String scheme, String table_name) throws SQLException{
        rs = md.getColumns(null, scheme, table_name, "%");
        rs.beforeFirst();
        while(rs.next()){
            columns.add(rs.getString(4));
        }
    }
    
    private void setTablesName(String scheme) throws SQLException{
        final String[] types = {"TABLE"};
        rs = md.getTables(null, scheme, "%", types);
        rs.beforeFirst();
        while(rs.next()){
            tables.add(rs.getString(3));
        }
    }
    private void setSchemasName() throws SQLException{
        rs = md.getSchemas(null, "%");
        rs.beforeFirst();
        while(rs.next()){
            if(!rs.getString(1).equalsIgnoreCase("pg_catalog") && !rs.getString(1).equalsIgnoreCase("information_schema")){
                schemas.add(rs.getString(1));
            }  
        }
    }
    
    public void closeConnection() throws SQLException{
        st.close();
        db.close();
        //cleaning variables
        schemas.clear();
        clearArrays();

    }
    public void clearArrays(){
        columns.clear();
        table_column_data.clear();
        tables.clear();
    }
    
    public ArrayList<String> getSchemas(){
        return schemas;
    }

    public void setCurrentScheme(String scheme){
        currentScheme = scheme;
    }
    public String[] getTableNames(){
        return tables.toArray(new String[tables.size()]);
    }
    
    public String[] getColumnsNames(String table_name){
        ArrayList<String> key = new ArrayList<>();
        table_column_data.get(table_name).forEach((k,v) -> key.add(k));
        
        return key.toArray(new String[key.size()]);
    }
    public int getCountTables(){
        return tables.size();
    }
    
    public String[][] getRecords(String table_name, String first_column){
        
        ArrayList<String> key = new ArrayList<>();
        ArrayList<ArrayList<String>> val = new ArrayList<>();
        
        table_column_data.get(table_name).forEach((k,v) -> key.add(k));
        table_column_data.get(table_name).forEach((k,v) -> val.add(v));
        
        int row = key.size();
        int col = val.get(0).size();
                
        String[][] tmp = new String[col][row];
        
        for(int i=0; i<col; i++){
            for(int j=0; j<row; j++){
                tmp[i][j] = val.get(j).get(i);
            }
        }

        return tmp;
    }
    
    public ArrayList<String> getMails() throws SQLException{
        ArrayList<String> mails = new ArrayList<>();
        
        rs = st.executeQuery("SELECT * FROM "+ MailSender.MAIL_SCHEME +"." + MailSender.MAIL_TABLE + " WHERE " + MailSender.MAIL_COLUMNS[1] + " = true" );
        rs.beforeFirst();
        while(rs.next()){
            mails.add(rs.getString(1));
        }
        
        return mails;
    }
}
