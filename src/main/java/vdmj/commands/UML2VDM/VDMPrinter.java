package vdmj.commands.UML2VDM;

import java.util.*;

import java.io.File; 
import java.io.IOException;
import java.io.FileWriter;  

public class VDMPrinter {
    
    private List<XMIClass> classList;

    public VDMPrinter(List<XMIClass> classList)
    {
        this.classList = classList;
    }

    public void printVDM(String path)
    {   
        try {
            new File("generated").mkdirs();

            for (int n = 0; n < classList.size(); n++) 
            {	
                XMIClass c = classList.get(n);
                
                File vdmFile = new File("generated/" + c.getName() + ".vdmpp");
                
               vdmFile.createNewFile();
                    
                FileWriter writer = new FileWriter(vdmFile.getAbsolutePath());
                
                printClass(writer, c);
                printAttributes(writer, c);
              /*   printValues(writer, c);
                printTypes(writer, c);
                printIVariables(writer, c); */
                printOperations(writer, c);
                printFunctions(writer, c);

                writer.write("\n\nend " + c.getName());
                writer.close();
            }

            System.out.println("generated vdm files");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printClass(FileWriter writer, XMIClass c)
    {
        try {
            writer.write(c.getClassString());
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }     

    private void printAttributes(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getValues().isEmpty())
            {
                List<XMIAttribute> valueList = c.getValues();
                writer.write("values\n");
                for (int count = 0; count < valueList.size(); count++) 
                {
                    XMIAttribute val = valueList.get(count);
                    writer.write(val.getAttributeString());
                }
                writer.write("\n");
            }
            
            if (!c.getTypes().isEmpty())
            {
                List<XMIAttribute> typeList = c.getTypes();
                writer.write("types\n");

                for (int count = 0; count < typeList.size(); count++) 
                {
                    XMIAttribute type = typeList.get(count);
                    writer.write(type.getAttributeString());
                }
                writer.write("\n");
            }

            if (!c.getIVariables().isEmpty())
            {
                List<XMIAttribute> varList = c.getIVariables();

                writer.write("instance variables\n");

                for (int count = 0; count < varList.size(); count++) 
                {
                    XMIAttribute var = varList.get(count);
                    writer.write(var.getAttributeString());
                }
                writer.write("\n");
            }
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /* private void printValues(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getValues().isEmpty())
            {
                List<XMIAttribute> valueList = c.getValues();

                writer.write("values\n");

                for (int count = 0; count < valueList.size(); count++) 
                {
                    XMIAttribute val = valueList.get(count);
                    writer.write(val.getVisibility() + val.getName() + " = undef;\n");
                }

                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }  */

  /*   private void printTypes(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getTypes().isEmpty())
            {
                List<XMIAttribute> typeList = c.getTypes();

                writer.write("types\n");

                for (int count = 0; count < typeList.size(); count++) 
                {
                    XMIAttribute type = typeList.get(count);

                    if(type.getName().contains(":"))
                    {
                        String segments[] = type.getName().split(":");
                        
                        if(segments[segments.length - 1].equals(segments[0]))

                            writer.write(type.getVisibility() + segments[0] + "= " + "undef" + ";\n");

                        else
                            writer.write(type.getVisibility() + segments[0] + "=" + segments[segments.length - 1] + ";\n");
                    }
                    
                    else
                        writer.write(type.getVisibility() + type.getName() + " = " + "undef" + ";\n");

                    
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } */

    /* private void printIVariables(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getIVariables().isEmpty())
            {
                List<XMIAttribute> varList = c.getIVariables();

                writer.write("instance variables\n");

                for (int count = 0; count < varList.size(); count++) 
                {
                    XMIAttribute var = varList.get(count);
 
                    if (var.getIsQualified())
                    {
                        String maptype = "";
                        if(var.getQualiType() == QualiTypes.map)
                            maptype = "map ";
                        else if(var.getQualiType() == QualiTypes.inmap)
                            maptype = "inmap ";
                  
                        writer.write(var.getVisibility() + var.getName() +  " : " + maptype + var.getQualifier() + " to " + var.getMulType() + var.getRelName() + ";\n");
                    }

                    else if (var.getIsAssociative())
                    {
                        writer.write(var.getVisibility() + var.getName() +  " : " + var.getMulType() + var.getRelName() + ";\n");
                    }
                    
                    else
                        writer.write(var.getVisibility() + var.getName() + ";\n");
                        
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }  */

    private void printOperations(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getOperations().isEmpty())
            {
                List<XMIOperation> opList = c.getOperations();

                writer.write("operations\n");

                for (int count = 0; count < opList.size(); count++) 
                {
                    XMIOperation op = opList.get(count);

                    writer.write(op.getVisibility() + op.getSignature() + "\n" + op.getShortName() + "\n\n");
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } 

    private void printFunctions(FileWriter writer, XMIClass c)
    {
        try {
            if (!c.getFunctions().isEmpty())
            {
                List<XMIOperation> funcList = c.getFunctions();

                writer.write("functions\n");

                for (int count = 0; count < funcList.size(); count++) 
                {
                    XMIOperation fun = funcList.get(count);

                    writer.write(fun.getVisibility() + fun.getSignature() + "\n" + fun.getShortName() + "\n\n");
                }
                writer.write("\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }       
    } 



}

