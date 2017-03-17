package masterInt.CandP.exo2;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/** 
 *  <h1>Reader</h1>
 *  The class Reader has to read from a memory shared with Writer threads and write the content (Files)
 *  in to disk in the folder passed by parameter to the constructor.
 *  <p>
 *  The memory is organized in slots. Each Reader has a slot since the operating system blocks the writing 
 *  file access to one writing process at a time. 
 *  After the Reader reads all data from the slot (an slot could have less space than the file, hence several readings)
 *  the slot is marked as deallocating and it is release of the main memory.
 *  <p>
 *  Finally the content is stored in the folder(String) passed by argument in the constructor. As the content came from 
 *  a file, it is stored in another file with the same name which was kept in the slot. 
 *
 *  @author  Marcos Bernal   
 */
public class Reader implements Runnable{
	private String folder_path;
	private byte[] data_from_file;
	private Memory main_memory;
	private MemorySlot buffer;
	
	public Reader(String folder_path, Memory main_memory){
		this.folder_path = folder_path;
		this.main_memory = main_memory;
		this.data_from_file = null;
	}
	
	@Override
	public void run() {
		
		this.buffer = main_memory.getMemorySlot();
		this.data_from_file = new byte [this.buffer.getFileTotalSize()];
		
		int index = 0;
		do{
		index = this.buffer.read(data_from_file, index);
		}
		while(index != this.buffer.getFileTotalSize());
		
		String file_name = this.buffer.getFileName(); 
		this.buffer.finishingUse();
		this.main_memory.releasingSlot(buffer);
		
		//Do stuff
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(folder_path+"/"+file_name);
			fos.write(this.data_from_file);
			fos.close();
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		
	}
}