package masterInt.CandP.exo2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/** 
 *  <h1>Main Program</h1>
 *  The class contains the main program that coordinates all the other classes to create the 
 *  main program.
 *  <p>
 *  To use this program the strings "copy_path_folder" and "origin_path_folder" (@see copy_path_folder) 
 *  (@see origin_path_folder) needs to be set in the desired location. 
 *  <p>
 *  (i.e in ubuntu "/home/marcos/Dropbox/UNS Nice/Parallelism/Practical LABs/tp2/DataSet/Original Data")
 *    
 *  @author  Marcos Bernal
 *  @author  Lokesh Gupta   
 */
public class Exo2 {

	public static void main (String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, DigestException{
		
		System.out.println("Starting...");
		
		String copy_path_folder = 
				"/home/marcos/Dropbox/UNS Nice/Parallelism/Practical LABs/tp2/DataSet/Copied Data";
		
		String origin_path_folder = 
				"/home/marcos/Dropbox/UNS Nice/Parallelism/Practical LABs/tp2/DataSet/Original Data";
		
		File[] files = new File(origin_path_folder).listFiles();
		
		Writer[] writers = new Writer[files.length];
		Thread[] writers_thread = new Thread[files.length];
		
		Reader[] readers = new Reader[files.length];
		Thread[] readers_thread = new Thread[files.length];
		
		Memory buffered_memory = new Memory(1000);
			
		for(int i = 0; i < files.length;i++){
			writers[i] = new Writer(files[i].toPath(), buffered_memory);
			writers_thread[i] = new Thread(writers[i]);
			readers[i] = new Reader(copy_path_folder, buffered_memory);
			readers_thread[i] = new Thread(readers[i]);
		}
		
		for(int i = 0; i < writers.length;i++){
			writers_thread[i].start();
		    readers_thread[i].start();
		}
				
		for(int i = 0; i < writers.length;i++){
			writers_thread[i].join();
		    readers_thread[i].join();
		}
		
		System.out.println("Everything works correctly, you should check the folder \n"+copy_path_folder);
	    
		MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");
		
		files = new File(origin_path_folder).listFiles();
		System.out.println("SHA ORIGIN Value: " + getFilesChecksum(shaDigest,files));
		
		
		shaDigest.reset();
		files = new File(copy_path_folder).listFiles();
		System.out.println("SHA COPIED Value: " + getFilesChecksum(shaDigest,files));
	}
	
	//Partially modified from 
	//http://howtodoinjava.com/core-java/io/how-to-generate-sha-or-md5-file-checksum-hash-in-java/
	private static String getFilesChecksum(MessageDigest digest, File[] files) throws IOException
	{
		sortingFilesBySize(files);

		String checksum = "";

		for(File file : files){
			//Get file input stream for reading the file content
			FileInputStream fis = new FileInputStream(file);

			//Create byte array to read data in chunks
			byte[] byteArray = new byte[1024];
			int bytesCount = 0; 

			//Read file data and update in message digest
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			};

			//close the stream; We don't need it now.
			fis.close();

			//Get the hash's bytes
			byte[] bytes = digest.digest();

			//This bytes[] has bytes in decimal format;
			//Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			checksum = checksum + sb.toString();
		} 
		//return complete hash
		digest.reset();
		
		digest.update(checksum.getBytes(), 0, checksum.length());
		byte[] bytes = digest.digest();
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
		{
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		return sb.toString();
	}
	
	private static void sortingFilesBySize(File[] files){
		for(int i = 0; i < files.length-1; i++)
			for(int j = i+1; j < files.length;j++)
				if(files[i].length() >= files[j].length()){
					File tmp = files[i];
					files[i]=files[j];
					files[j]=tmp;
				}
	}
}
