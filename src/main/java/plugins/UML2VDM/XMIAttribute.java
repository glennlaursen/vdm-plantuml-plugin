package plugins.UML2VDM;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class XMIAttribute {
    
    public enum AttTypes {type, value, var}
    public enum MulTypes {set, seq, set1, seq1, optional, empty}
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
    private Boolean isStatic;
    //private Boolean isAbstract;

    public XMIAttribute(Element aElement)
    {     
        this.isAssociative = false;
        this.isQualified = false;
        this.isStatic = false;
        //this.isAbstract = false;

        if(aElement.getAttribute("isStatic").equals("true"))
        {
            this.isStatic = true;
        }

        /* if(aElement.getAttribute("isAbstract").equals("true"))
        {
            this.isAbstract = true;
        } */
            
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
        this.visibility = "";
        
        NodeList relAttList = rElement.getElementsByTagName("UML:AssociationEnd");
        
        Element relStart  = (Element) relAttList.item(0);
        Element relEnd  = (Element) relAttList.item(1);
        
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

        else if(mult.equals("1...*") || mult.equals("1..*") || mult.equals("1.*"))
            this.mulType = MulTypes.set1; 
        
        else if(mult.equals("*"))
            this.mulType = MulTypes.set;    

        else if(mult.equals("0...1") || mult.equals("0..1") || mult.equals("0.1"))
            this.mulType = MulTypes.optional; 
            
        else this.mulType = MulTypes.empty;
    }

    private void setAttType(Element aElement)
    {
        if (aElement.getAttribute("name").contains("«value»"))
        {
            this.attType = AttTypes.value;
            this.name = aElement.getAttribute("name").replace(" «value»", "");             
        }		

        else if (aElement.getAttribute("name").contains("«type»"))
        {
            this.attType = AttTypes.type;
            this.name = aElement.getAttribute("name").replace(" «type»", "");            
        }
        
        else
        {
            this.attType = AttTypes.var;
            this.name = aElement.getAttribute("name");
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
	
		else if (element.getAttribute("visibility").contains("public"))
            return "public ";
        
        else if (element.getAttribute("visibility").contains("protected"))
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


    public String getStartID()
    {
        return startID;
    }

    public String getEndID()
    {
        return endID;
    }

    public AttTypes getAttType()
    {
        return attType;
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

    public String getAttributeString()
    {
        if(this.attType == AttTypes.value)
        {
            return this.visibility + this.name + " = undef;\n";
        }

        else if (this.attType == AttTypes.type)
        {
            String eq = "=";
           
            if(this.name.contains("::"))
            {
                this.name = remove(this.name, "::");
                eq = "::";
            }    

            if(this.name.contains(":"))
            {
                String segments[] = this.name.split(":");
                
                if(segments[segments.length - 1].equals(segments[0]))
                    return this.visibility + segments[0] + eq + " " + "undef" + ";\n";

                else
                    return this.visibility + segments[0] + eq + segments[segments.length - 1] + ";\n";
            }
            else
                return this.visibility + this.name + " " + eq + " " + "undef" + ";\n";
        }
       
        else if (this.attType == AttTypes.var)
        {
            //String abs = this.isAbstract ? "abstract " : "";
            String stat = this.isStatic ? "static " : "";
            String maptype = "";
            String asoc = "";

            if(this.qualiType == QualiTypes.map)
                    maptype = "map ";
            if(this.qualiType == QualiTypes.inmap)
                    maptype = "inmap ";

            String map = this.isQualified ? " : " + maptype + this.qualifier + " to " :  "";
            
            if(this.isAssociative)
            {
                String _relName = this.mulType == MulTypes.optional ? "[" + this.relName + "]" : this.relName; 
       
                if(!this.isQualified)
                    asoc = " : " + getMulType() + _relName;
                
                else
                    asoc = getMulType() +_relName;
            }

            return stat + this.visibility + this.name + map + asoc + ";\n";
        }
        
        else return "undef";
    }
    
}
    

