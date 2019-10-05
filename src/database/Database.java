package database;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;



public class Database {
 
    private final Connection  db;
    private final Statement st;
    private ResultSet rs;
    private final ArrayList<String> schemas, tables, columns;
    private final HashMap<String, ArrayList<String>> columnsData;
    private String currentScheme;
    
    public Database() throws ClassNotFoundException, SQLException{

        Class.forName("org.postgresql.Driver");

        db = DriverManager.getConnection("jdbc:postgresql://195.150.230.210:5434/2019_koziol_wojciech", "2019_koziol_wojciech", "wojtek9");
        st = db.createStatement();
        
        schemas = new ArrayList<>();
        tables = new ArrayList<>();
        columns = new ArrayList<>();
        columnsData = new HashMap<>();

    }
    
    public void result() throws SQLException{
        
        DatabaseMetaData md = db.getMetaData();
        
        setSchemasName(md);
        
        currentScheme = schemas.get(1);
        
        setTablesName(md, currentScheme);
        
        
        
        //pobieranie danych do kolumn z tabel currentScheme
        for(int i=0; i < tables.size(); i++){
            setColumnsName(md, currentScheme, tables.get(i));
            for(int j=0; j < columns.size(); j++){
                setColumnData(currentScheme, tables.get(i), columns.get(j));
            }
            columns.clear();
        }
        
        System.out.println(columnsData.get("id_adresu"));

        closeConnection();
  
    }
  
    
    private void setColumnData(String scheme_name, String table_name, String column_name) throws SQLException{
        
        ArrayList<String> data = new ArrayList();
        
        rs = st.executeQuery("SELECT * FROM "+ scheme_name +"." + table_name);
        while(rs.next()){
            data.add(rs.getString(column_name));
        }
        columnsData.put(column_name, data);
    }
    
    private void setColumnsName(DatabaseMetaData md, String scheme, String table_name) throws SQLException{
        rs = md.getColumns(null, scheme, table_name, "%");
        while(rs.next()){
            columns.add(rs.getString(4));
        }
    }
    
    private void setTablesName(DatabaseMetaData md, String scheme) throws SQLException{
        final String[] types = {"TABLE"};
        rs = md.getTables(null, scheme, "%", types);
        while(rs.next()){
            tables.add(rs.getString(3));
        }
    }
    private void setSchemasName(DatabaseMetaData md) throws SQLException{
        rs = md.getSchemas(null, "%");
        while(rs.next()){
            if(!rs.getString(1).equalsIgnoreCase("pg_catalog") && !rs.getString(1).equalsIgnoreCase("information_schema")){
                schemas.add(rs.getString(1));
            }  
        }
    }
    
    public void closeConnection() throws SQLException{
        st.close();
        db.close();
    }
    
      public ArrayList<String> getSchemas(){
        return schemas;
    }
    
    public String[] getColumns(){
        return columns.toArray(new String[columns.size()]);
    }
    
    public HashMap<String, ArrayList<String>> getColumnsData(){
        return columnsData;
    }
    
    public void setCurrentScheme(String scheme){
        currentScheme = scheme;
    }
}
