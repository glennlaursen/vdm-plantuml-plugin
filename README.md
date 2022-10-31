
# Support for PlantUML Visualizations of VDM Models 
The VDM-PlantUML plugin is integrated into the VDM-VSCode extension, which provides VDM language support for Visual Studio Code (VS Code). 
The plugin enables bi-directional translations between VDM models and the textually based diagram tool, [PlantUML](https://plantuml.com/). 
The object-oriented (OO) structure of the VDM models are represented in UML as [PlantUML class diagrams](https://plantuml.com/class-diagram).

For information about using the plugin on VS Code see the [VDM-VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#Translate-to-UML).

## Bi-directional Mapping:
The following section describes the common form between PlantUML class diagrams and VDM models. This common form is defined by defining UML constructs using VDM components.  

Before defining UML constructs like attributes, operations and associations, the way in which VDM types are treated in PlantUML will be described.  

### Basic Data Types
There is a one-to-one mapping of basic VDM data types between VDM and PlantUML, 
with the VDM symbol for the type being represented as the type in a UML element.


### Set, Sequence and Map Types
Non-associative, set, seq and map types are directily translated between PlantUML and VDM, with the VDM set, seq or map type being the type of the corrosponding UML attribute. 

If the type of the elements in a set, sequence or map is a class, the type is associative and shown in the UML model as an [association](https://github.com/jolnd/vdm-plantuml-plugin#association-definition). If the type of the elements is any other type, including another compound type that refers to a class, the type is non-associative and shown in the UML model as an [attribute](https://github.com/jolnd/vdm-plantuml-plugin#attribute-definition).

### Access specifiers
Access specifiers corrospond directly to element visibility in UML.

Visibility is defined as:

``` 
visibility = ‘+’
	   | ‘-’
	   | ‘#’    
``` 
For public, private and protected, respectively. The default visibility to any component is private.


### Class Declarations:
There is a one-to-one relationship between classes in UML and classes in VDM++/VDM-RT 
Attributes and operations in PlantUML are defined within class declarations. 

```
Syntax: class = ‘class’ identifier [class body] 

		class body = ‘{’ [definition block] ‘}’
			
			definition block = attribute definitions
					 | operation definitions		   
```

### Attribute Definition:
UML attributes occur when translating a VDM instance variable, type or value to UML.

```
Syntax: attribute definition = [visibility] identifier ‘:’ type [attribute stereotype]

	attribute stereotype = ‘<<type>>’
			     | ‘<<value>>’	
```
The attribute stereotype is used to differentiate between types, values and instance variables. If no stereotype is used, the attribute is considered an instance variable.  

### Operation Definition:
UML operations occur when translating VDM operations or functions to UML.

```
Syntax: operation definition = [visibility] identifier ‘(’ [type] ‘)’ ‘:’ type' [operation stereotype]

``` 
Where `type` is the discretionary type which the operation takes as argument, and `type'` is the discretionary return type of the operation.   
```
	operation stereotype = ‘<<function>>’
```
The operation stereotype is used to differentiate between VDM functions and operations. If no stereotype is used, the algorithm is considered an operation.


### Association Definition:
UML associations occur when the type of a VDM instance variable is a class or when the instance variable is a set, sequence or map type with a class as its subtype. When deciding if a map type from a type A to a type B is an association, type B is considered. Associations in plantUML are defined after [class declarations](https://github.com/jolnd/vdm-plantuml-plugin#class-declarations)

The use of set and sequence types dictate the multiplicity of the association and the use of the map type produce a qualified association.

```
Syntax:	association definition = class [qualification] ‘-->’ [multiplicity] class' ‘:’ [visibility] variable 
``` 

Where `class` is the identifier of the associating object, `class'` is the identifier of the associated object and `variable` is the identifier of the instance variable that is defined by the association. 


``` 

	qualification = general map type
		       | injective map type

	general map type = ‘"[’ type ‘]"’ 
	injective map type = ‘"[(’ type ‘)]"’ 


	multiplicity = set type
		     | seq type

	set type = set0 type
		 | set1 type

		set0 type = "*"
		set1 type = "1..*"

	seq type = seq0 type
		 | seq1 type

		set0 type = "(*)"
		set1 type = "(1..*)"
    
``` 



## Non bi-directional mapping: VDM2UML
This section describes cases where information is lost when translating from VDM to PlantUML.

### VDM2UML Structure Abstraction
To avoid excessive information in the class diagrams certain VDM structures can be abstracted away.

This effects how compound types are represented in UML and can prevent class diagrams from becoming cluttered and verbose.
The tradeoff is that the translation is no longer bi-directional, since information about types may be lost.


The VDM structure abstraction splits the VDM compound types into two groups. The two groupls are low capacity compound types, $C_{L}$ and high capacity compound types, $C_{h}$. 




capacity is a whole number.





## Non bi-directional mapping: UML2VDM
This section describes cases where information is lost when translating from PlantUML to VDM.

in keyword
type signifiers are optional
no vissibility on associations
