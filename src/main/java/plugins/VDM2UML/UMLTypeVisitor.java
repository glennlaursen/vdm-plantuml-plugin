package plugins.VDM2UML;

import java.util.List;
import java.util.Vector;

import com.fujitsu.vdmj.tc.types.TCFunctionType;
import com.fujitsu.vdmj.tc.types.TCInMapType;
import com.fujitsu.vdmj.tc.types.TCMapType;
import com.fujitsu.vdmj.tc.types.TCNamedType;
import com.fujitsu.vdmj.tc.types.TCOperationType;
import com.fujitsu.vdmj.tc.types.TCOptionalType;
import com.fujitsu.vdmj.tc.types.TCProductType;
import com.fujitsu.vdmj.tc.types.TCRecordType;
import com.fujitsu.vdmj.tc.types.TCSeq1Type;
import com.fujitsu.vdmj.tc.types.TCSeqType;
import com.fujitsu.vdmj.tc.types.TCSet1Type;
import com.fujitsu.vdmj.tc.types.TCSetType;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.tc.types.TCUnionType;
import com.fujitsu.vdmj.tc.types.visitors.TCLeafTypeVisitor;

import plugins.VDM2UML.UMLType.Type;

public class UMLTypeVisitor extends TCLeafTypeVisitor<Object, List<Object>, UMLType>
{
	static int MAX_LOW_CAPACITY = 3;
	static int MAX_HIGH_CAPACITY = 4;
	static int MAX_NUM_OF_COMPOSITE_TYPES = 5;

	// Check if a type is one of the basic types of VDM
	private Boolean isBasicType(TCType node)
	{
		if (node.toString() == "bool")
		{
			return true;
		}
		else if (node.toString() == "real")
		{
			return true;
		}
		else if (node.toString() == "rat")
		{
			return true;
		}
		else if (node.toString() == "int")
		{
			return true;
		}
		else if (node.toString() == "nat")
		{
			return true;
		}
		else if (node.toString() == "nat1")
		{
			return true;
		}
		else if (node.toString() == "char")
		{
			return true;
		}
		else if (node.toString() == "token")
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private String getAbstractType(UMLType arg)
	{
		String res = "";
		switch(arg.abstractedType)
		{
			case INMAP:
				res = "inmap...";
				break;
			case MAP:
				res = "map...";
				break;
			case NONE:
				res = "...";
				break;
			case OPTIONAL:
				res = "[...]";
				break;
			case PRODUCT:
				res = "*...";
				break;
			case UNION:
				res = "|...";
				break;
			case RECORD:
				res = "::...";
				break;
			case SEQ:
				res = "seq...";
				break;
			case SEQ1:
				res = "seq1...";
				break;
			case SET:
				res = "set...";
				break;
			case SET1:
				res = "set1...";
				break;
			default:
				res = "";
				break;
		}
		return res;
	}

	@Override
	public List<Object> caseType(TCType node, UMLType arg)
	{
		arg.depth++;
		if (!isBasicType(node))
		{
			arg.typeCost += 1;
		}
		if ((node.isClass(arg.env) && arg.isMap) || (node.isClass(arg.env)))
		{
			arg.endClass = node.toString();
			arg.isAsoc = true;
		}
		if (!arg.isMap)
		{
			if (arg.typeCost > arg.capacity)
			{
				arg.inClassType = getAbstractType(arg);
			}
			else
			{
				arg.inClassType += node.toString();
			}
		}
		
		return null;
	}

	@Override
	public List<Object> caseNamedType(TCNamedType node, UMLType arg)
	{
		if (arg.isType) 
		{
			node.type.apply(new UMLTypeVisitor(), arg);
		}
		else
		{
			arg.typeCost += 1;
			if (arg.depth > 0)
			{
				if (arg.typeCost > arg.capacity)
				{
					arg.inClassType = getAbstractType(arg);
				}
				else
				{
					arg.inClassType += node.toString();
				}
			} 
			else
			{
				arg.inClassType += node.toString();
			}
		}

		return null;
	}

	@Override
	public List<Object> caseSet1Type(TCSet1Type node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_LOW_CAPACITY - 1;
			arg.abstractedType = Type.SET1;
		}
		else
		{
			arg.typeCost += 1;
		}
		setSeqConstructor("1..*", "set1 of ", arg);

		if (arg.depth < arg.maxDepth)
		{
			node.setof.apply(new UMLTypeVisitor(), arg);
		}
		
		return null;
	}

    @Override
	public List<Object> caseSetType(TCSetType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_LOW_CAPACITY - 1;
			arg.abstractedType = Type.SET;
		}
		else
		{
			arg.typeCost += 1;
		}
		setSeqConstructor("*", "set of ", arg);

		if (arg.depth < arg.maxDepth)
			node.setof.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSeq1Type(TCSeq1Type node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_LOW_CAPACITY - 1;
			arg.abstractedType = Type.SEQ1;
		}
		else
		{
			arg.typeCost += 1;
		}
		setSeqConstructor("(1..*)", "seq1 of ", arg);

		if (arg.depth < arg.maxDepth)
			node.seqof.apply(new UMLTypeVisitor(), arg);
		
