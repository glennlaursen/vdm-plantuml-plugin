package plugins.VDM2UML;

import java.util.List;
import java.util.Vector;

import com.fujitsu.vdmj.tc.types.TCInMapType;
import com.fujitsu.vdmj.tc.types.TCMapType;
import com.fujitsu.vdmj.tc.types.TCNamedType;
import com.fujitsu.vdmj.tc.types.TCOptionalType;
import com.fujitsu.vdmj.tc.types.TCSeq1Type;
import com.fujitsu.vdmj.tc.types.TCSeqType;
import com.fujitsu.vdmj.tc.types.TCSet1Type;
import com.fujitsu.vdmj.tc.types.TCSetType;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.tc.types.visitors.TCLeafTypeVisitor;

import plugins.VDM2UML.UMLType.Type;

public class UMLAssociationCheckVisitor extends TCLeafTypeVisitor<Object, List<Object>, UMLType>
{
	@Override
	public List<Object> caseType(TCType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			return null;
		}

		if (node.isClass(arg.env))
		{
			arg.endClass = node.toString();
			arg.isAsoc = true;
		}
		else
		{
			arg.isAsoc = false;
		}
		return null;
	}

	@Override
	public List<Object> caseNamedType(TCNamedType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth == 1)
		{
			return null;
		}
		
		if (node.isClass(arg.env))
		{
			arg.endClass = node.toString();
			arg.isAsoc = true;
		}
		else
		{
			arg.isAsoc = false;
		}
		return null;
	}

	@Override
	public List<Object> caseSet1Type(TCSet1Type node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth > 2)
		{	
			return null;
		}
		if (arg.depth == 2 && !arg.isMap)
		{
			return null;
		}

		arg.prevType = Type.SET1;
		arg.multiplicity += "1..*";

		node.setof.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSetType(TCSetType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth > 2)
		{	
			return null;
		}
		if (arg.depth == 2 && !arg.isMap)
		{
			return null;
		}

		arg.prevType = Type.SET;
		arg.multiplicity += "*";

		node.setof.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSeq1Type(TCSeq1Type node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth > 2)
		{	
			return null;
		}
		if (arg.depth == 2 && !arg.isMap)
		{
			return null;
		}

		arg.prevType = Type.SEQ1;
		arg.multiplicity += "(1..*)";

		node.seqof.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseSeqType(TCSeqType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth > 2)
		{	
			return null;
		}
		if (arg.depth == 2 && !arg.isMap)
		{
			return null;
		}

		arg.prevType = Type.SEQ;
		arg.multiplicity += "(*)";

		node.seqof.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

	@Override
	public List<Object> caseOptionalType(TCOptionalType node, UMLType arg)
	{
        arg.depth++;
		if (arg.depth > 2)
		{	
			return null;
		}
		if (arg.depth == 2 && !arg.isMap)
		{
			return null;
		}
		arg.prevType = Type.OPTIONAL;
		arg.multiplicity += "0..1";

		node.type.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

	@Override
	public List<Object> caseInMapType(TCInMapType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth > 1)
		{
			return null;
		}
		
		arg.isMap = true;

		// TODO: Use visitor for qualifier
		arg.qualifier += "(";
		arg.qualifier += node.from.toString();
		arg.qualifier += ")";
		
		arg.prevType = Type.INMAP;
		node.to.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

    @Override
	public List<Object> caseMapType(TCMapType node, UMLType arg)
	{
		arg.depth++;
		if (arg.depth > 1)
		{
			return null;
		}
		
		arg.isMap = true;

		// TODO: Use visitor for qualifier
		arg.qualifier += node.from.toString();
		
		arg.prevType = Type.MAP;
		node.to.apply(new UMLAssociationCheckVisitor(), arg);

		return null;
	}

	@Override
	protected List<Object> newCollection()
	{
		return new Vector<Object>();
	}
}
