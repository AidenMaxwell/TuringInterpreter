package BusyBeaver;

import java.awt.*;
import org.json.*;
import java.io.*;
import java.util.*;

public class TuringInterpreter {

	public static void main(String[] args) {
		TuringMachine exampleTM = new TuringMachine();
		
		//creating variables
		
		String inputPath = "";
		int steps = 0;
		boolean printImg = false;
		boolean clamp = false;
		int cHeight = 0;
		boolean printCtx = false;
		String path = "";
		boolean printChart = false;
		boolean printAll = false;
		boolean printRules = false;
		
		//JSON reading
		
		boolean readJson = true;
		
		FileReader config = null;
		
		try
		{
			File configFile = new File("config.json");
			config = new FileReader(configFile);
		}
		catch(Exception e)
		{
			readJson = false;
		}

		JSONObject jo;
		
		if(readJson)
		{
			jo = new JSONObject(new JSONTokener(config));
			
			inputPath = jo.getString("inputPath");
			steps = jo.getInt("runSteps");
			printImg = jo.getBoolean("createImage");
			path = jo.getString("outputPath");
			clamp = jo.getBoolean("clamp");
			cHeight = jo.getInt("clampHeight");
			printCtx = jo.getBoolean("printHaltContext");
			printChart = jo.getBoolean("printTapeHistory");
			printAll = jo.getBoolean("printRealTimeSteps");
			printRules = jo.getBoolean("printRules");
		}
		
		//manual entry
		
		if(!readJson)
		{
			Scanner scan = new Scanner(System.in);
			
			System.out.print("Input path of .bbtm file: ");
			inputPath = scan.nextLine();
			
			System.out.print("Run for how many steps? (0 to run until stopped): ");
			steps = scan.nextInt();
			
			System.out.print("Create PNG image of tape history?: ");
			printImg = scan.nextBoolean();
			
			System.out.print("Clamp width/height of image or chart?: ");
			clamp = scan.nextBoolean();
			
			if(clamp)
			{
				System.out.print("Clamped height: ");
				cHeight = scan.nextInt();
			}
			
			System.out.print("Print halt context branches?: ");
			printCtx = scan.nextBoolean();
			
			scan.nextLine();
			
			if(printImg) 
			{
				System.out.print("Output path: ");
				path = scan.nextLine();
			}
			
			System.out.print("Print all steps?: ");
			printAll = scan.nextBoolean();
			
			System.out.print("Print tape history as text?: ");
			printChart = scan.nextBoolean();
			
			System.out.print("Print human-readable rules for turing machine?: ");
			printRules = scan.nextBoolean();
			
			scan.close();
		}
		
		if(!inputPath.endsWith(".bbtm"))
			inputPath += ".bbtm";
		
		if(!path.endsWith(".png"))
			path += ".png";
		
		//running the machine
		
		CreateTMFromPath(inputPath, exampleTM);
		
		//print halt context
		if(printCtx)
		{
			exampleTM.CreateCtxTree();
			
			for(ContextBranch cb : exampleTM.ctxTree)
				System.out.println("Halt Context " + exampleTM.ctxTree.indexOf(cb) + ": " + cb + "\n");
		}
		
		//print all steps
		if(printAll) 
		{
			boolean infinite = false;
			
			if(steps == 0)
				infinite = true;
				
			for(int i = 1; i <= steps || infinite; i++)
			{
				if(!exampleTM.Step())
					break;
				System.out.println(exampleTM + " Step: " + i + "\n");
			}
			
			System.out.println("\nFinal state: \n" + exampleTM);
			
			ContextBranch cb = exampleTM.GetLastCtxBranch();
			System.out.println("Final Halt Context: (Halt Context " + exampleTM.ctxTree.indexOf(cb) + ") " + cb + "\n");
		}
		
		//print tape history
		if(printChart)
		{
			System.out.println("\n\n" + exampleTM.CreateChart(steps));
			
			System.out.println("\nFinal state: \n" + exampleTM);
			
			ContextBranch cb = exampleTM.GetLastCtxBranch();
			System.out.println("Final Halt Context: (Halt Context " + exampleTM.ctxTree.indexOf(cb) + ") " + cb + "\n");
		}
		
		//print rules
		if(printRules)
			System.out.println("\n\n" + exampleTM.RuleCard());
		
		//print PNG image
		if(printImg)
		{
			try
			{
				exampleTM.PrintPNG(path, steps, cHeight, clamp);
				Desktop.getDesktop().edit(new File(path));
			}
			catch(Exception e)
			{
				System.err.print("It didn't work when printing char " + exampleTM.currentPrinting + ": ");
				e.printStackTrace();
			}
		}
	}
	
	public static void CreateTMFromPath(String path, TuringMachine tm)
	{
		//get TM from bbtm file
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		
			String line;
			while ((line = br.readLine()) != null) {
				tm.rules.add(new TuringInstruction(line));
			}
		
			br.close();
		}
		catch(Exception e)
		{
			System.out.println("File reading unsuccessful for " + path);
			System.exit(0);
		}
	}
}
