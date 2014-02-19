import java.io.BufferedReader;
import java.io.FileReader;


public class CSV_READER extends Thread{
	private String fileName="";
	private BufferedReader reader;
	private int lineCount=0;
	
	//TODO have a static vector of classes that is thread safe
	//the reasoning to use threading is to make the creation of dat faser soit can hande multiple csv's at a time
	
	
	CSV_READER(String fName){
		fileName=fName;
	}
	
	public void start(){
		readData();
	}
	
	public void readData(){
		try{
			reader=new BufferedReader(new FileReader(fileName));
			String line="";
			while((line=reader.readLine())!=null){
				System.out.println(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
