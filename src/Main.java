
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] names={"data/201410 szrgrdt.csv",
					"data/201320 szrgrdt.csv",
					"data/201330 szrgrdt.csv",
					"data/201320 szrgrdt.csv",
					"data/201310 szrgrdt.csv",
					"data/201230 szrgrdt.csv",
					"data/201220 szrgrdt.csv",
					"data/201210 szrgrdt.csv"};//more needs to be added
		
		try {
				CSV_READER file=new CSV_READER(names[0]);
				file.start();
				System.out.print(file.getNumCourse());
				file.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
}
