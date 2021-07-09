package BusyBeaver;
import java.util.*;

public class TapeSegment {
	//tape must be mutable
	public List<Character> tape;

	//the zero position, which can change during execution
	public int origin;
	
	public TapeSegment(List<Boolean> bools, int origin, int headPosition)
	{
		this.origin = origin;
		
		tape = new ArrayList<Character>();
		
		//add tape markings, with special markings for position of head
		for(int i = 0; i < bools.size(); i++)
		{
			tape.add(bools.get(i) ? '+' : '-');
			
			if(i == headPosition)
				tape.set(i, bools.get(i) ? '0' : 'o');
		}
	}
	
	public void offset(int origin, int size)
	{
		//add padding to left and right of tape to match tape history
		
		while(origin > origin)
		{
			origin++;
			tape.add(0, '-');
		}
		
		while(size > tape.size())
		{
			tape.add('-');
		}
	}
	
	public String toString()
	{
		return String.valueOf(tape);
	}
	
	public String skipString(int skip)
	{
		//returns shrunk string to save filesize and RAM
		
		char[] temp = new char[tape.size() / skip + 1];
		
		int l = 0;
		for(int i = 0; i < tape.size(); i += skip)
		{
			temp[l] = tape.get(i);
			l++;
		}
		
		return new String(temp);
	}
}
