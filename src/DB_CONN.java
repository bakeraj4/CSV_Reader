import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;


public class DB_CONN {
	static String dbFile="course.db";
	private static Connection conn_S;
	private Connection conn;
	
	//private String[] tables={"FACULTY","HAS_DEPT","DEPT","COURSE","GRADE"};
	
	private static HashMap<String,String> abrMap=new HashMap<String,String>();
	
	private static PreparedStatement deptInsert;
	private PreparedStatement facultyInsert,hasDeptInsert,courseInsert,gradeInsert;
	private PreparedStatement facultyIsIn,hasDeptIsIn,courseCID;
	
	
	DB_CONN(){
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			prepOtherPreparedStatements();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void init(){
		try {
			Class.forName("org.sqlite.JDBC");
			conn_S = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			//creates the db and the tables
			createDB();
			//creates the prepared statements for static data
			prepStaticPreparedStatements();
			//adds the departments to the db
			addAllDepts();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void prepStaticPreparedStatements(){
		try {
			deptInsert= conn_S.prepareStatement("INSERT INTO DEPT VALUES (?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void prepOtherPreparedStatements(){
		try{
			//faculty inserts (only insert if they aren't present in db yet)
			//the insert
			facultyInsert=conn.prepareStatement("INSERT INTO FACULTY VALUES(?);");
			//query for them
			facultyIsIn=conn.prepareStatement("SELECT * FROM FACULTY WHERE F_NAME = ?;");
			//hasdept (only insert if the combo of dept and faculty is unique. ie karro is in cse and bio)
			//the insert
			hasDeptInsert=conn.prepareStatement("INSERT INTO HAS_DEPT VALUES(?,?);");
			//check if the combo is in there
			hasDeptIsIn=conn.prepareStatement("SELECT * FROM HAS_DEPT WHERE D_NAME=? AND F_NAME=?;");
			//course
			//the insert
			courseInsert= conn.prepareStatement("INSERT INTO COURSE(C_NAME,D_NAME,SECTION, F_NAME, SEMESTER, C_NUM, C_ID) VALUES(?,?,?,?,?,?,?);");
			//get the c_ID
			courseCID=conn.prepareStatement("SELECT C_ID FROM COURSE WHERE SECTION =? AND F_NAME=? AND C_NUM=? AND SEMESTER =? AND C_NAME=? AND D_NAME =?;");
			//grades
			//the insert
			gradeInsert=conn.prepareStatement("INSERT INTO GRADE VALUES(?,?,?,?);");
		} catch (SQLException e){
			
		}
	}
	
	private static void addAllDepts(){
		try {
			Scanner scan=new Scanner(new File("classes.txt"));
			String s="",abr="",dept="";
			while(scan.hasNext()){
				s=scan.nextLine();
				abr=s.substring(0,3);
				dept=s.substring(3);
				
				//add to the has map
				abrMap.put(abr, dept);
				
				deptInsert.setString(1, dept);
				deptInsert.setString(2, abr);
				deptInsert.execute();
				//add to the dept table
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private int getCID(Course c){
		try {
			courseCID.setString(1, c.getSection());
			courseCID.setString(2, c.getInstructor());
			courseCID.setString(3, c.getCourseNum());
			courseCID.setString(4, c.getSem());
			courseCID.setString(5, c.getCourseTitle());
			courseCID.setString(6, abrMap.get(c.getAbbriavtion()));
			ResultSet res=courseCID.executeQuery();
			int ret=-1;
			if(res.next()){
				ret=res.getInt("C_ID");
			}
			res.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private static void createDB() throws SQLException {
		Statement query=conn_S.createStatement();
		//Use this to create the tables		
		query.executeUpdate("CREATE TABLE FACULTY(F_NAME VARCHAR(50) NOT NULL PRIMARY KEY)");
		query.executeUpdate("CREATE TABLE HAS_DEPT(D_NAME VARCHAR(100), " +
												  "F_NAME VARCHAR(50), " +
												  "FOREIGN KEY(F_NAME) REFERENCES FACULTY(F_NAME), " +
												  "FOREIGN KEY(D_NAME) REFERENCES DEPT(D_NAME), " +
												  "PRIMARY KEY(D_NAME,F_NAME))");
		query.executeUpdate("CREATE TABLE DEPT(D_NAME VARCHAR(100) NOT NULL, " +
												  "ABBRIVIATION VARCHAR(3), " +
												  "PRIMARY KEY(D_NAME, ABBRIVIATION))");
		query.executeUpdate("CREATE TABLE COURSE(C_ID INTEGER  NOT NULL PRIMARY KEY, " +
												  "C_NAME VARCHAR(100), " +
												  "D_NAME VARCHAR(100), " +
												  "SECTION VARCHAR(2), " +
												  "F_NAME VARCHAR(50), " +
												  "C_NUM VARCHAR(4), " +
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
	
	
	public void insertIntoTable(Course c,int cnum){
		try {
			//adds to the course table
			courseInsert.setString(1, c.getCourseTitle());
			courseInsert.setString(2, abrMap.get(c.getAbbriavtion()));
			courseInsert.setString(3, c.getSection());
			courseInsert.setString(4, c.getInstructor());
			courseInsert.setString(5, c.getSem());
			courseInsert.setString(6, c.getCourseNum());
			courseInsert.setInt(7, cnum);
			courseInsert.execute();
			
			
			//is prof in it already
			facultyIsIn.setString(1, c.getInstructor());
			ResultSet rs=facultyIsIn.executeQuery();
			//insert prof
			if(!rs.next()){//next givs false if the result set is empty
				facultyInsert.setString(1, c.getInstructor());
				facultyInsert.execute();
			}
			rs.close();
			
			//is dept prof combo in it
			hasDeptIsIn.setString(1, abrMap.get(c.getAbbriavtion()));
			hasDeptIsIn.setString(2, c.getInstructor());
			ResultSet res=hasDeptIsIn.executeQuery();
			//insert combo
			if(!res.next()){
				hasDeptInsert.setString(1, abrMap.get(c.getAbbriavtion()));
				hasDeptInsert.setString(2, c.getInstructor());
				hasDeptInsert.execute();
			}
			res.close();
			
			//insert the grades
			
			try{
				System.out.println(cnum);
				String[] grades={"A+","A","A-","B+","B","B-","C+","C","C-","D+","D","D-","F","W","WP","WF","I","X","Y","P","S"};
				for(int i=0;i<c.getGradeOccurance().length;i++){
					//the grade will be grades[i]
					gradeInsert.setString(1, grades[i]);
					//the CID is the C_ID from above
					gradeInsert.setInt(2, cnum);
					//the percent of c.getGradeDist()[i];
					gradeInsert.setDouble(3, c.getGradeDist()[i]);
					//the number of occurances c.getGradeOccurance()[i];
					gradeInsert.setInt(4, c.getGradeOccurance()[i]);
					gradeInsert.execute();
				}
			}catch(SQLException e){
				System.out.println("There was a duplicate.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(c.toString());
			System.exit(0);//just to kill the process
		}
	}
}
