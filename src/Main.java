
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
					"data/201210 szrgrdt.csv"};//more needs to be added, 200810 to 201410
		
		try {
			CSV_READER[] files=new CSV_READER[names.length];
			for(int i=0;i<names.length;i++){
				files[i]=new CSV_READER(names[i]);
				files[i].start();
			}
			for(int i=0;i<names.length;i++){
				System.out.println(files[i].getNumCourse());
				files[i].join();
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
}
