import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class DB_CONN {
	static String dbFile="course.db";
	private static Connection conn_S;
	private Connection conn;
	
	private String[] tables={"TEMP"};
	private PreparedStatement tempInsert;
	
	DB_CONN(){
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void init(){
		try {
			Class.forName("org.sqlite.JDBC");
			conn_S = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			createDB();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void createDB() throws SQLException {
		// TODO Auto-generated method stub
		Statement query=conn_S.createStatement();
		//Use this to create the tables
		
		//the following table is just for proof of concept that i can make via static call
		query.executeUpdate("CREATE TABLE TEMP(courseTitle VARCHAR(100), tmpkey INTEGER PRIMARY KEY AUTOINCREMENT)");
	}
	
	
	public void insertIntoTabe(Course c){
		//TODO give a lock to the conn.activitylock before insert
		
		try {
			 synchronized (conn_S) {
				 tempInsert=conn.prepareStatement("INSERT INTO TEMP(courseTitle) VALUES(?)");
				 tempInsert.setString(1, c.getCourseTitle());
				 tempInsert.executeUpdate();
			 }
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
