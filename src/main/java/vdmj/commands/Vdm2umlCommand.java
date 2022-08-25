package vdmj.commands;

import vdmj.commands.VDM2UML.Buffers;
import vdmj.commands.VDM2UML.UMLGenerator;
import workspace.PluginRegistry;
import workspace.plugins.TCPlugin;

import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;

import dap.DAPMessageList;
import dap.DAPRequest;
import json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Vdm2umlCommand extends Command
{	
	private StringBuilder boiler = new StringBuilder();

	public Vdm2umlCommand(String message) 
	{
		super();
	}

	@Override
	public DAPMessageList run(DAPRequest request)
	{	
		String message = "";

		TCPlugin tcPlugin = PluginRegistry.getInstance().getPlugin("TC");
		TCClassList classes = tcPlugin.getTC();

		Buffers buffers = new Buffers(classes);
		
		for (TCClassDefinition cdef: classes)
		{
			cdef.apply(new UMLGenerator(), buffers);
		}
		
		buildBoiler();
		printPlant("./", buffers);

		return new DAPMessageList(request, new JSONObject("result", message));
	}

	public StringBuilder buildBoiler() 
	{
		boiler.append("@startuml\n\n");
/* 		boiler.append("allow_mixing\n");
		boiler.append("skinparam packageStyle frame\n"); */
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

	public void printPlant(String path, Buffers buffers)
    {   
        try {
			new File("./" + path).mkdirs();
			File plantFile = new File(path + "/" + "Model" + ".wsd");
			
			plantFile.createNewFile();
			
			FileWriter writer = new FileWriter(plantFile.getAbsolutePath());
			
			writer.write(boiler.toString());
			writer.write(buffers.defs.toString());
			writer.write(buffers.asocs.toString());
			writer.write("@enduml");
			writer.close();

            System.out.println("generated PlantUML file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public boolean notWhenRunning()
	{
		return true;
	}
}
