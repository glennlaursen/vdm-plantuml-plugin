package plugins.VDM2UML;

import java.util.ArrayList;
import java.awt.Point;

import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

public class UMLType
{
    enum Type
	{
		NONE, SET, SET1, SEQ, SEQ1, MAP, INMAP, PRODUCT, UNION, OPTIONAL, RECORD
	}

    public Boolean isAsoc = false;
    public Boolean isMap = false;
    public Type prevType = Type.NONE;
    public Boolean isType = false;
    public Boolean useTempType = false;
    public Boolean useMapType = false;
    public Boolean tempBeforeMap = false;
    public String qualifier = "";
    public String multiplicity = "";
    public String endClass = "";
    public String inClassType = "";
    public String tempType = "";
    public String namedType = "";
    public String mapType = "";
    public String paramsType = "";
    public String returnType = "";
    public String currentClass = "";
    public int typeCost = 0;
    public ArrayList<Point> capacities;
    public int depth = 0;
    public PublicClassEnvironment env;

    public UMLType(PublicClassEnvironment _env) 
    {
        capacities = new ArrayList<Point>(0);
        env = _env;
    }

    public UMLType(PublicClassEnvironment _env, Boolean _isType) 
    {
        capacities = new ArrayList<Point>(0);
        env = _env;
        isType = _isType;
    }

    public void addCapacity(int cap)
    {
        capacities.add(new Point(cap, 0));
    }
}