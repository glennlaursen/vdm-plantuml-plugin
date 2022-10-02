package plugins.VDM2UML;

import com.fujitsu.vdmj.lex.Token;
import com.fujitsu.vdmj.tc.definitions.TCAccessSpecifier;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCInstanceVariableDefinition;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.definitions.TCValueDefinition;
import com.fujitsu.vdmj.tc.definitions.visitors.TCDefinitionVisitor;
import com.fujitsu.vdmj.tc.types.TCType;

public class UMLGenerator extends TCDefinitionVisitor<Object, Buffers>
{
	@Override
	public Object caseDefinition(TCDefinition node, Buffers arg)
	{
		return null;
	}

	@Override
	public Object caseClassDefinition(TCClassDefinition node, Buffers arg)
	{
		arg.defs.append("class ");
		arg.defs.append(node.name.getName());
		arg.defs.append("\n{\n");

		for (TCDefinition def: node.definitions)
		{
			def.apply(this, arg);
		}

		arg.defs.append("}\n\n");
		return null;
	}
	
	@Override
	public Object caseInstanceVariableDefinition(TCInstanceVariableDefinition node, Buffers arg)
	{	
		TCType type = node.getType();
		UMLType umlType = new UMLType(Buffers.env);
		type.apply(new UMLTypeVisitor(), umlType);

		String visibility = visibility(node.accessSpecifier);
		String varName = node.name.getName();
		String className = node.classDefinition.name.getName();

		if (umlType.isAsoc) 
		{
			/* 
			 * Create instance variable as association 
			 */

			arg.asocs.append(className);
			if (!umlType.qualifier.isEmpty())
			{
				arg.asocs.append(" \"[");
				arg.asocs.append(umlType.qualifier);
				arg.asocs.append("]\"");
			}
			arg.asocs.append(" --> ");
			if (!umlType.multiplicity.isEmpty())
			{
				arg.asocs.append("\"");
				arg.asocs.append(umlType.multiplicity);
				arg.asocs.append("\" ");
			}
			arg.asocs.append(umlType.endClass);
			arg.asocs.append(" : ");
			if (!visibility.isEmpty())
			{
				arg.asocs.append(visibility);
			}
			arg.asocs.append(varName);
			arg.asocs.append("\n");
		} 
		else 
		{
			/*
			 * Create instance variable as attribute in class 
			 */

			arg.defs.append("\t");
			if (!visibility.isEmpty())
			{
				arg.defs.append(visibility);
			}
			arg.defs.append(varName + ": " + umlType.inClassType);
			arg.defs.append("\n");
		}
		
		return null;
	}
	
	@Override
	public Object caseTypeDefinition(TCTypeDefinition node, Buffers arg)
	{
		TCType type = node.getType();
		UMLType umlType = new UMLType(Buffers.env);
		type.apply(new UMLTypeVisitor(), umlType);

		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(node.name.getName());
		arg.defs.append(": ");
		arg.defs.append(umlType.inClassType);
		arg.defs.append(" <<type>>");
		arg.defs.append("\n");		

		return null; 
	}

	@Override
	public Object caseValueDefinition(TCValueDefinition node, Buffers arg)
	{
		for (TCDefinition def: node.getDefinitions())
		{
			TCType type = def.getType();
			UMLType umlType = new UMLType(Buffers.env);
			type.apply(new UMLTypeVisitor(), umlType);
			
			arg.defs.append("\t");
			arg.defs.append(visibility(def.accessSpecifier));
			arg.defs.append(def.name.getName());
			arg.defs.append(": ");
			arg.defs.append(umlType.inClassType);
			arg.defs.append(" <<value>>");
			arg.defs.append("\n");
		}

		return null;
	}
	
	@Override
	public Object caseExplicitFunctionDefinition(TCExplicitFunctionDefinition node, Buffers arg)
	{
		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(node.name.getName());
		arg.defs.append(getPlantArgs(node.getType().toString()));
		arg.defs.append(" <<function>>");
		arg.defs.append("\n");

		return null;
	}
	
	@Override
	public Object caseExplicitOperationDefinition(TCExplicitOperationDefinition node, Buffers arg)
	{	
		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(" ");
		arg.defs.append(node.name.getName());
		arg.defs.append(getPlantArgs(node.getType().toString()));
		arg.defs.append("\n");

		return null;
	}


	private String getPlantArgs(String args)
	{
		String str0 = removeBrackets(args);
		String splitter = "";
		if (str0.contains("->"))
			splitter = " ->";

		if (str0.contains("==>"))
			splitter = " ==>";
		
		String seg1[] = str0.split(splitter);
		String out = seg1[seg1.length - 1];
		String vdmArgLine = seg1[0];

		if(args.contains("*"))
		{
			String seg2[] = seg1[0].split(" \\* "); 
			vdmArgLine = seg2[0];
			for(int n = 1 ; n < seg2.length ; n++)
			{
				vdmArgLine = vdmArgLine + ", " + seg2[n];            
			} 
		}
		return "(" + vdmArgLine + ")" + ":" + out; 
	}

	private String visibility(TCAccessSpecifier access)
	{	
		String res = "";

		if (access.access == Token.PUBLIC)
			res += "+";
		else if (access.access == Token.PRIVATE)
			res += "-";
		else if (access.access == Token.PROTECTED)
			res += "#";
		
		if (access.isStatic)
			res += "{static}";
		
		return res;
	}

	private String removeBrackets(String str)
	{
		if (str.contains("("))
			str = str.replace("(", "");
		
		if (str.contains(")"))
			str = str.replace(")", "");

		return str;
	}
}	

