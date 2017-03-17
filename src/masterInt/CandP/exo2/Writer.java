package masterInt.CandP.exo2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
/** 
 *  <h1>Writer</h1>
 *  The class Writer has to read from a file of the system and write its bytes into 
 *  a memory shared with Reader threads.
 *  <p>
 *  The memory is organized in slots. Each Writer has a slot since the operating system
 *  blocks the writing file access to one writing process at a time. 
 *  After the Writer reads all data from the file, allocate a buffer(MemorySlot) in the 
 *  main memory asking the size of the file. 
 *  The main_memory returns a slot that can the space required or less. After setting 
 *  the flag, the Writer starts to write the content of the file in the buffer 
 *  (can be several operations, so the index is stored).
 *  <p>
 *  When all content is sent(index is equal to size of the file) the Writer notifies the user with a system.out.println and ends.
 *  
 *  @author  Marcos Bernal   
 */
public class Writer implements Runnable{
	private Path file_path;
	private File file;
	byte[] data_from_file;
	Memory main_memory;
	MemorySlot buffer;
	
	public Writer(Path path, Memory main_memory){
		this.file_path = path;
		this.main_memory = main_memory;
		this.data_from_file = null;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!Files.isRegularFile(file_path) || !Files.exists(file_path)){
			System.out.println("Writer has NOT found file in: "+file_path.toString());
			return;
		}
		
		file = new File(file_path.toString());
		try {
			this.data_from_file = Files.readAllBytes(file_path);
		} catch (IOException e) { e.printStackTrace(); }
		
		//Do stuff System.out.println("Writer has the file: "+file.getName() + " size: " + this.data_from_file.length);
		
		this.buffer = main_memory.createSlot(file.getName(), this.data_from_file.length);
		this.buffer.startingUse();
		
		int index = 0;
		do{
		index = this.buffer.write(data_from_file, index);
		}
		while(index != this.data_from_file.length);
		
		
		System.out.println("Writing finishes "+ index);
	}

	
}
