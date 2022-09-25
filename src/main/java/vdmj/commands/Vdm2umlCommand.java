package vdmj.commands;

import vdmj.commands.VDM2UML.PlantBuilder;
import vdmj.commands.VDM2UML.UMLGenerator;
import workspace.PluginRegistry;
import workspace.plugins.TCPlugin;

import com.fujitsu.vdmj.Settings;
import com.fujitsu.vdmj.lex.Dialect;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;

import dap.DAPMessageList;
import dap.DAPRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Vdm2umlCommand extends Command
{	
	public static final String USAGE = "Usage: vdm2uml <folder>";
	public static final String[] HELP =	{ "vdm2uml", "vdm2uml <folder> - transform VDM++ or VDM-RT model to PlantUML" };

	private String outputPath = "";
	private StringBuilder boiler = new StringBuilder();

	public Vdm2umlCommand(String line) 
	{
		super();

		String[] parts = line.split("\\s+");

		if (line.equals("vdm2uml"))
		{
			outputPath = "output";
		}
		else if (parts.length == 2)
		{
			outputPath = parts[1];
		} 
		else 
		{
			throw new IllegalArgumentException(USAGE);
		}
	}

	String message = "";

	@Override
	public DAPMessageList run(DAPRequest request)
	{	
		if (Settings.dialect != Dialect.VDM_PP && Settings.dialect != Dialect.VDM_RT)
		{
			return new DAPMessageList(request,
				false, "Command only available for VDM-PP and VDM-RT", null);	
		}

		TCPlugin tcPlugin = PluginRegistry.getInstance().getPlugin("TC");
		TCClassList classes = tcPlugin.getTC();
		PlantBuilder buffer;
		
		if (!classes.isEmpty())
		{
			buffer = new PlantBuilder(classes);
			for (TCClassDefinition cdef: classes)
			{
				cdef.apply(new UMLGenerator(), buffer);
			}
		}
		else
		{
			return new DAPMessageList(request,
				false, "No classes in VDM project", null);	
		}
		
		buildBoiler();
		if (!printPlantUML(buffer))
		{
			return new DAPMessageList(request,
				false, "Failed writing to output path", null);	
		}

		return new DAPMessageList(request, true, "UML generation completed", null);
	}

	public StringBuilder buildBoiler() 
	{
		boiler.append("@startuml\n\n");
		boiler.append("hide empty members\n");
		boiler.append("skinparam Shadowing false\n");
		boiler.append("skinparam classAttributeIconSize 0\n");
		boiler.append("skinparam ClassBorderThickness 0.5\n");
		boiler.append("skinparam class {\n");
		boiler.append("\tBackgroundColor AntiqueWhite\n");
		boiler.append("\tArrowColor Black\n");
		boiler.append("\tBorderColor Black\n}\n");
		boiler.append("skinparam defaultTextAlignment center\n\n");

		return boiler;
	}

	public boolean printPlantUML(PlantBuilder buffer)
    {   
        try 
		{
			new File("./" + outputPath).mkdirs();
			File plantFile = new File("./" + outputPath + "/" + "Model" + ".wsd");
			
			plantFile.createNewFile();
			
			FileWriter writer = new FileWriter(plantFile.getAbsolutePath());
			
			writer.write(boiler.toString());
			writer.write(buffer.defs.toString());
			writer.write(buffer.asocs.toString());
			writer.write("@enduml");
			writer.close();

			return true;
        } 
		catch (IOException e) 
		{
            return false;
        }
    }

	@Override
	public boolean notWhenRunning()
	{
		return true;
	}
}
