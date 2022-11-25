package plugins.VDM2UML;

import java.util.List;
import java.util.Vector;

import com.fujitsu.vdmj.tc.types.TCBracketType;
import com.fujitsu.vdmj.tc.types.TCInMapType;
import com.fujitsu.vdmj.tc.types.TCMapType;
import com.fujitsu.vdmj.tc.types.TCNamedType;
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

public class UMLMapVisitor extends TCLeafTypeVisitor<Object, List<Object>, UMLCost>
{
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

	@Override
	public List<Object> caseType(TCType node, UMLCost umlCost)
	{
		if (!isBasicType(node))
		{
			umlCost.cost = umlCost.cost + 1;
		}
		return null;
	}

	@Override
	public List<Object> caseNamedType(TCNamedType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		return null;
	}

	@Override
	public List<Object> caseSet1Type(TCSet1Type node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.setof.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

    @Override
	public List<Object> caseSetType(TCSetType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.setof.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

    @Override
	public List<Object> caseSeq1Type(TCSeq1Type node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.seqof.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

    @Override
	public List<Object> caseSeqType(TCSeqType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.seqof.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

	@Override
	public List<Object> caseOptionalType(TCOptionalType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.type.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

	@Override
	public List<Object> caseInMapType(TCInMapType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.from.apply(new UMLMapVisitor(), umlCost);
		node.to.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

    @Override
	public List<Object> caseMapType(TCMapType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		node.from.apply(new UMLMapVisitor(), umlCost);
		node.to.apply(new UMLMapVisitor(), umlCost);
		return null;
	}

    @Override
	public List<Object> caseProductType(TCProductType node, UMLCost umlCost)
	{
		int i = 0;
		for (TCType type : node.types)
		{
			type.apply(new UMLMapVisitor(), umlCost);
			if (i < node.types.size() - 1) 
			{
				umlCost.cost = umlCost.cost + 1;
			}
			i++;
		}
		return null;
	}

    @Override
	public List<Object> caseUnionType(TCUnionType node, UMLCost umlCost)
	{
		int i = 0;
		for (TCType type : node.types)
		{
			type.apply(new UMLMapVisitor(), umlCost);
			if (i < node.types.size() - 1) 
			{
				umlCost.cost = umlCost.cost + 1;
			}
			i++;
		}
		return null;
	}

	@Override
	public List<Object> caseRecordType(TCRecordType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		return null;
	}

	@Override
	public List<Object> caseBracketType(TCBracketType node, UMLCost umlCost)
	{
		umlCost.cost = umlCost.cost + 1;
		return null;
	}

	@Override
	protected List<Object> newCollection()
	{
		return new Vector<Object>();
	}
}
