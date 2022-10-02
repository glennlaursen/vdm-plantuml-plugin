package plugins.VDM2UML;

import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class UMLType
{
    public Boolean isAsoc = false;
    public Boolean isMap = false;
    public String qualifier = "";
    public String multiplicity = "";
    public String endClass = "";
    public String inClassType = "";
    public int maxDepth = 3;
    public int depth = 0;
    public PublicClassEnvironment env;

    public UMLType(PublicClassEnvironment _env, int _maxDepth) 
    {
        env = _env;
        maxDepth = _maxDepth;
    }

    public UMLType(PublicClassEnvironment _env) 
    {
        env = _env;
    }
}