package vdmj.commands;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Run;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.classdiagram.ClassDiagramFactory;
import net.sourceforge.plantuml.code.ByteArray;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.xmi.XmiClassDiagramStar;



public class Uml2vdmCommand extends Command {
    
    private Hashtable<String, XMIClass> cHash = new Hashtable<String, XMIClass>(); 
	private List<XMIClass> classList = new ArrayList<XMIClass>();  
	String path = "";

	public Uml2vdmCommand(String[] argv)
	{
		if (argv.length == 2)
		{
			path = argv[1];
		}
	}

	public static List<StringLocated> convert(List<String> strings) {
		final List<StringLocated> result = new ArrayList<>();
		LineLocationImpl location = new LineLocationImpl("block", null);
		for (String s : strings) {
			location = location.oneLineRead();
			result.add(new StringLocated(s, location));
		}
		return result;
	}

	@Override
	public DAPMessageList run(DAPRequest request)
	{
		try 
		{
			File inputFile = new File(path);
			List<String> source = new ArrayList<>();
			
			try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
				source = br.lines().collect(Collectors.toList());
			}

			List<StringLocated> sourceLocated = new ArrayList<>();
			LineLocationImpl location = new LineLocationImpl("uml", null);
			for (String s : source) {
				location = location.oneLineRead();
				sourceLocated.add(new StringLocated(s, location));
			}
			UmlSource umlSource = UmlSource.create(sourceLocated, false);
			
			ClassDiagramFactory factory = new ClassDiagramFactory();
			ClassDiagram classDiagram = factory.createEmptyDiagram(ThemeStyle.LIGHT_REGULAR, umlSource, null);

			XmiClassDiagramStar xmiDiagram = new XmiClassDiagramStar(classDiagram);

			OutputStream os = new ByteArrayOutputStream();
			xmiDiagram.transformerXml(os);
			
			System.out.println(os.toString());

			/* DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         	Document doc = dBuilder.parse(inputFile);
         	doc.getDocumentElement().normalize(); */
			
/* 			NodeList cList = doc.getElementsByTagName("UML:Class");
			NodeList gList = doc.getElementsByTagName("UML:Generalization");
			NodeList rList = doc.getElementsByTagName("UML:Association");
	 
			createClasses(cList);
			addInheritance(gList);
			addAssociations(rList); */

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
