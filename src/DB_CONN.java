import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DB_CONN {
	static String dbFile="course.db";
	private static Connection conn_S;
	private Connection conn;
	
	DB_CONN(){
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
	}
	
	
	private void insertIntoTabe(Course c){
		//TODO give a lock to the conn.activitylock before insert
	}
}
