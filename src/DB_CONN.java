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
	
	//private String[] tables={"FACULTY","HAS_DEPT","DEPT","COURSE","GRADE"};
	
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
		
		query.executeUpdate("CREATE TABLE FACULTY(F_NAME VARCHAR(50) NOT NULL PRIMARY KEY)");
		query.executeUpdate("CREATE TABLE HAS_DEPT(D_NAME VARCHAR(100), " +
												  "F_NAME VARCHAR(50), " +
												  "FOREIGN KEY(F_NAME) REFERENCES FACULTY(F_NAME), " +
												  "FOREIGN KEY(D_NAME) REFERENCES DEPT(D_NAME), " +
												  "PRIMARY KEY(D_NAME,F_NAME))");
		query.executeUpdate("CREATE TABLE DEPT(D_NAME VARCHAR(100) NOT NULL PRIMARY KEY," +
												  "ABBRIVIATION VARCHAR(3))");
		query.executeUpdate("CREATE TABLE COURSE(C_ID INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
												  "C_NAME VARCHAR(100), " +
												  "D_NAME VARCHAR(100), " +
												  "SECTION VARCHAR(2), " +
												  "F_NAME VARCHAR(50), " +
												  "SEMESTER VARCHAR(100), " +
												  "FOREIGN KEY(D_NAME) REFERENCES DEPT(D_NAME), " +
												  "FOREIGN KEY(F_NAME) REFERENCES FACULTY(F_NAME))");
		query.executeUpdate("CREATE TABLE GRADE(GRADE VARCHAR(2) NOT NULL, " +
												  "C_ID INTEGER NOT NULL, " +
												  "PERCENT DOUBLE, " +
												  "COUNT INTEGER, " +
												  "FOREIGN KEY(C_ID) REFERENCES COURSE(C_ID), "+
												  "PRIMARY  KEY(GRADE,C_ID))");
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
