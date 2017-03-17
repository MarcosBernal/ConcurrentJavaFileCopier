package masterInt.CandP.exo2;

/** 
 *  <h1>MemorySlot</h1>
 *  The class MemorySlot manages the access to local memory assigned to a reader or writer
 *  process.
 *  <p>
 *  The MemorySlot contains the name of the related file and its original size. With the 
 *  slot_memory a portion of the main memory is referenced, previous allocation. The access
 *  to the memory slot is managed through the state of the memory slot(@see SLOT_STATE) and
 *  the assignment to a reader process variable.
 *  <p>
 *  The mutual exclusion and atomicity is ensured through the synchronized methods and blocks
 *  and the methods wait() and notifyAll() used in write and read.
 *  
 *    
 *  @author  Marcos Bernal   
 */
public class MemorySlot{
	
    public enum SLOT_STATE {
        ALLOCATING,
        WRITTING,
        READING,
        DEALLOCATING;
    }
	
	private String namefile;
    private int file_total_size;
    private SLOT_STATE state;
	private byte [] slot_memory;
    private boolean assigned_to_reader;

    
    public MemorySlot(String namefile, int byte_length, int file_total_size){
    	this.namefile = namefile;
    	this.slot_memory = new byte[byte_length];
    	this.file_total_size = file_total_size;
    	this.state = SLOT_STATE.ALLOCATING;
    	this.assigned_to_reader = false;
    }

    public void startingUse (){
    	this.state = SLOT_STATE.WRITTING;
    }
    
    public void finishingUse (){
    	this.state = SLOT_STATE.DEALLOCATING;
    }
   
    
    public int write(byte [] data, int index){
    	
    	while(this.state != SLOT_STATE.WRITTING)
			try {
				synchronized(this.slot_memory) { this.slot_memory.wait();}
			} catch (InterruptedException e) { e.printStackTrace(); }
    	
    	int i = 0;
        for(i = 0; i < this.slot_memory.length && (index + i) < (data.length) ; i++)
        	this.slot_memory[i] = data[index+i];
        	
        this.state = SLOT_STATE.READING;
        synchronized (this.slot_memory) {this.slot_memory.notifyAll();}
        
        
    	return (i+index);
    }
    
    public int read(byte [] data, int index){
        	while(this.state != SLOT_STATE.READING)
    			try {
    				synchronized (this.slot_memory) {this.slot_memory.wait();}
    			} catch (InterruptedException e) { e.printStackTrace(); }
        	
        	int i = 0;
            for(i = 0; i < this.slot_memory.length && (index + i) < (data.length); i++)
            	data[index+i] = this.slot_memory[i];
            	
            this.state = SLOT_STATE.WRITTING;
            synchronized (this.slot_memory) { this.slot_memory.notifyAll();}
            
        	return (i+index);
    }
    
    
    
    public int getFileTotalSize(){
    	return this.file_total_size;
    }
    
    public int getTotalAllocatedSize(){
    	return this.slot_memory.length;
    }
    
    public SLOT_STATE getState (){
    	return this.state;
    }
    
    public String getFileName(){
    	return this.namefile;
    }
    
    public boolean availableForAssigningToReader(){
    	if(this.assigned_to_reader)
    		return false;
    	assigned_to_reader = true;
    	return assigned_to_reader;
    }
}