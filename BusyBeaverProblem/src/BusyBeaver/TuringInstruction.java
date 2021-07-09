package BusyBeaver;

public class TuringInstruction {
	//the digit to write on the tape
	public Boolean digitWrite0, digitWrite1;
	
	//the direction to move
	public char move0, move1;
	
	//the state to change to
	public int state0, state1;
	
	//the string used to initialize the instruction
	public String base;
	
	public String toString()
	{
		return 
			"(0): " + (digitWrite0 ? 1 : 0) + (move0) + state0 +
			" (1): " + (digitWrite1 ? 1 : 0) + (move1) + state1;
	}
	
	public String toVerboseString()
	{
		return 
			"If 0, write digit " + digitWrite0 + ", move " + move0 + ", and enter state " + state0 +
			". If 1, write digit " + digitWrite1 + ", move " + move1 + ", and enter state " + state1 + ".";
	}
	
	public TuringInstruction(String start)
	{
		int phase = 0;
		String tempSlot = "";
		base = start;
		
		//interpret all parts of base string
		
		for(char c : start.toCharArray())
		{
			switch(phase)
			{
			case 0:
				//in first phase, record digit to write
				if(Character.isDigit(c))
					tempSlot += c;
				else
				{
					if(tempSlot.isEmpty())
						//possible to write no digits
						digitWrite0 = null;
					else
						digitWrite0 = Integer.valueOf(tempSlot) == 1;
					tempSlot = "";
					//record movement direction
					move0 = Character.toUpperCase(c);
					phase++;
				}
				break;
			
			case 1:
				//record state to change to
				if(Character.isDigit(c))
					tempSlot += c;
				else
				{
					if(Character.toLowerCase(c) == 'h')
					{
						//recognize halt state
						state0 = 0;
						tempSlot = "";
						phase++;
					}
					else 
					{
						state0 = Integer.valueOf(tempSlot);
						phase++;
						tempSlot = "";
					}
				}
				break;
				//repeat after underscore
			case 2:
				if(Character.isDigit(c)) 
					tempSlot += c;
				else
				{
					if(c == '_') 
					{
						break;
					}

					if(tempSlot.isEmpty())
						digitWrite1 = null;
					else
						digitWrite1 = Integer.valueOf(tempSlot) == 1;
					tempSlot = "";
					move1 = Character.toUpperCase(c);
					phase++;
				}
				break;
				
			case 3:
				if(Character.isDigit(c))
					tempSlot += c;
				else if(Character.toLowerCase(c) == 'h')
				{
					tempSlot = "0";
				}
				break;
			}
		}

		state1 = Integer.valueOf(tempSlot);
	}
}
