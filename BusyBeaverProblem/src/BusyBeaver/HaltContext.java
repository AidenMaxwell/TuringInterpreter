package BusyBeaver;

import java.util.*;

public class HaltContext {
	//The instruction and state represented
	public TuringInstruction instruction;
	public int state;
	
	//The states that this instruction leads to
	public List<HaltContext> children;
	
	//The state that this instruction comes from
	public HaltContext parent;
	
	public TuringMachine owner;
	
	public HaltContext(TuringInstruction ti, TuringMachine tm, HaltContext parent)
	{
		instruction = ti;
		
		if(instruction != null)
		{
			//Find the state for instruction
			state = tm.rules.indexOf(ti) + 1;
		}
		else
		{
			state = 0;
		}
		
		owner = tm;
		
		this.parent = parent;
		
		children = new ArrayList<HaltContext>();
	}
	
	public void FindChildren()
	{
		//Look in owner's rules for states coming from this one
		for(TuringInstruction ti : owner.rules)
		{
			if(ti.state0 == state || ti.state1 == state)
			{
				HaltContext hc = new HaltContext(ti, owner, this);
				children.add(hc);
				
				//do this recursively unless it would lead to a SO
				if(!FamilyContainsInstruction(ti))
					hc.FindChildren();
			}
		}
	}
	
	public boolean FamilyContainsInstruction(TuringInstruction ti)
	{
		if(parent == null)
			return false;
		
		return instruction == ti || parent.FamilyContainsInstruction(ti);
	}
	
	public List<HaltContext> CreateBranch(List<HaltContext> start)
	{
		//only start with leaf nodes
		if(!children.isEmpty() && start.isEmpty())
			return null;
		
		//travel up towards root node, creating branches
		start.add(this);
		
		if(parent == null)
			return start;
		
		return parent.CreateBranch(start);
	}
	
	public void AddTreeTo(List<ContextBranch> start)
	{
		//Add all branches to halt context tree
		if(!children.isEmpty())
		{
			for(HaltContext hc : children)
				hc.AddTreeTo(start);
		}
		
		ContextBranch cb = new ContextBranch(CreateBranch(new ArrayList<HaltContext>()));
		
		if(!cb.branch.isEmpty())
			start.add(cb);
	}
}
