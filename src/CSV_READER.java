import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CSV_READER extends Thread{
	private String fileName="";
	private BufferedReader reader;
	private int lineCount=0;
	private CourseLib myLib=new CourseLib();
	//TODO have a static vector of classes that is thread safe
	//the reasoning to use threading is to make the creation of dat faser soit can hande multiple csv's at a time
	
	
	CSV_READER(String fName){
		fileName=fName;		
	}
	
	public void start(){
		readData();
	}
	
	public void readData(){
		String str="";
		int linesLeft=0;
		Course tmp = null;
		try{
			reader=new BufferedReader(new FileReader(fileName));
			while((str=reader.readLine())!=null){
				Pattern pattern= Pattern.compile("[A-Z][A-Z][A-Z] [0-9][0-9][0-9]");
				Matcher match=pattern.matcher(str);
				if(match.find()){
					if(str.indexOf(',')>4&& !str.contains("Total")){//CSE 999 line
						System.out.println(str);
						linesLeft=3;
						tmp=new Course();
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
								dataIndex++;
								tmp.setInstructor(profName);
							}
							else if(dataIndex==4){
								courseName+=theLine[i];
								i++;
								if(!theLine[i].equals("N")){
									courseName+=" "+theLine[i];
									i++;
								}
								dataIndex++;
								tmp.setCourseTitle(courseName);
							}
						}
					}
				}
				else if(linesLeft==3){//A+ A A-.......
					//skips
					linesLeft--;
				}
				else if(linesLeft==2){//# occurances
					String[] line=str.split(",");
					for(int i=0;i<21;i++){
						if(!line[i].equals(" ")){
							tmp.setGrade(i-1, Integer.parseInt(line[i]));
						}
					}
					linesLeft--;
				}
				else if(linesLeft==1){//percent
					String[] line=str.split(",");
					for(int i=1;i<22;i++){
						tmp.setDist(i-1, Double.parseDouble(line[i]));
					}
					tmp.calulateGPA();
					myLib.addCourse(tmp);
					linesLeft=0;
				}
			}
		}catch(Exception e){
		}
		System.out.println(myLib.toString());
	}
}
