package vdmj.commands;

import java.io.File;

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


public class Uml2vdmCommand extends Command {
    
    private Hashtable<String, XMIClass> cHash = new Hashtable<String, XMIClass>(); 
	private List<XMIClass> classList =new ArrayList<XMIClass>();  
	String outputPath = "";

	public Uml2vdmCommand(String[] argv)
	{
		if (argv.length == 2)
		{
			outputPath = argv[1];
		}
	}

	@Override
	public DAPMessageList run(DAPRequest request)
	{
		try 
		{
			File inputFile = new File(outputPath);
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
			
			printer.printVDM(outputPath.replace(inputFile.getName(), ""));
		
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
