
# Support for PlantUML Visualizations of VDM Models 
The VDM-PlantUML plugin is integrated into the VDM-VSCode extension, which provides VDM language support for Visual Studio Code (VS Code). 
The plugin enables bi-directional translations between VDM models and the textually based diagram tool, [PlantUML](https://plantuml.com/). 
The object-oriented structure of the VDM models are represented in UML as [PlantUML class diagrams](https://plantuml.com/class-diagram).

For information about using the plugin on VS Code see the [VDM-VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#Translate-to-UML).

## Bi-directional Mapping:
The following section describes the common form between PlantUML class diagrams and VDM models. This common form is defined by defining UML constructs using VDM components.  

Before defining UML constructs like attributes, operations and associations, the way in which VDM types are treated in PlantUML will be described.  

### Basic Data Types
There is a one-to-one mapping of basic VDM data types between VDM and PlantUML, 
with the VDM symbol for the type being represented as the type in a UML element.


### Set, Sequence and Map Types
Non-associative, set, seq and map types are directily translated between PlantUML and VDM, with the VDM set, seq or map type being the type of the corrosponding UML attribute. 

Set, Sequence and Map Types are the only types that can be associative. 


If the type of the elements in a set, sequence or map is a Class, the compound type is considered to be associative and shown in the UML model as an [association](https://github.com/jolnd/vdm-plantuml-plugin#association-definition). 
If the type of the elements is any other type, including another compound type that refer to a class, the set is considered to be non-associative and shown in the UML model as an [attribute](https://github.com/jolnd/vdm-plantuml-plugin#attribute-definition).


### Class Declarations:
There is a one-to-one relationship between classes in UML and classes in VDM++/VDM-RT 
Attributes and operations in PlantUML are defined within class declarations. 


### Attribute Definition:


```
Syntax: attribute definition = [visibility] identifier ‘:’ type [attribute stereotype]

	attribute stereotype = ‘<<type>>’
			     | ‘<<value>>’	
```
The attribute stereotype is used to differentiate between types, values and instance variables. If no stereotype is used, the attribute is considered an instance variable.  

### Operation Definition:
```
Syntax: operation definition = [visibility] identifier ‘(’ [type] ‘)’ ‘:’ type' [operation stereotype]

``` 
Where `type` is the discretionary type which the operations take as argument, and `type'` is the discretionary return type of the operation.   
```
	operation stereotype = ‘<<function>>’
```
The operation stereotype is used to differentiate between functions and operations. If no stereotype is used, the algorithm is considered an operation.


### Association Definition:
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

### Visibility
Visibility is known as `access` in the object-oriented dialects of VDM. 
It is defined as:

``` 
visibility = ‘+’
	   | ‘-’
	   | ‘#’    
``` 
For public, private and protected, respectively. The default visibility to any component is private.


## Non bi-directional mapping: VDM2UML
Information is lost when translating between VDM and PlantUML

#### VDM2UML Structure Abstraction
To avoid excessive information in the class diagram certain VDM structures are abstracted away.






## Non bi-directional mapping: UML2VDM
in keyword
type signifiers are optional
no vissibility on associations
