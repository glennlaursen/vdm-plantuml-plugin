
# Support for PlantUML Visualizations of VDM Models 

The VDM-PlantUML plugin is integrated into VDM-VSCode, which is an extension that provides VDM language support for Visual Studio Code (VS Code). 
The plugin enables bi-directional translations between VDM models and the textually based diagram tool, [PlantUML](https://plantuml.com/). 
The object-oriented structure of the VDM models are represented in UML as [PlantUML class diagrams](https://plantuml.com/class-diagram).

For information about using the plugin on VS Code see the [VDM-VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#Translate-to-UML).

## Bi-directional Mapping:

The following section describes the common form between PlantUML class diagrams and VDM models. This common form is defined by defining UML constructs using VDM components.  

The bi-directional mapping assumes that the type of the VDM component is a basic type or a combination of compound type with a maximum depth of 5 (todo: ?), otherwise information will be abstracted, and the translation is no longer bi-directional, see [VDM Structure Abstraction](https://github.com/jolnd/vdm-plantuml-plugin/edit/main/README.md#vdm-structure-abstraction) for more information. 


### Basic Data Types

There is a one-to-one mapping of basic VDM data types between VDM and PlantUML, 
with the VDM symbol for the construct being represented as the type in a UML attribute or operation.
Basic data types are therefore always bi-directional.

### Non-Associative Compound Types
If the type of the elements in a set is a Class, the set is considered to be associative and shown in the UML model as an association. 
If the type of the elements in a set is any other type, including a compound type that refer to a class, the set is considered to be non-associative and shown in the UML model as an attribute.


#### Set and Sequence Types




#### Map Types



### Visibility
Visibility is known as `access` in the object-oriented dialects of VDM. 
It is defined as:

``` 
visibility = ‘+’
	   | ‘-’
	   | ‘#’    
``` 
For public, private and protected, respectively. The default visibility to any component is private.

### Attribute, Operation and Association definitions
The mapping of attributes, operations and association are bi-directional, even though the types of the components may be subject to [VDM structure abstraction](https://github.com/jolnd/vdm-plantuml-plugin/edit/main/README.md#vdm-structure-abstraction).


#### Attribute Definition:

```
Syntax: attribute definition = [visibility] identifier ‘:’ type [attribute stereotype]

	attribute stereotype = ‘<<type>>’
			     | ‘<<value>>’	
```
The attribute stereotype is used to differentiate between types, values and instance variables. If no stereotype is used, the attribute is considered an instance variable.  

#### Operation Definition:
```
Syntax: operation definition = [visibility] identifier ‘(’ [type] ‘)’ ‘:’ type' [operation stereotype]

``` 
Where `type` is the discretionary type which the operations take as argument, and `type'` is the discretionary return type of the operation.   
```
	operation stereotype = ‘<<function>>’
```
The operation stereotype is used to differentiate between functions and operations. If no stereotype is used, the algorithm is considered an operation.


#### Association Definition:
	
```
Syntax:	association definition = class [qualification] ‘-->’ [multiplicity] class' ‘:’ ‘-’ variable 
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

Information is lost when translating between VDM and PlantUML

#### VDM2UML Structure Abstraction

To avoid excessive information in the class diagram certain VDM structures are abstracted away.

##### Set and Sequence Types

set of set of ClassA (defined in ClassA, no association)

map set of set of alarm to set of set of expert

##### Map Types


##### Other Compound types
The product, composite, union, optional and product compound types are abstracted in their UML representation, 
by omiting the subtypes of the compound type in the UML model and instead showing the types using VDM2UML type signifiers.


VDM2UML type signifiers are an abstract representation of VDM types. 
The type signifiers consists type tokens which are special strings of characters 
that denote the type of the type signifier. In the case of union and product types, 
the number of subtypes determine the number of type tokens used in the type signifier,
with N subtypes leading to N-1 type tokens in the UMl model.


The VDM2UML type signifiers are as follows: 

Optimal type: "[]"
Composite type: "::"
Union type: "|"
Product type: "*"

If the number type tokens needed to create a type signifier exceeds 5, 
the signifier is instead a single type token followed by three dots.
The value of the subtypes is not considered, only top level compound type is represented in the UML model.


Examples: 

VDM							UML

expr = [char] | nat | seq of char * bool;		-Expr0 : ||

var : nat * nat * nat * nat * nat * nat * nat;		-Expr1 : *...




## Non bi-directional mapping: UML2VDM


in keyword
type signifiers are optional

