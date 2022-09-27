package vdmj.commands.VDM2UML;

import com.fujitsu.vdmj.lex.Token;
import com.fujitsu.vdmj.tc.definitions.TCAccessSpecifier;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCExplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCImplicitFunctionDefinition;
import com.fujitsu.vdmj.tc.definitions.TCImplicitOperationDefinition;
import com.fujitsu.vdmj.tc.definitions.TCInstanceVariableDefinition;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.definitions.TCValueDefinition;
import com.fujitsu.vdmj.tc.definitions.visitors.TCDefinitionVisitor;
import com.fujitsu.vdmj.tc.types.TCType;

public class UMLGenerator extends TCDefinitionVisitor<Object, PlantBuilder>
{
	@Override
	public Object caseDefinition(TCDefinition node, PlantBuilder arg)
	{
		return null;
	}

	@Override
	public Object caseClassDefinition(TCClassDefinition node, PlantBuilder arg)
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
	public Object caseInstanceVariableDefinition(TCInstanceVariableDefinition node, PlantBuilder arg)
	{	
		TCType type = node.getType();
		UMLType umlType = new UMLType(PlantBuilder.env, false);
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
	public Object caseTypeDefinition(TCTypeDefinition node, PlantBuilder arg)
	{
		TCType type = node.getType();
		UMLType umlType = new UMLType(PlantBuilder.env, true);
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
	public Object caseValueDefinition(TCValueDefinition node, PlantBuilder arg)
	{
		for (TCDefinition def : node.getDefinitions()) 
		{
			TCType type = def.getType();
			UMLType umlType = new UMLType(PlantBuilder.env, false);
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
	public Object caseExplicitFunctionDefinition(TCExplicitFunctionDefinition node, PlantBuilder arg)
	{
		TCType type = node.getType();
		UMLType umlType = new UMLType(PlantBuilder.env, false);
		type.apply(new UMLTypeVisitor(), umlType);

		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(node.name.getName());
		arg.defs.append("(");
		arg.defs.append(umlType.paramsType);
		arg.defs.append("): ");
		arg.defs.append(umlType.returnType);
		arg.defs.append(" <<function>>");
		arg.defs.append("\n");

		return null;
	}
	
	@Override
	public Object caseExplicitOperationDefinition(TCExplicitOperationDefinition node, PlantBuilder arg)
	{	
		TCType type = node.getType();
		UMLType umlType = new UMLType(PlantBuilder.env, false);
		type.apply(new UMLTypeVisitor(), umlType);

		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(node.name.getName());
		arg.defs.append("(");
		arg.defs.append(umlType.paramsType);
		arg.defs.append(")");
		if (!(umlType.returnType == "" || umlType.returnType == "()"))
		{
			arg.defs.append(": ");
			arg.defs.append(umlType.returnType);
		}
		arg.defs.append("\n");

		return null;
	}

	public Object caseImplicitFunctionDefinition(TCImplicitFunctionDefinition node, PlantBuilder arg) {
		TCType type = node.getType();
		UMLType umlType = new UMLType(PlantBuilder.env, false);
		type.apply(new UMLTypeVisitor(), umlType);

		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(node.name.getName());
		arg.defs.append("(");
		arg.defs.append(umlType.paramsType);
		arg.defs.append(")");
		if (!(umlType.returnType == "" || umlType.returnType == "()"))
		{
			arg.defs.append(": ");
			arg.defs.append(umlType.returnType);
		}
		arg.defs.append(" <<function>>");
		arg.defs.append("\n");

		return null;
	}
	  
	public Object caseImplicitOperationDefinition(TCImplicitOperationDefinition node, PlantBuilder arg) {
		TCType type = node.getType();
		UMLType umlType = new UMLType(PlantBuilder.env, false);
		type.apply(new UMLTypeVisitor(), umlType);

		arg.defs.append("\t");
		arg.defs.append(visibility(node.accessSpecifier));
		arg.defs.append(node.name.getName());
		arg.defs.append("(");
		arg.defs.append(umlType.paramsType);
		arg.defs.append(")");
		if (!(umlType.returnType == "" || umlType.returnType == "()"))
		{
			arg.defs.append(": ");
			arg.defs.append(umlType.returnType);
		}
		arg.defs.append(" <<function>>");
		arg.defs.append("\n");

		return null;
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
		
		return res;
	}
}