package vdmj.commands;

import java.io.File;
/* import java.io.FileNotFoundException;
import java.io.FileReader; */
import java.io.IOException;

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

/* import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.classdiagram.ClassDiagramFactory; */
import net.sourceforge.plantuml.Run;



public class Uml2vdmCommand extends Command {
    
    private Hashtable<String, XMIClass> cHash = new Hashtable<String, XMIClass>(); 
	private List<XMIClass> classList = new ArrayList<XMIClass>();  
	public static final String USAGE = "Usage: Translate PlantUML to VDM";
	public static final String[] HELP = { "" };
	
	String path;

	public Uml2vdmCommand(String line)
	{
		String[] parts = line.split("\\s+", 2);
		
		if (parts.length == 2)
		{
			this.path = parts[1];
		}
		else
		{
			throw new IllegalArgumentException(USAGE);
		}
	}

	@Override
	public DAPMessageList run(DAPRequest request)
	{
		try { 

			System.out.println("before xmi creation");

			String[] plantArg = {path, "-txmi:star"}; 
			Run.main(plantArg);
		} catch (IOException e) {
			return new DAPMessageList(request, false, "Diagram not found", null);
		} catch(Exception e) {
			return new DAPMessageList(request, false, "error", null);	
		}
		
		try 
		{
			System.out.println("reached after xmi creation");
			

			String newPath = path.replace(".wsd", ".xmi");
			
			File inputFile = new File(newPath);			


			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         	Document doc = dBuilder.parse(inputFile);
         	doc.getDocumentElement().normalize(); 
			
			NodeList cList = doc.getElementsByTagName("UML:Class");
			NodeList gList = doc.getElementsByTagName("UML:Generalization");
			NodeList rList = doc.getElementsByTagName("UML:Association");
	 
			createClasses(cList);
			addInheritance(gList);
			addAssociations(rList);

			VDMPrinter printer = new VDMPrinter(classList);
			
			printer.printVDM(path.replace(inputFile.getName(), ""));
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return new DAPMessageList();
	}
	
	private void createClasses(NodeList cList)
	{
		for (int temp = 0; temp < cList.getLength(); temp++) 
		{
			Node nNode = cList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element cElement = (Element) nNode;
				
				XMIClass c = new XMIClass(cElement);

				classList.add(c);
				
				if (! (cElement.getAttribute("xmi.id") == null || (cElement == null)))
				{
					cHash.put(cElement.getAttribute("xmi.id"), c);
				}
			}
		}		
	}		

 	private void addAssociations(NodeList list)
	{		
		for (int count = 0; count < list.getLength(); count++) 
		{
			Element rElement = (Element) list.item(count);

			XMIAttribute rel = new XMIAttribute(rElement);
			
			rel.setRelName(cHash.get(rel.getEndID()).getName()); 
			
			XMIClass c = cHash.get(rel.getStartID());
			
			c.addAssoc(rel);
		}
	} 

	private void addInheritance(NodeList list)
	{	
		for (int count = 0; count < list.getLength(); count++) 
		{	
			Element iElement = (Element) list.item(count);
			
			String cID = iElement.getAttribute("child");

			XMIClass childClass = cHash.get(cID);
			childClass.setInheritance(true);
			
			String pID = iElement.getAttribute("parent");
			XMIClass parentClass = cHash.get(pID);
			
			childClass.setParent(parentClass.getName());
		}
	}

	@Override
	public boolean notWhenRunning()
	{
		return true;
	}
}
