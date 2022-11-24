package plugins.VDM2UML;

import java.util.List;
import java.util.Vector;
import java.awt.Point;

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
	static int MAX_LOW_CAPACITY = 1;			// Capacityset, seq and optional types
	static int MAX_HIGH_CAPACITY = 3;			// Capacitymap, union, product and record (?) types
	static int MAX_NUM_OF_COMPOSITE_TYPES = 5;

	// Check if a type is onethe basic typesVDM
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
		else if (node.toString().contains("<"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private String getAbstractType(Type type)
	{
		String res = "";
		switch(type)
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

	private Boolean checkAndSetCapacities(UMLType arg)
	{
		Boolean isOverCapacity = false;
		for (int i = 0; i < arg.capacities.size(); i++)
		{
			arg.capacities.set(i, new Point(arg.capacities.get(i).x, arg.capacities.get(i).y + 1));
			if (arg.capacities.get(i).y > arg.capacities.get(i).x)
			{
				isOverCapacity = true;
			}
		}
		return isOverCapacity;
	}

	private Boolean checkCapacities(UMLType arg)
	{
		Boolean isOverCapacity = false;
		for (int i = 0; i < arg.capacities.size(); i++)
		{
			if (arg.capacities.get(i).y > arg.capacities.get(i).x)
			{
				isOverCapacity = true;
			}
		}
		return isOverCapacity;
	}

	@Override
	public List<Object> caseType(TCType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = false;
		if (!isBasicType(node))
		{
			isOverCapacity = checkAndSetCapacities(arg);
		}
		else
		{
			isOverCapacity = checkCapacities(arg);
		}

		if (!arg.isMap)
		{
			if (isOverCapacity)
			{
				typeString = "...";
			}
			else
			{
				if (arg.prevType == Type.SET1 ||
					arg.prevType == Type.SET  || 
					arg.prevType == Type.SEQ1 || 
					arg.prevType == Type.SEQ
				)
				{
					typeString += " of ";
				}
				typeString += node.toString();
			}
		}
		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}
		
		return null;
	}

	@Override
	public List<Object> caseNamedType(TCNamedType node, UMLType arg)
	{
		String typeString = "";

		if (arg.isType) 
		{
			node.type.apply(new UMLTypeVisitor(), arg);
		}
		else
		{
			Boolean isOverCapacity = checkAndSetCapacities(arg);

			if (isOverCapacity)
			{
				typeString += "...";
			}
			else
			{
				if (arg.prevType == Type.SET1 ||
					arg.prevType == Type.SET  || 
					arg.prevType == Type.SEQ1 || 
					arg.prevType == Type.SEQ
				)
				{
					typeString += " of ";
				}
				typeString += node.toString();
			}
		}
		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		return null;
	}

	@Override
	public List<Object> caseSet1Type(TCSet1Type node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY);		

		// arg.abstractedType = Type.SET1;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 || 
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		arg.prevType = Type.SET1;
		typeString += "set1";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.setof.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSetType(TCSetType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY);

		// arg.abstractedType = Type.SET;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 || 
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		arg.prevType = Type.SET;
		typeString += "set";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.setof.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSeq1Type(TCSeq1Type node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY);

		// arg.abstractedType = Type.SEQ1;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 || 
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		arg.prevType = Type.SEQ1;
		typeString += "seq1";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.seqof.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSeqType(TCSeqType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY);

		// arg.abstractedType = Type.SEQ;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 || 
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		arg.prevType = Type.SEQ;
		typeString += "seq";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.seqof.apply(new UMLTypeVisitor(), arg);

		return null;
	}

	@Override
	public List<Object> caseOptionalType(TCOptionalType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";
		
		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY);

		// arg.abstractedType = Type.OPTIONAL;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 ||
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		arg.prevType = Type.OPTIONAL;
		if (arg.depth == 1)
		{
			arg.multiplicity += "0..1";
		}
		typeString += "[";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.type.apply(new UMLTypeVisitor(), arg);

		typeString = "]";
		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		return null;
	}

	@Override
	public List<Object> caseInMapType(TCInMapType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY * 2);

		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 ||
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}
		
		arg.prevType = Type.INMAP;
		typeString += "inmap ";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.from.apply(new UMLTypeVisitor(), arg);
		
		isOverCapacity = checkCapacities(arg);
		// TODO: Handle maps better
		if (isOverCapacity)
		{
			return null;
		}

		typeString += " to ";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.to.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseMapType(TCMapType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_LOW_CAPACITY * 2);

		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 ||
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}
		
		arg.prevType = Type.MAP;
		typeString += "map ";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.from.apply(new UMLTypeVisitor(), arg);
		isOverCapacity = checkCapacities(arg);
		

		typeString = " to ";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		node.to.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseProductType(TCProductType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_HIGH_CAPACITY);

		// arg.abstractedType = Type.PRODUCT;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 ||
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		if (node.types.size() > MAX_NUM_OF_COMPOSITE_TYPES || arg.useTempType)
		{
			typeString += "*...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		int i = 1;
		arg.useTempType = true;
		for (TCType type : node.types)
		{
			arg.prevType = Type.PRODUCT;
			type.apply(new UMLTypeVisitor(), arg);
			if (i < node.types.size())
			{
				arg.tempType += " * ";
				if (checkAndSetCapacities(arg))
				{
					i = node.types.size();
				}
			}
			i++;
		}
		isOverCapacity = checkCapacities(arg);
		if (isOverCapacity)
		{
			for (i = 1; i < node.types.size(); i++)
			{
				arg.inClassType += "|";
			}
		}
		else
		{
			arg.inClassType += arg.tempType;
		}
		arg.tempType = "";
		arg.useTempType = false;

		return null;
	}

    @Override
	public List<Object> caseUnionType(TCUnionType node, UMLType arg)
	{
        arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_HIGH_CAPACITY);

		// arg.abstractedType = Type.UNION;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 ||
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}

		if (node.types.size() > MAX_NUM_OF_COMPOSITE_TYPES || arg.useTempType)
		{
			typeString += "|...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		
		arg.useTempType = true;
		int i = 1;
		for (TCType type : node.types)
		{
			arg.prevType = Type.UNION;
			type.apply(new UMLTypeVisitor(), arg);
			if (i < node.types.size())
			{
				arg.tempType += " | ";
				if (checkAndSetCapacities(arg))
				{
					i = node.types.size();
				}
			}
			i++;
		}
		isOverCapacity = checkCapacities(arg);
		if (isOverCapacity)
		{
			for (i = 1; i < node.types.size(); i++)
			{
				arg.inClassType += "|";
			}
		}
		else
		{
			arg.inClassType += arg.tempType;
		}
		arg.tempType = "";
		arg.useTempType = false;

		return null;
	}

	@Override
	public List<Object> caseRecordType(TCRecordType node, UMLType arg)
	{
       	arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_HIGH_CAPACITY);

		// arg.abstractedType = Type.RECORD;
		if (isOverCapacity)
		{
			typeString += "...";
			if (arg.useTempType)
			{
				arg.tempType += typeString;
			}
			else
			{
				arg.inClassType += typeString;
			}
			return null;
		}
		if (arg.prevType == Type.SET1 ||
			arg.prevType == Type.SET  || 
			arg.prevType == Type.SEQ1 || 
			arg.prevType == Type.SEQ
		)
		{
			typeString += " of ";
		}

		typeString += "::...";

		if (arg.useTempType)
		{
			arg.tempType += typeString;
		}
		else
		{
			arg.inClassType += typeString;
		}
		
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
			
			// Remove whitespace at endparameter
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

			// Remove whitespace at endparameter
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
