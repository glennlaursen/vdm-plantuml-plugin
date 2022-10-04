package plugins.UML2VDM;


import org.w3c.dom.Element;

public class XMIOperation {
    
    public enum OpTypes {operation, function}
    
    private String signature;
    private OpTypes opType;
    private String shortName;
    private String visibility;
    private Boolean hasShortName;
    private String[] args;

    public XMIOperation(Element aElement)
    {     
        String xmiName = (aElement.getAttribute("name"));
        hasShortName = false;
        
        if (aElement.getAttribute("name").contains("«function»"))
        {
            this.opType = OpTypes.function;
            xmiName = remove(xmiName, "«function»");      
        }	
        else
            this.opType = OpTypes.operation;

        String seg1[] = xmiName.split("\\(");
        String opName = seg1[0];
        String parameters[] = (seg1[seg1.length - 1]).split("\\)");
        String readyArgLine;

        if(!(parameters.length == 0))
        {
            String[] abreviated = shortNames(parameters);   
            readyArgLine = vdmArgline();

            String newShortName = opName + "(" + abreviated[0];
            for(int n = 1; n < abreviated.length ; n++)
            {
                newShortName = newShortName + ","  + abreviated[n];
            }
            this.shortName = newShortName + ") == ()" ;
        } 

        else 
        {
            this.shortName = opName + "() == ()";
            readyArgLine = "";
        }


        String opOut;
        if (xmiName.contains(":"))
        {
            String seg3[] =  xmiName.split(":");
            opOut = seg3[seg3.length - 1];
        }
        else 
            opOut = "";


        if(this.opType == OpTypes.operation)
            this.signature = opName + " : " + readyArgLine + " ==>" + opOut; 

        else
            this.signature = opName + " : " + readyArgLine + " ->" + opOut;
        
        this.visibility = visibility(aElement);
    
    }

    private String vdmArgline()
    {
        if (!(args == null))
        {
            String vdmArgLine = args[0];
            
            for(int n = 1 ; n < args.length ; n++)
            {
                vdmArgLine = vdmArgLine + " *" + args[n];            
            }
            return vdmArgLine;
        }
        else return "";
    }

    private String[] shortNames(String[] s)
    {
        if ((!(s.length == 0)))
        {
           
            String argLine = s[0];
            args = argLine.split(",");

            String [] shortnames = new String[args.length];

            for(int n = 0; n < args.length; n++)     
            {
                if(args[n].contains("in ") || args[n].contains(" in "))
                {
                    args[n] = args[n].replace("in ", "");
                    hasShortName = true;
                }
                String inSeg1[] = args[n].split(":");
                
                if(hasShortName == true)
                    shortnames[n] = inSeg1[0];
                else
                    shortnames[n] = "";
                
                hasShortName = false;
                
                if (!(inSeg1.length == 0))
                    args[n] = inSeg1[inSeg1.length - 1];
                
            }
           
            return shortnames;
        }

        else return new String[0];

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

    public String getVisibility()
    {
        return visibility;
    }

    public String getSignature()
    {
        return signature;
    }

    public String getShortName()
    {
        return this.shortName;
    }

    public OpTypes getOpType()
    {
        return opType;
    }


    
}
    

