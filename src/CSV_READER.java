import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
	private final static Object lock =new Object();
	private static int courseNum=0;
	
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
							if(theLine[i].length()<2){//there are courses like line # 24167 in the fall 2104 doc that had a floating A an im ignoring it
								i++;
							}
							profName+=theLine[i];
							i++;
							if(profName.toLowerCase().equals("de")){
								profName+=" "+theLine[i];
								i++;
							}
							profName+=", "+theLine[i];
							i++;
							if(theLine[i].contains(".")&&theLine[i].length()==2){
								profName+=" "+theLine[i];
								i++;
							}
							
							dataIndex++;
							i--;
							tmp.setInstructor(profName);
						}
						else if(dataIndex==4){
							int n=inDoubleArr(theLine[i]);
							if(theLine[i].equals("Carlos")){
								i+=2;
								tmp.setInstructor(tmp.getInstructor()+" Carlos L.");
							}
							else if(theLine[i].equals("Ann")){
								i+=2;
								tmp.setInstructor(tmp.getInstructor()+" Ann M.");
							}
							else if(theLine[i].equals("Ana")){
								i+=2;
								tmp.setInstructor(tmp.getInstructor()+" Ana Maria");
							}
							else if(theLine[i].equals("Natalie")){
								i+=2;
								tmp.setInstructor(tmp.getInstructor()+" Natalie D.");
							}
							else if(theLine[i].equals("John")||theLine[i].equals("Mark")||theLine[i].equals("Elizabeth")){
								tmp.setInstructor(tmp.getInstructor()+" "+theLine[i]);
								i++;
								if(theLine[i].equals("E.")||theLine[i].equals("J.")){//they both could have a E. middle initail
									tmp.setInstructor(tmp.getInstructor()+" "+theLine[i]);
									i++;
								}
							}
							else if(n!=-1){
								tmp.setInstructor(tmp.getInstructor()+" "+theLine[i]);
								i++;
								for(int j=i+n;i<j;i++){
									tmp.setInstructor(tmp.getInstructor()+" "+theLine[i]);
								}
							}
							else if(inSingleArr(theLine[i])){
								tmp.setInstructor(tmp.getInstructor()+" "+theLine[i]);
								i++;
							}
							
							courseName+=theLine[i];
							i++;
							for(;i<theLine.length;i++){
									courseName+=" "+theLine[i];
							}
							
							int loc=courseName.indexOf(',');
							if(loc!=-1){
								courseName=courseName.substring(0, loc);
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
					synchronized(lock){
						myCONN.insertIntoTable(tmp,courseNum);
						courseNum++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		//System.out.println(myLib.toString());
	}

	
	private final Map<String,Integer> doubleNames=Collections.unmodifiableMap(new HashMap<String,Integer>(){{
		put("Wietse",1);
		put("Gomes",2);
		put("Chung",1);
		put("Buffy",1);
		put("Natalie",1);
		put("Garaza",2);
		put("Maria",1);
		put("Henry",1);
		put("Alana",1);
		put("Allison",1);
		put("Joyce",1);
		put("Kathryn",1);
		put("Jacqueline",1);
		put("Amy",1);
		put("Garza",2);
		put("Nikolai",1);
		put("Phillip",1);
		put("Merwe",2);
		put("Ellen",1);
		put("Puligadda",1);
		put("Nathan",1);
		put("Laura",1);
		put("Daniel",1);
		put("Teso",1);
		put("Benedicte",1);
	}});//how many additional tokens. so Aaron,1 means Aaron B. 
	
	private int inDoubleArr(String string){
		if(doubleNames.containsKey(string)){
			return doubleNames.get(string);
		}
		return -1;
	}
	
	private final String [] singleNames={"Asoka","Kerry","Jean","Spahr","Micheal","Barbara",
	"R.","Azizeh","Michele","Raisa","Eva","Steven","Katia","Sandra","Whitney","Lauren","Kathlenn",
	"Katie","Lee","Barbra","Feng","Sophie","Renee","Jane","Rachel","Brad","Guy","Michael",
	"William","William","Douglas","Hays","Sherman","Judith","Sandra","Sik","Rudy","Karen",
	"Christopher","Allen","Aurora","Peter","James","Allyn","John","Mark","Kathleen","Qing","Sidury",
	"David","Fay","Bob","Elijah","Maria-Carmen"};
	private boolean inSingleArr(String string) {
		for(String s: singleNames){
			if(s.equals(string)){
				return true;
			}
		}
		return false;
	}

	public int getNumCourse() {
		numCourse=myLib.getCourses().size();
		return numCourse;
	}
	
	public String getLibStr(){
		return fileName+"\n"+myLib.toString();
	}
}
