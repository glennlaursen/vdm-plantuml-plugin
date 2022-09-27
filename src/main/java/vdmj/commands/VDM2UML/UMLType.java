package vdmj.commands.VDM2UML;

import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class UMLType
{
    public Boolean isAsoc = false;
    public Boolean isMap = false;
    public Boolean isType = false;
    public Boolean isOpOrFunc = false;
    public String qualifier = "";
    public String multiplicity = "";
    public String endClass = "";
    public String inClassType = "";
    public String paramsType = "";
    public String returnType = "";
    public int maxDepth = 3;
    public int depth = 0;
    public PublicClassEnvironment env;

    public UMLType(PublicClassEnvironment _env, int _maxDepth, Boolean _isType) 
    {
        env = _env;
        maxDepth = _maxDepth;
        isType = _isType;
    }

    public UMLType(PublicClassEnvironment _env, Boolean _isType) 
    {
        env = _env;
        isType = _isType;
    }

    public UMLType(PublicClassEnvironment _env, int _maxDepth, Boolean _isType, Boolean _isOpOrFunc) 
    {
        env = _env;
        maxDepth = _maxDepth;
        isType = _isType;
        isOpOrFunc = _isOpOrFunc;
    }

    public UMLType(PublicClassEnvironment _env, Boolean _isType, Boolean _isOpOrFunc) 
    {
        env = _env;
        isType = _isType;
        isOpOrFunc = _isOpOrFunc;
    }

    public UMLType(PublicClassEnvironment _env) 
    {
        env = _env;
    }
}