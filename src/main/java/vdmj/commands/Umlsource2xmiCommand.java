package vdmj.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import dap.DAPMessageList;
import dap.DAPRequest;
import vdmj.commands.UML2VDM.VDMPrinter;
import vdmj.commands.UML2VDM.XMIAttribute;
import vdmj.commands.UML2VDM.XMIClass;

import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.classdiagram.ClassDiagramFactory;
import net.sourceforge.plantuml.Run;


public class Umlsource2xmiCommand extends Command {
    
	String path = "";

	public Umlsource2xmiCommand(String[] argv)
	{
		if (argv.length == 2)
		{
			path = argv[1];
		}
	}

	@Override
	public DAPMessageList run(DAPRequest request)
	{
		try 
		{
			File inputFile = new File(path);

/* 			VDMPrinter printer = new VDMPrinter(classList);
			
			printer.printVDM(path.replace(inputFile.getName(), "")); */
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return new DAPMessageList();
	}

	@Override
	public boolean notWhenRunning()
	{
		return true;
	}
}
