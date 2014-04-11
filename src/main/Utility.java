package main;
import java.io.*;
import java.util.*;
public class Utility {

	public static List<String> readFile(String fileName)
	{
		File file = new File(getCurrentDir()+fileName);
		List <String> messageList = new ArrayList<String>();
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream(file);
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine = null ;
			  
			  while ((strLine = br.readLine()) != null)   {
					 messageList.add(strLine);
			  }
			  //Close the input stream
			  in.close();
                          
			    }
				catch (Exception e){//Catch exception if any
			  	System.err.println("Error: " + e.getMessage());
				
			  }
	return messageList;
	}
	
	public static boolean writeFile(String fileName , String message)
	{
		File file = new File(getCurrentDir()+fileName);
		
		 try{
			  // Create file 
			  FileWriter fstream = new FileWriter(file,true);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(message);
			  out.newLine();
			  //Close the output stream
			  out.close();
			  return true;
			  }catch (Exception e)
			  {
				  //Catch exception if any				  
				  return false;
			  }
			  
		
	}
	
	
	public static String getCurrentDir()
	{
		return "E:\\ACNProjectNetbeans\\src\\main\\";
	}
	
	
	
//	public static void main(String a[]){
//		
//		System.out.println(readFile(new File(getCurrentDir()+"x.txt")));
//		System.out.println(writeFile(new File(getCurrentDir()+"y.txt"),"shruti"));
//			
//	}
}
