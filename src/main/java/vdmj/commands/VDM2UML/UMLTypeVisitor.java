package vdmj.commands.VDM2UML;

import java.util.Vector;
import java.util.List;

import com.fujitsu.vdmj.tc.types.TCInMapType;
import com.fujitsu.vdmj.tc.types.TCMapType;
import com.fujitsu.vdmj.tc.types.TCOptionalType;
import com.fujitsu.vdmj.tc.types.TCProductType;
import com.fujitsu.vdmj.tc.types.TCRecordType;
import com.fujitsu.vdmj.tc.types.TCSeqType;
import com.fujitsu.vdmj.tc.types.TCSet1Type;
import com.fujitsu.vdmj.tc.types.TCSeq1Type;
import com.fujitsu.vdmj.tc.types.TCSetType;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.tc.types.TCUnionType;
import com.fujitsu.vdmj.tc.types.visitors.TCLeafTypeVisitor;

public class UMLTypeVisitor extends TCLeafTypeVisitor<Object, List<Object>, UMLType>
{
	@Override
	public List<Object> caseType(TCType node, UMLType arg)
	{
		arg.depth++;
		if (node.isClass(arg.env))
		{
			arg.endClass = node.toString();
			arg.isAsoc = true;
		} 
		else if (!arg.isMap)
			arg.inClassType += node.toString();
		
		return null;
	}

	@Override
	public List<Object> caseSet1Type(TCSet1Type node, UMLType arg)
	{
		arg.depth++;
		setSeqConstructor("1..*", "set1 of ", arg);

		if (arg.depth < 3)
			node.setof.apply(new UMLTypeVisitor(), arg);
		
		return null;
	}

    @Override
	public List<Object> caseSetType(TCSetType node, UMLType arg)
	{
		arg.depth++;
		setSeqConstructor("*", "set of ", arg);

		if (arg.depth < 3)
			node.setof.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSeq1Type(TCSeq1Type node, UMLType arg)
	{
		arg.depth++;
		setSeqConstructor("(1..*)", "seq1 of ", arg);

		if (arg.depth < 3)
			node.seqof.apply(new UMLTypeVisitor(), arg);
		
		return null;
	}

    @Override
	public List<Object> caseSeqType(TCSeqType node, UMLType arg)
	{
		arg.depth++;
        setSeqConstructor("(*)", "seq of ", arg);

		if (arg.depth < 3)
			node.seqof.apply(new UMLTypeVisitor(), arg);
		
		return null;
	}

	private void setSeqConstructor(String _multiplicity, String _type, UMLType arg)
	{
		if (arg.depth < 2)
			arg.multiplicity += _multiplicity;
		
		if (!arg.isMap && !arg.inClassType.contains("map"))
			arg.inClassType += _type;
	}

	@Override
	public List<Object> caseInMapType(TCInMapType node, UMLType arg)
	{
		arg.depth++;
		arg.inClassType += "inmap ";

		if (arg.depth > 2)
			return null;
		
		if (arg.depth < 2)
			arg.isMap = true;
			if (!node.from.isClass(arg.env))
			{
				arg.qualifier += "(";	
				arg.qualifier += node.from.toString();
				arg.qualifier += ")";
			}
		node.to.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseMapType(TCMapType node, UMLType arg)
	{
		arg.depth++;
		arg.inClassType += "map ";

		if (arg.depth > 2)
			return null;
		
		if (arg.depth < 2)
			arg.isMap = true;
			if (!node.from.isClass(arg.env))
				arg.qualifier += node.from.toString();
		
		node.to.apply(new UMLTypeVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseProductType(TCProductType node, UMLType arg)
	{
        /** Set type to * */
		arg.depth++;
		if (arg.depth < 3 && !arg.isMap)
		for (int i = 0; i < node.types.size()-1; i++)
			arg.inClassType += "*";
		
		return null;
	}

    @Override
	public List<Object> caseUnionType(TCUnionType node, UMLType arg)
	{
        /** Set type to | */
		arg.depth++;
		if (arg.depth < 3 && !arg.isMap)
			for (int i = 0; i < node.types.size()-1; i++)
				arg.inClassType += "|";
		
		return null;
	}

    @Override
	public List<Object> caseOptionalType(TCOptionalType node, UMLType arg)
	{
        /** Set type to [] */
		arg.depth++;
		if (arg.depth < 3 && !arg.isMap)
			arg.inClassType += "[]";
		return null;
	}

	@Override
	public List<Object> caseRecordType(TCRecordType node, UMLType arg)
	{
        /** Set type to :: */
		arg.depth++;
		if (arg.depth < 3 && !arg.isMap)
			arg.inClassType += "::";
		
		return null;
	}

	@Override
	protected List<Object> newCollection()
	{
		return new Vector<Object>();
	}
}

