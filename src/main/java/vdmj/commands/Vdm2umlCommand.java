/*******************************************************************************
 *
 *	Copyright (c) 2022 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *	SPDX-License-Identifier: GPL-3.0-or-later
 *
 ******************************************************************************/

package vdmj.commands;

import vdmj.commands.VDM2UML.Buffers;
import vdmj.commands.VDM2UML.UMLGenerator;
import workspace.PluginRegistry;
import workspace.plugins.TCPlugin;

import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;

import dap.DAPMessageList;
import dap.DAPRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/* import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.classdiagram.ClassDiagramFactory;
import net.sourceforge.plantuml.Run; */


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
		TCPlugin tcPlugin = PluginRegistry.getInstance().getPlugin("TC");
		TCClassList classes = tcPlugin.getTC();

		Buffers buffers = new Buffers(classes);
		
		for (TCClassDefinition cdef: classes)
		{
			cdef.apply(new UMLGenerator(), buffers);
		}
		
		buildBoiler();
		printPlant("./", buffers);

		return new DAPMessageList();
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
