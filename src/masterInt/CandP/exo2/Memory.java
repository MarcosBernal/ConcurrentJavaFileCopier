package masterInt.CandP.exo2;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/** 
 *  <h1>Memory</h1>
 *  The class Memory manages the access to memory and allocate, provide and release 
 *  memory slots for writer and reader classes.
 *  <p>
 *  The memory is organized in slots. Each Writer or Reader has access to one slot since
 *  the operating system blocks the writing file access to one writing process at a time. 
 *  <p>
 *  Two semaphores are used for limiting the access to Writer (when creating slot) and 
 *  Reader(when looking for get one slot). To ensure atomicity and mutual exclusion the 
 *  available_space attribute is used as an AtomicReference object and the access to that 
 *  variable and the list of slots are synchronized ensuring mutual exclusion.
 *    
 *  @author  Marcos Bernal   
 */
public class Memory {
	
	private int memory_size;
	private AtomicReference<Integer> available_space;
	private Semaphore writing_sema;
	private Semaphore reading_sema;
	
	private ArrayList<MemorySlot> memory;
	
	public Memory(int byte_length){
		this.memory_size = byte_length;
		this.available_space = (new AtomicReference<Integer>());
		this.available_space.set(memory_size);
		System.out.println("Creating memory of "+ available_space);
		this.memory = new ArrayList<MemorySlot>();
		
		this.writing_sema = new Semaphore(0);
		this.reading_sema = new Semaphore(0);
	}
	
	public MemorySlot createSlot(String file_name, int file_size){
		int random_size = (int)(Math.random()*300);
		random_size = (random_size == 0) ? 100 : random_size;
		random_size = (file_size < random_size) ? file_size : random_size;
		
		MemorySlot slot = new MemorySlot(file_name, random_size, file_size); 
		
		synchronized(available_space) {
			while(available_space.get() < random_size)
				try {
					writing_sema.acquire();
				} catch (InterruptedException e) {	e.printStackTrace(); }

			synchronized(memory) { memory.add(slot); }
			this.available_space.set(available_space.get() - random_size);
		}
		this.reading_sema.release();
		System.out.println("Created Slot of size: "+random_size + " .Available memory "+ available_space);
		return slot;
	}
	
	public synchronized void releasingSlot(MemorySlot slot){		
		int allocated_size = slot.getTotalAllocatedSize();
		synchronized(memory) { memory.remove(slot); }			
		this.available_space.set(available_space.get() + allocated_size);
		if(this.writing_sema.availablePermits() < 1)
			this.writing_sema.release();
	}
	
	public MemorySlot getMemorySlot(){
		try {
			this.reading_sema.acquire();
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		synchronized(memory) {
		for(MemorySlot slot : memory)
			if(slot.availableForAssigningToReader())
				return slot;
		}
		return null;
	}
}