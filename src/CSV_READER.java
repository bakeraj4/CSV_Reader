import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CSV_READER extends Thread{
	private String fileName="";
	private BufferedReader reader;
	private int numCourse=0;
	private CourseLib myLib=new CourseLib();
	//The follwing array of regex's don't take J-Term into consideration because we weren't given the csvfor it
	private String[] regexs={"First Semester","Fall Semester","Second Semester","Spring Semester","Summer Session", "Summer Term"};
	private DB_CONN myCONN;
	private static Object lock =new Object();
	
	
	CSV_READER(String fName){
		fileName=fName;	
		myCONN=new DB_CONN();
	}
	
	public void start(){
		readData();
	}
	
	public void readData(){
		String str="";
		Course tmp = null;
		String sem="";
		try {
			//to get the semester
			reader=new BufferedReader(new FileReader(fileName));
			reader.readLine();
			reader.readLine();
			str=reader.readLine();
			Pattern sem_pattern;
			Matcher sem_match;
			boolean found=false;
			for(int i=0;i<regexs.length&&!found;i++){
				sem_pattern=Pattern.compile(regexs[i]);
				sem_match=sem_pattern.matcher(str);
				if(sem_match.find()){
					sem=sem_match.group();
					if(i%2==0){//This makes things consistent. The registar changes between them
						sem=regexs[i+1];
					}
					int index =str.indexOf(sem)+sem.length()+2;
					String remainder=str.substring(index);
					index=remainder.indexOf("\"");
					sem+=(" "+this.fileName.substring(5,9));
					found=true;
				}
			}
			//real data
			reader=new BufferedReader(new FileReader(fileName));
			while((str=reader.readLine())!=null){
				Pattern pattern= Pattern.compile("^[A-Z][A-Z][A-Z] [0-9][0-9][0-9]");
				Matcher match=pattern.matcher(str);
				if(match.find()){
					tmp=new Course();
					tmp.setSem(sem);
					//hanldes name's and others
					//dept # section Name Course title
					int dataIndex=0;//0 to 5 to represent the attribute curently parsing no correlation to i b/c some take multiple indexes
					String[] theLine=str.split(new String("\\s+"));
					String profName="",courseName="";
					for(int i =0;i<theLine.length;i++){
						if(dataIndex==0){
							tmp.setAbbriavtion(theLine[i]);
							dataIndex++;
						}
						else if(dataIndex==1){
							tmp.setCourseNum(theLine[i]);
							dataIndex++;
						}
						else if(dataIndex==2){
							tmp.setSection(theLine[i]);
							dataIndex++;
						}
						else if(dataIndex==3){
							profName+=theLine[i];
							i++;
							profName+=", "+theLine[i];
							i++;
							System.out.println(profName);
							System.out.println(theLine[i]);
							if(theLine[i].contains(".")){
								profName+=" "+theLine[i];
								i++;
							}
							dataIndex++;
							tmp.setInstructor(profName);
						}
						else if(dataIndex==4){
							i--;
							courseName+=theLine[i];
							i++;
							for(;i<theLine.length;i++){
								if(!theLine[i].contains(",")){
									courseName+=" "+theLine[i];
								}
								else{
									int loc=theLine[i].indexOf(',');
									courseName+=" "+theLine[i].substring(0, loc);
									i+=theLine.length;
								}
							}
							dataIndex++;
							tmp.setCourseTitle(courseName);
						}
					}
					str=reader.readLine();//A+, A, ....F
					str=reader.readLine();//# occurances
					String[] line=str.split(",");
					for(int i=0;i<21;i++){
						if(!line[i].equals(" ")){
							tmp.setGrade(i-1, Integer.parseInt(line[i]));
						}
					}
					str=reader.readLine();//% occurances
					line=str.split(",");
					for(int i=1;i<22;i++){
						tmp.setDist(i-1, Double.parseDouble(line[i]));
					}
					tmp.calulateGPA();
					myLib.addCourse(tmp);
					//synchronized(lock){
						//myCONN.insertIntoTabe(tmp);
						System.out.println(tmp.getCourseTitle()+"\t"+tmp.getInstructor());
					//}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		//System.out.println(myLib.toString());
	}

	public int getNumCourse() {
		numCourse=myLib.getCourses().size();
		return numCourse;
	}
	
	public String getLibStr(){
		return fileName+"\n"+myLib.toString();
	}
}
