package vdmj.commands.UML2VDM;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class XMIAttribute {
    
    public enum AttTypes {type, value, var}
    public enum MulTypes {set, seq, set1, seq1, empty}
    public enum QualiTypes {map, inmap}

    private String name;
    private String relName;
    private String startID;
    private String endID;
    private AttTypes attType;
    private MulTypes mulType;
    private QualiTypes qualiType;
    private Boolean isQualified;

    private String qualifier;

    private String visibility;
    private Boolean isAssociative;


    public XMIAttribute(Element aElement)
    {     
        this.isAssociative = false;
        this.isQualified = false;


        this.name = (aElement.getAttribute("name"));

        setAttType(aElement);

        this.visibility = visibility(aElement);

        if(aElement.getAttribute("xmi.id").contains("ass"))
        {
            this.isAssociative = true;
            initializeAssoc(aElement);
        }
    }

    private void initializeAssoc(Element rElement)
    {
        NodeList relAttList = rElement.getElementsByTagName("UML:AssociationEnd");
        
        Element relStart  = (Element) relAttList.item(1);
        Element relEnd  = (Element) relAttList.item(0);
        
        String indicator = relStart.getAttribute("name");
        String mult = relEnd.getAttribute("name");

        if (setQualified(indicator))
        {
            this.isQualified = true;
            
            setMultType(mult);
            
            this.startID = relStart.getAttribute("type");  
            this.endID = relEnd.getAttribute("type");  
            String str = relStart.getAttribute("name");   

            if(qualiType == QualiTypes.map)
            {
                str = str.replace("[", "");
                str = str.replace("]", "");
            }

            else if (qualiType == QualiTypes.inmap)
            {
                str = str.replace("[(", "");
                str = str.replace(")]", "");
            }
            this.qualifier = str;
        }

        else
        {
            this.endID = relEnd.getAttribute("type");   
            this.startID = relStart.getAttribute("type");
            setMultType(mult);
        }      
    }

    private Boolean setQualified(String indicator)
    {
        if(indicator.contains("[(") && indicator.contains(")]"))
        {
            this.qualiType = QualiTypes.inmap;
            return true;
        }    

        else if(indicator.contains("[") && indicator.contains("]"))
        {
            this.qualiType = QualiTypes.map;
            return true;
        }    

        else return false;
    }

    private void setMultType(String mult)
    {
        if(mult.equals("(1...*)") || mult.equals("(1..*)") || mult.equals("(1.*)"))
            this.mulType = MulTypes.seq1;
        
        else if(mult.equals("(*)"))
            this.mulType = MulTypes.seq;
        
        else if(mult.equals("*"))
            this.mulType = MulTypes.set;    

        else this.mulType = MulTypes.empty;
    }

    private void setAttType(Element aElement)
    {
        if (aElement.getAttribute("name").contains("«value»"))
        {
            this.attType = AttTypes.value;
            this.name = remove(this.name, "«value»");      
        }		

        if (aElement.getAttribute("name").contains("«type»"))
        {
            this.attType = AttTypes.type;
            this.name = remove(this.name, " «type»");      
        }
        
        if (! (aElement.getAttribute("name").contains("«type»") || 
                aElement.getAttribute("name").contains("«value»")))
        {
            this.attType = AttTypes.var;
        }
    }

   
    private String remove(String s, String r)
	{
        return s.replace(r, "");
	}
    
    private String visibility(Element element)
	{
		if (element.getAttribute("visibility").contains("private")) 
			return "private ";
	
		if (element.getAttribute("visibility").contains("public"))
            return "public ";
        
        if (element.getAttribute("visibility").contains("protected"))
            return "protected "; 

        else return "private ";
	}

    public void setRelName(String parent)
    {
        if (parent.equals(this.name))
        this.relName = "undef";

        else
            this.relName = parent;
    }

    public void setVisibility(String newVis)
    {
        this.visibility = newVis;
    }

    public String getStartID()
    {
        return startID;
    }

    public String getEndID()
    {
        return endID;
    }

    public String getVisibility()
    {
        return visibility;
    }

    public Boolean getIsAssociative()
    {
        return isAssociative;
    }

    public Boolean getIsQualified()
    {
        return isQualified;
    }

    public String getName()
    {
        return name;
    }

    public String getRelName()
    {
        return relName;
    }  

    public AttTypes getAttType()
    {
        return attType;
    }

    public QualiTypes getQualiType()
    {
        return qualiType;
    }

    public String getMulType()
    {
        if (this.mulType == MulTypes.set)
            return "set of ";
        
        if (this.mulType == MulTypes.seq)
            return "seq of ";

        if (this.mulType == MulTypes.seq1)
            return "seq1 of ";

        if (this.mulType == MulTypes.set1)
            return "set1 of ";

        if (this.mulType == MulTypes.empty)
            return "";
        
        else
            return "";
    }
    
    public String getQualifier()
    {
        return qualifier;
    }
    
}
    

