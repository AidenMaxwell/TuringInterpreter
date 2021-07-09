package BusyBeaver;

import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

public class TuringMachine {
	//the current state
	public int state;
	
	//the TM's rules
	public List<TuringInstruction> rules;
	
	//the infinite tape
	public List<Boolean> tape;
	
	//the position of the head on the tape
	public int headPosition;
	
	//the original position of the head
	public int origin;
	
	//the lists of colors for their corresponding characters when printing the PNG image
	public Hashtable<Character, Color> colors = new Hashtable<Character, Color>();
	
	//the current printed character (for debug purposes)
	public char currentPrinting;
	
	//the history of all states leading up to the HALT state
	public List<Integer> stateLog;
	
	//the halt context tree
	public List<ContextBranch> ctxTree;
	
	public TuringMachine()
	{
		Reset();
		InitColors();
		
		rules = new ArrayList<TuringInstruction>();
	}
	
	public void InitColors()
	{
		//The colors correspond to the characters on the tape history
		colors.put('-', Color.WHITE);
		colors.put('+', Color.BLACK);
		colors.put('o', Color.CYAN);
		colors.put('0', Color.BLUE);
		colors.put('\n', Color.WHITE);
	}
	
	public boolean Step()
	{
		return Step(true);
	}
	
	public boolean Step(boolean verbose)
	{
		//stop if no rules
		if(rules.isEmpty())
		{
			if(verbose)
				System.out.println("Turing Machine has no rules! <" + this.hashCode() + ">");
			return false;
		}
		
		//stop if in halt state
		if(state < 1)
		{
			if(verbose)
				System.out.println("Turing Machine Halted! <" + this.hashCode() + ">");
			return false;
		}

		//add state to state log
		stateLog.add(state);
		//interpret the instruction corresponding to the current state
		InterpretInstruction(rules.get(state - 1));
		return true;
	}
	
	public ContextBranch GetLastCtxBranch()
	{
		CreateCtxTree();
		
		boolean[] stopped = new boolean[ctxTree.size()];
		
		//loop through state history
		for(int s = 0; s < stateLog.size(); s++)
		{
			int count = 0;
			int last = 0;
			
			//find context branch that matches state history
			for(int i = 0; i < stopped.length; i++)
			{
				if(!stopped[i])
				{
					count++;
					last = i;
				}
			}
			
			//only return correct context branch
			for(int c = 0; c < stopped.length; c++)
			{
				if(ctxTree.get(c).states.size() - 2 - s < 0)
					break;
				
				if(!stopped[c])
				{
					if(ctxTree.get(c).states.get(ctxTree.get(c).states.size() - 2 - s) != stateLog.get(stateLog.size() - 1 - s))
						stopped[c] = true;
				}
			}
			
			if(count == 1)
				return ctxTree.get(last);
		}
		
		return null;
	}
	
	public void CreateCtxTree()
	{
		ctxTree = new ArrayList<ContextBranch>();
		
		HaltContext hc = new HaltContext(null, this, null);
		
		hc.FindChildren();
		
		hc.AddTreeTo(ctxTree);
	}
	
	public String toString()
	{
		String toRet = "";
		
		//Show origin and current head position
		for(int i = 0; i < tape.size(); i++)
		{
			char toAdd = ' ';
			
			if(i == origin)
				toAdd = '+';
			if(i == headPosition)
				toAdd = 'v';
			
			toRet += toAdd;
		}

		toRet += "\n";
		
		//Show current tape
		for(Boolean b : tape)
		{
			toRet += b ? 1 : 0;
		}
		
		//Show the current state
		toRet += "\nCurrent state: " + state;
		
		return toRet;
	}
	
	public String RuleCard()
	{
		String toRet = "Rules:";
		
		for(int i = 0; i < rules.size(); i++)
		{
			toRet += "\nState " + i + ": " + rules.get(i).toVerboseString() + " (" + rules.get(i).base + ")";
		}
		
		return toRet;
	}
	
