package vdmj.commands;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import dap.DAPMessageList;
import dap.DAPRequest;
import vdmj.commands.UML2VDM.VDMPrinter;
import vdmj.commands.UML2VDM.XMIAttribute;
import vdmj.commands.UML2VDM.XMIClass;

import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.PSystemBuilder;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.xmi.XmiClassDiagramStar;

public class Uml2vdmCommand extends Command {
    
    private Hashtable<String, XMIClass> cHash = new Hashtable<String, XMIClass>(); 
	private List<XMIClass> classList = new ArrayList<XMIClass>();  
	public static final String USAGE = "Usage: uml2vdm <file>";
	public static final String[] HELP = { "uml2vdm", "uml2vdm <file> - translate PlantUML model to VDM++" };
	
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

	public static List<StringLocated> convert(List<String> strings) {
		final List<StringLocated> result = new ArrayList<>();
		LineLocationImpl location = new LineLocationImpl("uml", null);
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

			PSystemBuilder pBuilder = new PSystemBuilder();
			Diagram diagram = pBuilder.createPSystem(ThemeStyle.LIGHT_REGULAR, null, sourceLocated, null);

			XmiClassDiagramStar xmiDiagram = new XmiClassDiagramStar((ClassDiagram) diagram);

			OutputStream os = new ByteArrayOutputStream();
			xmiDiagram.transformerXml(os);
			
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(os.toString()));

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         	Document doc = dBuilder.parse(is);
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