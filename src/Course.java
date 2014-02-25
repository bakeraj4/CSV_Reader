
public class Course {
	private String abbriavtion="", instructor="", courseTitle="",section="",courseNum="";
	private double gpa= -1.0;
	private int[]gradeOccurance=new int[21];// 13= (a -d w/ +-) 3=W, WP, WF 5=I, X, Y, P, S
	private double[]gradeDist =new double[21];
	private String sem="";
	Course(){
		//default constructor
	}

	public String toString(){
		//will change later for more details
		return abbriavtion+""+courseNum+" "+section+" "+" is tought by "+instructor+" and had a "+gpa+" gpa, during the "+sem+".";
	}
	
	public String getAbbriavtion() {
		return abbriavtion;
	}

	public void setAbbriavtion(String abbriavtion) {
		this.abbriavtion = abbriavtion;
	}

	public String getCourseNum() {
		return courseNum;
	}

	public void setCourseNum(String courseNum) {
		this.courseNum = courseNum;
	}

	public String getInstructor() {
		return instructor;
	}

	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public int[] getGradeOccurance() {
		return gradeOccurance;
	}

	public void setGradeOccurance(int[] gradeOccurance) {
		this.gradeOccurance = gradeOccurance;
	}

	public double[] getGradeDist() {
		return gradeDist;
	}

	public void setGradeDist(double[] gradeDist) {
		this.gradeDist = gradeDist;
	}

	public void setGrade(int i, int grade){
		gradeOccurance[i]=grade;
	}
	
	public void setDist(int i, double dist){
		gradeDist[i]=dist;
	}
	
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void calulateGPA() {
		double numPoints=0.0;
		int numPeople=0;
		double[] points={4.0,4.0,3.7,3.3,3.0,2.7,2.3,2.0,1.7,1.3,1.0,0.7,0.0};
		for(int i=0;i<points.length;i++){
			numPoints+=(points[i]*gradeOccurance[i]);
			numPeople+=gradeOccurance[i];
		}
		gpa=(numPoints/numPeople);
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}
}