	public void InterpretInstruction(TuringInstruction ti)
	{
		if(!tape.get(headPosition))
		{
			//write the specified digit to the tape
			if(ti.digitWrite0 != null)
				tape.set(headPosition, ti.digitWrite0);

			//move in the specified direction
			switch(ti.move0)
			{
			case 'L':
				headPosition--;
				break;
			case 'R':
				headPosition++;
				break;
			case 'N':
				break;
			}
			
			//change to the specified state
			state = ti.state0;
		}
		else
		{
			if(ti.digitWrite1 != null)
				tape.set(headPosition, ti.digitWrite1);

			switch(ti.move1)
			{
			case 'L':
				headPosition--;
				break;
			case 'R':
				headPosition++;
				break;
			case 'N':
				break;
			}
			
			state = ti.state1;
		}
		
		CheckForEdge();
	}
	
	public void CheckForEdge()
	{
		//add padding to the tape if nearing the edge
		if(headPosition == 0) 
		{
			tape.add(0, false);
			headPosition++;
			origin++;
		}
		else if(headPosition == tape.size() - 1) 
		{
			tape.add(false);
		}
	}
	
	public void Reset()
	{
		state = 1;
		
		tape = new ArrayList<Boolean>();
		for(int i = 0; i < 3; i++)
		{
			tape.add(false);
		}
		
		origin = 1;
		headPosition = 1;
		
		stateLog = new ArrayList<Integer>();
	}
	
	public String CreateChart(int maxSteps)
	{
		return CreateChart(maxSteps, true, 0);
	}
	
	public String CreateChart(int maxSteps, boolean verbose, int clampHeight)
	{
		Reset();
		
		boolean infinite = maxSteps == 0;
		
		//initialize tape history
		List<TapeSegment> history = new ArrayList<TapeSegment>();
		
		//do steps and add results to history
		for(int i = 1; i <= maxSteps || infinite; i++)
		{
			if(!Step(false))
				break;
			
			TapeSegment ts = new TapeSegment(tape, origin, headPosition);
			
			history.add(ts);
			
			if(infinite && i % 10 == 0)
				System.out.println("Step " + i);
		}

		System.out.println("Fixing tape...");
		
		//pad out all of the tapes to match the last one
		int historyO = history.get(history.size() - 1).origin;
		int historyS = history.get(history.size() - 1).tape.size();
		
		int skip = 1;
		if(clampHeight > 0)
			skip = history.size() / clampHeight;
		
		int lastPercent = 0;

		StringBuffer toRet = new StringBuffer(verbose ? "Tape history:\n" : "");

		//skip some lines to match the chart
		for(int p = 0; p < history.size(); p += skip)
		{
			//offset tape
			history.get(p).offset(historyO, historyS);

			//display percentage of tape fixed
			int percent = (int)((p / (double)history.size()) * 100);
			
			if(percent > lastPercent)
			{
				System.out.println(percent + 1 + "% fixed");
				lastPercent = percent;
			}
			
			toRet.append(history.get(p).skipString(skip));
			toRet.append('\n');
		}
		
		return toRet.toString();
	}
	
	public String PrintPNG(String path, int maxSteps, int clampHeight, boolean clamp) throws IOException
	{
		if(!clamp)
			clampHeight = 0;
		
		//create a chart to base the image on
		String start = CreateChart(maxSteps, false, clampHeight);
		
		int width = start.indexOf("\n") + 1;
		int height = start.length() / width;
		
		System.out.println("Printing image of width " + width + " and height " + height);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int lastPercent = 0;
		
		//add each line to image
		for(int w = 0; w < width; w++)
			for(int h = 0; h < height; h++)
			{
				int p = h * width + w;
				
				int percent = p / (width * height) * 100;
				
				//increase percentage displayed
				if(percent > lastPercent)
				{
					System.out.println(percent + 1 + "% printed");
					lastPercent = percent + 1;
				}
				
				//match correct color to character on tape
				currentPrinting = start.charAt(p);
				if(colors.containsKey(start.charAt(p)))
					img.setRGB(w, h, (colors.get(start.charAt(p)).getRGB()));
				else
					img.setRGB(w, h, (Color.WHITE).getRGB());
			}
		
		//write image to file
		File outputfile = new File(path);
		if(outputfile.exists())
			outputfile.delete();
	    ImageIO.write(img, "png", outputfile);
		
		currentPrinting = '/';
		
		return start;
	}
}
