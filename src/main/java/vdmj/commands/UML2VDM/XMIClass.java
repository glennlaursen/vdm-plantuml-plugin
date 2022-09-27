package vdmj.commands.UML2VDM;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import vdmj.commands.UML2VDM.XMIAttribute.AttTypes;
import vdmj.commands.UML2VDM.XMIOperation.OpTypes;

import java.util.*;


public class XMIClass {

    private String name;
    private String parent;
    private Boolean isInherited;
    private Boolean isAbstract;
    
    private List<XMIAttribute> typeList = new ArrayList<XMIAttribute>();   
    private List<XMIAttribute> valueList = new ArrayList<XMIAttribute>();   
    private List<XMIAttribute> varList = new ArrayList<XMIAttribute>(); 
    private List<XMIOperation> operationList = new ArrayList<XMIOperation>();   
    private List<XMIOperation> functionList = new ArrayList<XMIOperation>();   
    

    public XMIClass(Element cElement){
        this.name = cElement.getAttribute("name");
        //this.ID = cElement.getAttribute("xmi.id");
        this.isInherited = false;
        this.isAbstract = false;

        if(cElement.getAttribute("isAbstract").equals("true"))
            this.isAbstract = true;

        NodeList attributeList = cElement.getElementsByTagName("UML:Attribute");
        if(! (attributeList.getLength() == 0))
            createAttributes(attributeList);
    
        NodeList operationList = cElement.getElementsByTagName("UML:Operation");
        if(! (operationList.getLength() == 0))
            createOperations(operationList);
    }

    private void createOperations(NodeList list)
    {
        for (int count = 0; count < list.getLength(); count++) {
					
			Element oElement  = (Element) list.item(count);
			
            XMIOperation op = new XMIOperation(oElement);

			if (op.getOpType() == OpTypes.operation)
                operationList.add(op);

            if (op.getOpType() == OpTypes.function)
                functionList.add(op);
		}
    }
    
    private void createAttributes(NodeList list)
    {
        for (int count = 0; count < list.getLength(); count++) {
                        
            Element aElement  = (Element) list.item(count);

            XMIAttribute att = new XMIAttribute(aElement);

            if (att.getAttType() == AttTypes.value)		                
                valueList.add(att);

            if (att.getAttType() == AttTypes.type)		                
                typeList.add(att);

            if (att.getAttType() == AttTypes.var)		                
                varList.add(att);          
        }
    }

    public void addAssoc(XMIAttribute att)
    {
        if (att.getAttType() == AttTypes.type)
            typeList.add(att);

        else
            varList.add(att);
    }

    public void setParent(String parentName)
    {
        this.parent = parentName;
    }

    public void setInheritance(Boolean bool)
    {
        this.isInherited = bool;
    }
    
    public String getIsAbstract()
    {
        if (this.isAbstract == true)
            return "Abstract";

        else return "";
    }

    public String getName()
    {
        return name;
    }

    public List<XMIAttribute> getIVariables()
    {
        return varList;
    }

    
    public List<XMIAttribute> getTypes()
    {
        return typeList;
    }
    
    public List<XMIAttribute> getValues()
    {
        return valueList;
    }

    
    public List<XMIOperation> getOperations()
    {
        return operationList;
    }

    
    public List<XMIOperation> getFunctions()
    {
        return functionList;
    }

    public String getClassString()
    {
        String abs = this.isAbstract ? "abstract " : "";

        String inh = this.isInherited ? " is subclas of " + this.parent : "";

        return abs + "class " + this.name + inh + "\n\n";   
    }

}

