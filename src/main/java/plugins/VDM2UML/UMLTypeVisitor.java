package plugins.VDM2UML;

import java.util.List;
import java.util.Vector;
import java.awt.Point;

import com.fujitsu.vdmj.tc.types.TCBracketType;
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
	static int MAX_LOW_CAPACITY = 1;			// Capacity map, set, seq and optional types
	static int MAX_HIGH_CAPACITY = 3;			// Capacity union, product and record (?) types
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

	private void setTypeString(String typeString, UMLType arg)
	{
		if (arg.useTempType && arg.useMapType)
		{
			if (arg.tempBeforeMap)
			{
				arg.mapType += typeString;
			}
			else
			{
				arg.tempType += typeString;
			}
			return;
		}
		if (arg.useMapType)
		{
			arg.mapType += typeString;
			return;
		}
		if (arg.useTempType)
		{
			arg.tempType += typeString;
			return;
		}
		arg.inClassType += typeString;
		return;
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

		if (isOverCapacity)
		{
			typeString = "... ";
			setTypeString(typeString, arg);
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
		typeString += node.toString();

		setTypeString(typeString, arg);
		
		return null;
	}

	@Override
	public List<Object> caseNamedType(TCNamedType node, UMLType arg)
	{
		String typeString = "";

		if (arg.isType && arg.namedType.equals(node.toString())) 
		{
			node.type.apply(new UMLTypeVisitor(), arg);
			return null;
		}
		else
		{
			Boolean isOverCapacity = checkAndSetCapacities(arg);

			if (isOverCapacity)
			{
				typeString += "... ";
				setTypeString(typeString, arg);
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
			
			typeString += node.toString();
			setTypeString(typeString, arg);
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

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		setTypeString(typeString, arg);

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

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		setTypeString(typeString, arg);

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

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		setTypeString(typeString, arg);

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

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		setTypeString(typeString, arg);

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

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		setTypeString(typeString, arg);

		node.type.apply(new UMLTypeVisitor(), arg);

		typeString = "]";
		setTypeString(typeString, arg);

		return null;
	}

	@Override
	public List<Object> caseInMapType(TCInMapType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		// arg.addCapacity(MAX_LOW_CAPACITY * 2);

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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
		setTypeString(typeString, arg);

		isOverCapacity = checkAndSetCapacities(arg);
		if (isOverCapacity || arg.useMapType)
		{
			typeString = "inmap...";
			setTypeString(typeString, arg);
			return null;
		}

		UMLCost fromCost = new UMLCost();
		UMLCost toCost = new UMLCost();
		node.from.apply(new UMLMapVisitor(), fromCost);
		node.to.apply(new UMLMapVisitor(), toCost);
		
		Boolean abstractFrom = false;
		Boolean abstractTo = false;
		if (fromCost.cost > MAX_LOW_CAPACITY)
		{
			if (toCost.cost != 0)
			{
				abstractFrom = true;
			}
		}
		if (toCost.cost > MAX_LOW_CAPACITY)
		{
			if (fromCost.cost != 0)
			{
				abstractTo = true;
			}
		}
		
		arg.prevType = Type.INMAP;
		arg.useMapType = true;
		if (arg.useTempType)
		{
			arg.tempBeforeMap = true;
		}
		else
		{
			arg.tempBeforeMap = false;
		}
		typeString = "inmap ";
		setTypeString(typeString, arg);

		if (abstractFrom)
		{
			arg.addCapacity(MAX_LOW_CAPACITY);
		}
		else
		{
			arg.addCapacity(MAX_LOW_CAPACITY * 2);
		}
		int capacityNum = arg.capacities.size();
		node.from.apply(new UMLTypeVisitor(), arg);
		
		int newCapacityNum = arg.capacities.size();
		for (int i = capacityNum; i < newCapacityNum; i++)
		{
			arg.capacities.remove(arg.capacities.size() - 1);
		}
		arg.capacities.remove(arg.capacities.size() - 1);

		arg.prevType = Type.INMAP;
		typeString = " to ";
		setTypeString(typeString, arg);

		if (abstractTo)
		{
			arg.addCapacity(MAX_LOW_CAPACITY);
		}
		else
		{
			arg.addCapacity(MAX_LOW_CAPACITY * 2);
		}
		capacityNum = arg.capacities.size();
		node.to.apply(new UMLTypeVisitor(), arg); 

		arg.useMapType = false;
		arg.tempBeforeMap = true;
		setTypeString(arg.mapType, arg);
		arg.mapType = "";

		return null;
	}

    @Override
	public List<Object> caseMapType(TCMapType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		// arg.addCapacity(MAX_LOW_CAPACITY * 2);

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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
		setTypeString(typeString, arg);

		isOverCapacity = checkAndSetCapacities(arg);
		if (isOverCapacity || arg.useMapType)
		{
			typeString = "map...";
			setTypeString(typeString, arg);
			return null;
		}

		UMLCost fromCost = new UMLCost();
		UMLCost toCost = new UMLCost();
		node.from.apply(new UMLMapVisitor(), fromCost);
		node.to.apply(new UMLMapVisitor(), toCost);
		
		Boolean abstractFrom = false;
		Boolean abstractTo = false;
		if (fromCost.cost > MAX_LOW_CAPACITY)
		{
			if (toCost.cost != 0)
			{
				abstractFrom = true;
			}
		}
		if (toCost.cost > MAX_LOW_CAPACITY)
		{
			if (fromCost.cost != 0)
			{
				abstractTo = true;
			}
		}
		System.out.println("(from,to): (" + fromCost.cost + "," + toCost.cost + "), for type: " + node.toString());
		
		arg.prevType = Type.MAP;
		arg.useMapType = true;
		if (arg.useTempType)
		{
			arg.tempBeforeMap = true;
		}
		else
		{
			arg.tempBeforeMap = false;
		}
		typeString = "map ";
		setTypeString(typeString, arg);

		if (abstractFrom)
		{
			arg.addCapacity(MAX_LOW_CAPACITY);
		}
		else
		{
			arg.addCapacity(MAX_LOW_CAPACITY * 2);
		}
		int capacityNum = arg.capacities.size();
		node.from.apply(new UMLTypeVisitor(), arg);
		
		int newCapacityNum = arg.capacities.size();
		for (int i = capacityNum; i < newCapacityNum; i++)
		{
			arg.capacities.remove(arg.capacities.size() - 1);
		}
		arg.capacities.remove(arg.capacities.size() - 1);

		arg.prevType = Type.MAP;
		typeString = " to ";
		setTypeString(typeString, arg);

		if (abstractTo)
		{
			arg.addCapacity(MAX_LOW_CAPACITY);
		}
		else
		{
			arg.addCapacity(MAX_LOW_CAPACITY * 2);
		}
		capacityNum = arg.capacities.size();
		node.to.apply(new UMLTypeVisitor(), arg); 

		arg.useMapType = false;
		arg.tempBeforeMap = true;
		setTypeString(arg.mapType, arg);
		arg.mapType = "";

		return null;
	}

    @Override
	public List<Object> caseProductType(TCProductType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_HIGH_CAPACITY);
		int capacityNum = arg.capacities.size();
		int newCapacityNum;
		System.out.println("capacities (product): " + arg.capacities);
		isOverCapacity = checkCapacities(arg);

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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
			typeString = "*... ";
			setTypeString(typeString, arg);
			return null;
		}

		setTypeString(typeString, arg);

		int i = 0;
		arg.useTempType = true;
		if (arg.useMapType)
		{
			arg.tempBeforeMap = false;
		}
		else
		{
			arg.tempBeforeMap = true;
		}

		for (TCType type : node.types)
		{
			arg.prevType = Type.PRODUCT;
			type.apply(new UMLTypeVisitor(), arg);
			newCapacityNum = arg.capacities.size();
			if (checkCapacities(arg) || i == node.types.size() - 1)
			{
				i = node.types.size();
			}
			else
			{
				setTypeString(" * ", arg);
			}
			for (int j = capacityNum; j < newCapacityNum; j++)
			{
				arg.capacities.remove(arg.capacities.size() - 1);
			}
			if (i > 1)
			{
				checkAndSetCapacities(arg);
			}
			i++;
		}
		
		arg.useTempType = false;
		arg.tempBeforeMap = false;
		isOverCapacity = checkCapacities(arg);
		typeString = "";

		if (isOverCapacity)
		{
			for (i = 1; i < node.types.size(); i++)
			{
				typeString += "*";
			}
		}
		else
		{
			typeString += arg.tempType;
		}
		arg.tempType = "";
		
		setTypeString(typeString, arg);

		return null;
	}

    @Override
	public List<Object> caseUnionType(TCUnionType node, UMLType arg)
	{
        arg.depth++;
		String typeString = "";
		int capacityNum = arg.capacities.size();
		int newCapacityNum;

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_HIGH_CAPACITY);
		System.out.println("capacities (union): " + arg.capacities);

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		setTypeString(typeString, arg);

		if (node.types.size() > MAX_NUM_OF_COMPOSITE_TYPES || arg.useTempType)
		{
			typeString = "|... ";
			setTypeString(typeString, arg);
			return null;
		}
		
		int i = 0;
		arg.useTempType = true;
		if (arg.useMapType)
		{
			arg.tempBeforeMap = false;
		}
		else
		{
			arg.tempBeforeMap = true;
		}

		for (TCType type : node.types)
		{
			arg.prevType = Type.UNION;
			type.apply(new UMLTypeVisitor(), arg);
			newCapacityNum = arg.capacities.size();
			if (checkCapacities(arg) || i == node.types.size() - 1)
			{
				i = node.types.size();
			}
			else
			{
				setTypeString(" | ", arg);
			}
			for (int j = capacityNum; j < newCapacityNum; j++)
			{
				arg.capacities.remove(arg.capacities.size() - 1);
			}
			if (i > 1)
			{
				checkAndSetCapacities(arg);
			}
			i++;
		}

		arg.useTempType = false;
		arg.tempBeforeMap = false;
		isOverCapacity = checkCapacities(arg);
		typeString = "";

		if (isOverCapacity)
		{
			for (i = 1; i < node.types.size(); i++)
			{
				typeString += "|";
			}
		}
		else
		{
			typeString += arg.tempType;
		}
		arg.tempType = "";
		
		setTypeString(typeString, arg);

		return null;
	}

	@Override
	public List<Object> caseRecordType(TCRecordType node, UMLType arg)
	{
		// TODO: Enter the identifiers of the fields as types
       	arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);
		arg.addCapacity(MAX_HIGH_CAPACITY);

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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

		if (!arg.isType)
		{
			typeString += node.name.toString();
		}
		else
		{
			typeString += "::";
		}

		setTypeString(typeString, arg);
		
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
			
			// Remove whitespace at end parameter
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

			// Remove whitespace at end parameter
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
	public List<Object> caseBracketType(TCBracketType node, UMLType arg)
	{
		arg.depth++;
		String typeString = "";

		Boolean isOverCapacity = checkAndSetCapacities(arg);

		if (isOverCapacity)
		{
			typeString += "... ";
			setTypeString(typeString, arg);
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
		typeString += "?";
		setTypeString(typeString, arg);
		// node.type.apply(new UMLTypeVisitor(), arg);
		return null;
	}

	@Override
	protected List<Object> newCollection()
	{
		return new Vector<Object>();
	}
}
