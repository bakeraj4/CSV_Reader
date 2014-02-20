import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class CourseLib {
	//used for mapping. ie MTH -> Mathematics
	private String[][]arr=new String[2][86];
	private ArrayList<Course> courses=new ArrayList<Course>(); 
	
	CourseLib(){
		readClasses();
	}	
	
	public int isAbbreviation(String str){
		for(int i=0;i<86;i++){
			if(arr[0][i].equals(str))
				return i;
		}
		return -1;
	}
	
	public void addCourse(Course course){
		courses.add(course);
	}
	
	/*public ArrayList<Course> getCourses(){
		return courses;
	}*/
	
	public String toString(){
		String ret="";
		for(Course course: courses)
			ret+=course.toString()+"\n";
		return ret;
	}
	
	public void readClasses(){
		try {
			DataInputStream dis=new DataInputStream(new FileInputStream("classes.txt"));
			String line="";
			int i=0;
			while(i<86){
				line=dis.readLine();
				arr[0][i]=line.substring(0,3);
				arr[1][i]=line.substring(3);
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public String[][] getArr(){
		return arr;
	}
}
