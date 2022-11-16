package plugins.UML2VDM;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.PSystemBuilder;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.xmi.XmiClassDiagramStar;

public class Uml2vdmMain {
    
    private Hashtable<String, XMIClass> cHash = new Hashtable<String, XMIClass>(); 
	private List<XMIClass> classList = new ArrayList<XMIClass>();  

	File pumlFile;
	File outputDir;

	public Uml2vdmMain(File inputfile, File output)
	{
		this.pumlFile = inputfile;
		this.outputDir = output;

	}

	public String run()
	{
		try 
		{
			List<String> source = new ArrayList<>();
			
			try (BufferedReader br = new BufferedReader(new FileReader(pumlFile))) {
				source = br.lines().collect(Collectors.toList());
			}

			List<StringLocated> sourceLocated = convert(source);
			PSystemBuilder pBuilder = new PSystemBuilder();
			Diagram diagram = pBuilder.createPSystem(ThemeStyle.LIGHT_REGULAR, null, sourceLocated, null);
			XmiClassDiagramStar xmiDiagram = new XmiClassDiagramStar((ClassDiagram) diagram);

			OutputStream os = new ByteArrayOutputStream();
			xmiDiagram.transformerXml(os);
			
			InputSource is = new InputSource();
			is.setEncoding("UTF-8");
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
			printer.printVDM(outputDir);
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return "";
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

}