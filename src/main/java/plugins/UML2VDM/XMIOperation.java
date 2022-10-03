package plugins.UML2VDM;


import org.w3c.dom.Element;

public class XMIOperation {
    
    public enum OpTypes {operation, function}
    
    private String signature;
    private OpTypes opType;
    private String shortName;
    private String visibility;
    private Boolean hasShortName;

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

        String seg2[] = (seg1[seg1.length - 1]).split("\\)");
        
        String argLine = seg2[0];

        String args[] = argLine.split(",");

        String [ ] shortnames = new String [args.length];


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
            args[n] = inSeg1[inSeg1.length - 1];
        }
        
        String vdmArgLine = args[0];
        for(int n = 1 ; n < args.length ; n++)
        {
            vdmArgLine = vdmArgLine + " *" + args[n];            
        }

        String seg3[] =  xmiName.split(":");
        String opOut = seg3[seg3.length - 1];

        if(this.opType == OpTypes.operation)
            this.signature = opName + ":" + vdmArgLine + " ==>" + opOut; 

        else
            this.signature = opName + ":" + vdmArgLine + " ->" + opOut;
        
        this.visibility = visibility(aElement);
        

        String newShortName = opName + "(" + shortnames[0];
        for(int n = 1; n < shortnames.length ; n++)
        {
            newShortName = newShortName + ","  + shortnames[n];
        }
        this.shortName = newShortName + ") == ()" ;
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
        return shortName;
    }

    public OpTypes getOpType()
    {
        return opType;
    }


    
}
    

