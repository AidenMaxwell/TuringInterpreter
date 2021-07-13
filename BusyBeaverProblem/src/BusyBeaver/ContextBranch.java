package BusyBeaver;

import java.util.*;

public class ContextBranch {
	//The list of instructions leading up to a halt state.
	public List<HaltContext> branch;
	//The list of states of the instructions.
	public List<Integer> states;
	
	public ContextBranch(List<HaltContext> branch)
	{
		this.branch = branch;
		
		if(branch == null)
			this.branch = new ArrayList<HaltContext>();
		
		states = new ArrayList<Integer>();
		
		//add all of the contexts' states
		for(HaltContext hc : this.branch)
			states.add(hc.state);
	}
	
	public String toString()
	{
		String toRet = "";
		
		for(HaltContext hc : branch)
		{
			//print all instructions except HALT
			if(hc.instruction != null)
				toRet += "(" + hc.state + ") " + hc.instruction.base + " => ";
		}
		
		return toRet + "HALT";
	}
}
