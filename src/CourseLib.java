import java.util.ArrayList;

public class CourseLib {
	private ArrayList<Course> courses=new ArrayList<Course>(); 	
	
	CourseLib(){
	}
	
	public void addCourse(Course course){
		courses.add(course);
	}
	
	public ArrayList<Course> getCourses(){
		return courses;
	}
	
	public String toString(){
		String ret="";
		for(Course course: courses)
			ret+=course.toString()+"\n";
		return ret;
	}
}
