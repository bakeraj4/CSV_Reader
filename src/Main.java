import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Main {
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] names={"data/201410 szrgrdt.csv",
					"data/201330 szrgrdt.csv",
					"data/201320 szrgrdt.csv",
					"data/201310 szrgrdt.csv",
					"data/201230 szrgrdt.csv",
					"data/201220 szrgrdt.csv",
					"data/201210 szrgrdt.csv",
					"data/201130 szrgrdt.csv",
					"data/201120 szrgrdt.csv",
					"data/201110 szrgrdt.csv",
					"data/201030 szrgrdt.csv",
					"data/201020 szrgrdt.csv",
					"data/201010 szrgrdt.csv",
					"data/200930 szrgrdt.csv",
					"data/200920 szrgrdt.csv",
					"data/200910 szrgrdt.csv",
					"data/200830 szrgrdt.csv",
					"data/200820 szrgrdt.csv",
					"data/200810 szrgrdt.csv"};
		//200810 to 201410
		
		try {
			//DB_CONN.init();
			
			
			CSV_READER[] files=new CSV_READER[names.length];
			for(int i=0;i<names.length;i++){
				files[i]=new CSV_READER(names[i]);
				files[i].start();
			}
			//has a txt version for now
			//create the db file here and the tables to be used.
			BufferedWriter out=new BufferedWriter(new FileWriter("Results.txt"));
			PrintWriter pw =new PrintWriter(new FileWriter("Results.csv"));
			
			for(int i=0;i<names.length;i++){
				System.out.println(files[i].getNumCourse());
				files[i].join();
				out.write(files[i].getLibStr());
				pw.write(files[i].getLibStr());
			}
			out.close();
		} catch (InterruptedException e) {
				e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}