import java.io.BufferedReader;
import java.io.FileReader;


public class CSV_READER extends Thread{
	private String fileName="";
	private BufferedReader reader;
	private int lineCount=0;
	private CourseLib myLib=new CourseLib();
	private Boolean[] encounteredDept;
	//TODO have a static vector of classes that is thread safe
	//the reasoning to use threading is to make the creation of dat faser soit can hande multiple csv's at a time
	
	
	CSV_READER(String fName){
		fileName=fName;
		encounteredDept=new Boolean[myLib.getArr()[0].length];
		for(int i=0;i<myLib.getArr()[0].length;i++){
			encounteredDept[i]=false;
		}
	}
	
	public void start(){
		readData();
	}
	
	public void readData(){
		int linesRead=0;
		String str="";
		try{
			Course tmpCourse = null;
			int deptIndex=0;
			int courseLine=0;
			reader=new BufferedReader(new FileReader(fileName));
			while((str=reader.readLine())!=null){
				if(!encounteredDept[deptIndex]){//hasn't found the line to start a department
					if(str.contains(myLib.getArr()[0][deptIndex]+myLib.getArr()[1][deptIndex])){//there is an extra space in the second term
						encounteredDept[deptIndex]=true;
					}
				}
				else{
					//System.out.println(str);
					if((" "+str).contains(myLib.getArr()[1][deptIndex])){
						//skip this non-useful line and skips the next three lines
						System.out.println(str);
						str=reader.readLine();
						System.out.println(str);
						str=reader.readLine();
						System.out.println(str);
						str=reader.readLine();
						System.out.println(str);
						deptIndex++;
					}
					else if(str.contains("Course Total")){
						//skip this non-useful line and skips the next three lines
						System.out.println(str);
						str=reader.readLine();
						System.out.println(str);
						str=reader.readLine();
						System.out.println(str);
						str=reader.readLine();
						System.out.println(str);
					}
					else if(str.contains("Miami Plan")){
						//skip the non-useful line
						System.out.println(str);
					}
					else{
						if(courseLine==0){
							tmpCourse=new Course();
							//hanldes name's and others
							//dept # section Name Course title
							int dataIndex=0;//0 to 5 to represent the attribute curently parsing no correlation to i b/c some take multiple indexes
							String[] theLine=str.split(new String("\\s+"));
							String profName="",courseName="";
							for(int i =0;i<theLine.length;i++){
								if(dataIndex==0){
									tmpCourse.setAbbriavtion(theLine[i]);
									dataIndex++;
								}
								else if(dataIndex==1){
									tmpCourse.setCourseNum(theLine[i]);
									dataIndex++;
								}
								else if(dataIndex==2){
									tmpCourse.setSection(theLine[i]);
									dataIndex++;
								}
								else if(dataIndex==3){
									profName+=theLine[i];
									i++;
									profName+=", "+theLine[i];
									i++;
									dataIndex++;
									tmpCourse.setInstructor(profName);
								}
								else if(dataIndex==4){
									courseName+=theLine[i];
									i++;
									if(!theLine[i].equals("N")){
										courseName+=" "+theLine[i];
										i++;
									}
									dataIndex++;
									tmpCourse.setCourseTitle(courseName);
								}
								
							}
							courseLine++;
						}
						else if(courseLine==1){
							//skip it, because it only lists A+, A, A- ....
							courseLine++;
						}
						else if(courseLine==2){
							//gets the actual distribution of students
							String[] line=str.split(",");
							for(int i=0;i<21;i++){
								if(!line[i].equals(" ")){
									tmpCourse.setGrade(i-1, Integer.parseInt(line[i]));
								}
							}
							courseLine++;
						}
						else if(courseLine==3){
							//gets the %'s
							String[] line=str.split(",");
							for(int i=1;i<22;i++){
								tmpCourse.setDist(i-1, Double.parseDouble(line[i]));
							}
							courseLine=0;
							tmpCourse.calulateGPA();
							myLib.addCourse(tmpCourse);
							System.out.println("line at "+linesRead);
						}
					}
				}	
				linesRead++;
			}
		}catch(Exception e){
			System.out.println(str);
		}
		
		System.out.println();
		System.out.println(linesRead);
		/*for(Course c: myLib.getCourses()){
			System.out.println(c.toString());
		}*/
		System.out.println(myLib.toString());
	}
}
