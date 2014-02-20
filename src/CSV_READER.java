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
		try{
			Course tmpCourse = null;	
			boolean first=true;
			int linesRead=0;
			int deptIndex=0;
			int courseLine=0;
			reader=new BufferedReader(new FileReader(fileName));
			String str="";
			while((str=reader.readLine())!=null&&linesRead<10){
				if(!encounteredDept[deptIndex]){//hasn't found the line to start a department
					//System.out.println(myLib.getArr()[0][deptIndex]+myLib.getArr()[1][deptIndex]);
					if(str.contains(myLib.getArr()[0][deptIndex]+myLib.getArr()[1][deptIndex])){//there is an extra space in the second term
						encounteredDept[deptIndex]=true;
					}
				}
				else{
					//TODO need to stupid check it.
						//miami plan
						//course Total
						//department total
					if(courseLine==0){
						tmpCourse=new Course();
						//add the : AAA 201   A  Mannur Anita     Intro to Asian/ Asian Amer,Y,N,,,,,,,,,,,,,,,,,,,,
						courseLine++;
					}
					else if(courseLine==1){
						//skip it
						courseLine++;
					}
					else if(courseLine==2){
						//add this  ",0,12,8,3,4,7,0,3,0,2,1,0,2,0,0,0,0,0,0,0,0,3.05"
						courseLine++;
					}
					else if(courseLine==3){
						//add this %,0,28.6,19,7.1,9.5,16.7,0,7.1,0,4.8,2.4,0,4.8,0,0,0,0,0,0,0,0,
						courseLine=0;
						tmpCourse.calulateGPA();
						myLib.addCourse(tmpCourse);
					}
				}
					
				System.out.println(str);
				
				linesRead++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println();
		System.out.print(myLib.toString());
	}
}