		return null;
	}

    @Override
	public List<Object> caseSeqType(TCSeqType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_LOW_CAPACITY - 1;
			arg.abstractedType = Type.SEQ;
		}
		else
		{
			arg.typeCost += 1;
		}
        setSeqConstructor("(*)", "seq of ", arg);

		if (arg.depth < arg.maxDepth)
			node.seqof.apply(new UMLTypeVisitor(), arg);
		
		return null;
	}

	private void setSeqConstructor(String _multiplicity, String _type, UMLType arg)
	{
		if (arg.depth == 1)
		{
			arg.multiplicity += _multiplicity;
		}
			
		if (!arg.isMap && !arg.inClassType.contains("map"))
		{
			arg.inClassType += _type;
		}
	}

	@Override
	public List<Object> caseInMapType(TCInMapType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_LOW_CAPACITY;
			arg.abstractedType = Type.INMAP;
			arg.isMap = true;
			if (!node.from.isClass(arg.env))
			{
				// TODO: Use visitor for qualifier
				arg.qualifier += "(";	
				arg.qualifier += node.from.toString();
				arg.qualifier += ")";
			}
		}
		else
		{
			arg.typeCost += 1;
		}
		arg.inClassType += "inmap ";
		node.from.apply(new UMLTypeVisitor(), arg);
		arg.inClassType += " to ";
		node.to.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseMapType(TCMapType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_LOW_CAPACITY;
			arg.abstractedType = Type.MAP;
			arg.isMap = true;
			
			if (!node.from.isClass(arg.env))
			{
				// TODO: Use visitor for qualifier
				arg.qualifier += node.from.toString();
			}
		}
		else
		{
			arg.typeCost += 1;
		}
		arg.inClassType += "map ";
		node.from.apply(new UMLTypeVisitor(), arg);
		arg.inClassType += " to ";
		node.to.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseProductType(TCProductType node, UMLType arg)
	{
        /** Set type to * */
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_HIGH_CAPACITY;
			arg.abstractedType = Type.PRODUCT;
		}
		else
		{
			arg.typeCost += 1;
		}

		if (node.types.size() <= MAX_NUM_OF_COMPOSITE_TYPES)
		{
			int i = 1;
			for (TCType type : node.types)
			{
				type.apply(new UMLTypeVisitor(), arg);
				if (i < node.types.size())
				{
					arg.inClassType += " * ";
				}
				i++;
			}
		}
		else
		{
			arg.inClassType += "*... ";
		}
		return null;
	}

    @Override
	public List<Object> caseUnionType(TCUnionType node, UMLType arg)
	{
        /** Set type to | */
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_HIGH_CAPACITY;
			arg.abstractedType = Type.UNION;
		}
		else
		{
			arg.typeCost += 1;
		}
		if (node.types.size() <= MAX_NUM_OF_COMPOSITE_TYPES)
		{	
			int i = 1;
			for (TCType type : node.types)
			{
				type.apply(new UMLTypeVisitor(), arg);
				if (i < node.types.size())
				{
					arg.inClassType += " | ";
				}
				i++;
			}
		}
		else
		{
			arg.inClassType += "|... ";
		}

		return null;
	}

    @Override
	public List<Object> caseOptionalType(TCOptionalType node, UMLType arg)
	{
        /** Set type to [] */
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_HIGH_CAPACITY;
			arg.abstractedType = Type.OPTIONAL;
		}
		else
		{
			arg.typeCost += 1;
		}
		arg.inClassType += "[";
		node.type.apply(new UMLTypeVisitor(), arg);
		arg.inClassType += "]";

		return null;
	}

	@Override
	public List<Object> caseRecordType(TCRecordType node, UMLType arg)
	{
        /** Set type to :: */
		arg.depth++;
		if (arg.depth == 1)
		{
			arg.capacity = MAX_HIGH_CAPACITY;
			arg.abstractedType = Type.RECORD;
		}
		else
		{
			arg.typeCost += 1;
		}
		arg.inClassType += ":: ";
		
		return null;
	}

	@Override
	public List<Object> caseOperationType(TCOperationType node, UMLType arg)
	{
		int i = 0;
		for (TCType param : node.parameters)
		{
			UMLType paramUMLType = new UMLType(arg.env, false);
			param.apply(new UMLTypeVisitor(), paramUMLType);
			arg.paramsType += paramUMLType.inClassType;
			
			// Remove whitespace at end of parameter
			if (arg.paramsType.length() > 1)
			{
				if (Character.isWhitespace(arg.paramsType.charAt(arg.paramsType.length() - 1)))
				{
					arg.paramsType = arg.paramsType.substring(0, arg.paramsType.length() - 1);
				}
			}
			if (i < node.parameters.size() - 1)
				arg.paramsType += ", ";
			i += 1;
		}
		UMLType returnUMLType = new UMLType(arg.env, false);
		node.result.apply(new UMLTypeVisitor(), returnUMLType);
		arg.returnType = returnUMLType.inClassType;
		
		return null;
	}

	@Override
	public List<Object> caseFunctionType(TCFunctionType node, UMLType arg)
	{
		int i = 0;
		for (TCType param : node.parameters)
		{
			UMLType paramUMLType = new UMLType(arg.env, false);
			param.apply(new UMLTypeVisitor(), paramUMLType);
			arg.paramsType += paramUMLType.inClassType;

			// Remove whitespace at end of parameter
			if (arg.paramsType.length() > 1)
			{
				if (Character.isWhitespace(arg.paramsType.charAt(arg.paramsType.length() - 1)))
				{
					arg.paramsType = arg.paramsType.substring(0, arg.paramsType.length() - 1);
				}
			}
			if (i < node.parameters.size() - 1)
				arg.paramsType += ", ";
			i += 1;
		}
		UMLType returnUMLType = new UMLType(arg.env, false);
		node.result.apply(new UMLTypeVisitor(), returnUMLType);
		arg.returnType = returnUMLType.inClassType;
		
		return null;
	}

	@Override
	protected List<Object> newCollection()
	{
		return new Vector<Object>();
	}
}
