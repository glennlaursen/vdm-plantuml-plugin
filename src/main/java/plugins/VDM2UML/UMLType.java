package plugins.VDM2UML;

import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class UMLType
{
    enum Type
	{
		NONE, SET, SET1, SEQ, SEQ1, MAP, INMAP, PRODUCT, UNION, OPTIONAL, RECORD
	}

    public Boolean isAsoc = false;
    public Boolean isMap = false;
    public Type abstractedType = Type.NONE;
    public Boolean isType = false;
    public String qualifier = "";
    public String multiplicity = "";
    public String endClass = "";
    public String inClassType = "";
    public String paramsType = "";
    public String returnType = "";
    public int maxDepth = 3;
    public int typeCost = 0;
    public int capacity = -1;
    public int depth = 0;
    public PublicClassEnvironment env;

    public UMLType(PublicClassEnvironment _env) 
    {
        env = _env;
    }

    public UMLType(PublicClassEnvironment _env, int _maxDepth) 
    {
        env = _env;
        maxDepth = _maxDepth;
    }

    public UMLType(PublicClassEnvironment _env, Boolean _isType) 
    {
        env = _env;
        isType = _isType;
    }

    public UMLType(PublicClassEnvironment _env, int _maxDepth, Boolean _isType) 
    {
        env = _env;
        maxDepth = _maxDepth;
        isType = _isType;
    }
}