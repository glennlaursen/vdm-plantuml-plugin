# Support for PlantUML Visualizations of VDM Models 
The VDM-PlantUML plugin is integrated into the VDM-VSCode extension, which provides VDM language support for Visual Studio Code (VS Code). 
The plugin enables bi-directional translations between VDM models and the textually based diagram tool, [PlantUML](https://plantuml.com/). 
The object-oriented (OO) structure of the VDM models are represented in UML as [PlantUML class diagrams](https://plantuml.com/class-diagram).

For information about using the plugin on VS Code see the [VDM-VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#Translate-to-UML).

## Bi-directional Mapping:
The following section describes the common form between PlantUML class diagrams and VDM models. This common form is defined by defining UML constructs using VDM components.  

Before defining UML constructs like attributes, operations and associations, the way VDM types are treated in PlantUML will be described.  

### Basic Data Types
There is a one-to-one mapping of basic VDM data types between VDM and PlantUML, with the VDM symbol for the type being represented as the type in a UML element.


### Set, Sequence and Map Types
Non-associative, set, seq and map types are directly translated between PlantUML and VDM, with the VDM set, seq or map type being the type of the corresponding UML attribute. 

If the type of the elements in a set, sequence or map is a class, the type is associative and shown in the UML model as an [association](https://github.com/jolnd/vdm-plantuml-plugin#association-definition). If the type of the elements is any other type, including another compound type that refers to a class, the type is non-associative and shown in the UML model as an [attribute](https://github.com/jolnd/vdm-plantuml-plugin#attribute-definition).

### Access specifiers
Access specifiers correspond directly to element visibility in UML.

Visibility is defined as:

``` 
visibility = ‘+’
	   | ‘-’
	   | ‘#’    
``` 
For public, private and protected, respectively. The default visibility to any component is private.


### Class Declarations:
There is a one-to-one relationship between classes in UML and classes in VDM++/VDM-RT. 
Attributes and operations in PlantUML are defined within class declarations. 

```
Syntax: class = ‘class’ identifier [class body] 

		class body = ‘{’ [definition block] ‘}’
			
			definition block = attribute definitions
					 | operation definitions		   
```

### Attribute Definition:
UML attributes occur when translating a VDM instance variable, type, or value to UML.

```
Syntax: attribute definition = [visibility] identifier ‘:’ type [attribute stereotype]

	attribute stereotype = ‘<<type>>’
			     | ‘<<value>>’	
```
The attribute stereotype is used to differentiate between types, values, and instance variables. If no stereotype is used, the attribute is considered an instance variable.  

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
UML associations occur when the type of a VDM instance variable is a class or when the instance variable is a set, sequence or map type with a class as its subtype. When deciding if a map type from a type A to a type B is an association, type B is considered. Associations in PlantUML are defined after [class declarations](https://github.com/jolnd/vdm-plantuml-plugin#class-declarations)

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
		 

		set0 type = ‘"*"’
		set1 type = ‘"1..*"’

	seq type = seq0 type
		 | seq1 type

		set0 type = ‘"(*)"’
		set1 type = ‘"(1..*)"’
    
``` 



## Non-bi-directional mapping: VDM2UML
This section describes cases where information is lost when translating from VDM to PlantUML.


### VDM2UML Type Abstraction - Not yet implemented
This feature is not yet implemented.

The VDM2UML type abstraction effects how compound types are represented in UML and can prevent class diagrams from becoming cluttered and verbose.
The tradeoff is that the translation is no longer bi-directional since information about types may be lost. This is an optional feature, enabled by default. To see how to turn abstraction off, see the [translate to UML section of the VDM VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#translate-to-uml) 

The VDM type abstraction splits VDM compound types into two groups. The groups are the primary compound types, $C_{0}$ and the secondary compound types, $C_{1}$. 

$C_{0} = set, seq, map, optional$

$C_{1} = product, union$. 

Each group has a different capacity determined by $\gamma_{0}$, $\gamma_{1} \in Z*$, for $C_{0}$, $C_{1}$ respectively.

The capacity determines how many compound types any given type can compose, before it is deemed too complicated for UML and therefore in need of abstraction. 

A compound type with multiple compound types within it, will belong to the group of the outer compound type. All non-basic types in the inner type count towards the capacity. If the capacity is reached, abstraction will be done in accordance to which group the type belongs to.    


```
abstraction = C_0 abstraction 
	    | C_1 abstraction

	C_0 abstraction = ‘seq of’ type_a
			| ‘set of’ type_a
			| ‘[’ type_a ‘]’ 
			| ‘map’ type_a | basic type ‘to’ type_a | basic type

		type_a = c_0'
		       | c_1'

			c_0' = set...
			     | seq...
			     | [...]

			c_1' = ‘*’ {‘*’}
			     | ‘|’ {‘|’}

	C_1 abstraction = c_1'
```
The capacity for a map type is $2\gamma_{0}$, since a map has a minimum of two subtypes. This is also why a map type can have a basic type as one of its subtypes and still be abstracted, if the other subtype consists of enough compound sub-types to exceed the capacity. 

For $c_1'$, the number of symbols used is given by $n-1$ where n is the number of subtypes in the non-abstracted compound type. 

#### Examples: 
Let $\gamma_{0} = 1$, $\gamma_{1} = 3$

| Original Type | Capacity Used | Abstraction |
| ----------- | ----------- | ----------- |
| nat \| nat \| nat \| nat| 3 |  Not abstracted|
| bool * seq of map nat to nat | 3 | Not abstracted|
| map set of char to token * bool| 2 | Not abstracted|
| set of seq of char| 1 | Not abstracted|
| nat \| seq of set of char \| nat | 4 | \|\| |
| set of seq of char * bool | 2 | set of seq...|
| set of bool * nat * token | 2 | set of **|
| [(char * nat) \| (seq of nat)] | 4 | [ \| ] |
| map seq of (char * nat) to set of nat | 3 | map seq... to set...|
| map (set of nat \| char) to (bool * bool)| 3 | map set... to * |
| map set of (nat \| char) to [nat]| | map set... to [...] |



## Vdm-PlantUML Cheatsheet 

| Component | VDM | PlantUML |
| ----------- | ----------- | ----------- |
| Class Declarations| Class Object1 ... End Object1 | class Object1{ ... } |
| Instance Variable Definitions| instance variables <br /> var1 : Type; | var1 : Type |
| Value Definitions| values <br /> val1 : Type = value1 | val1 : Type «value» |
| Type Definitions| types <br /> type1 = Type | type1 : Type «type»  |
| Operation Definitions| operations <br /> op1 : Type ==> Type; <br /> op1() == ( ... ); | op1() : Type |
| Function Definitions| functions <br /> func1 : Type ==> Type; <br /> func1() == ( ... ); | func1() : Type «function» |
| Associations | class Object1 <br /> ... <br /> instance variables <br /> asoc1 : Object2;| Object1 --> Object2 : asoc1 |
| Associative set | collection1 : set of Object2 | Object1 --> "*" Object2 : collection1 |
| Associative set1 | collection1 : set1 of Object2 | Object1 --> "1..*" Object2 : collection1 | 
| Associative seq | collection1 : seq of Object2 | Object1 --> "(*)" Object2 : collection1 |
| Associative seq1 | collection1 : seq1 of Object2 | Object1 --> "(1..*)" Object2 : collection1 |
| Associative map | quali1 : map Type to Object2; | Object1 "[Type]" -> Object2 : quali1 |
| Associative inmap | quali1 : inmap Type to Object2; | Object1 "[(Type)]" -> Object2 : quali1 |
| Static Keyword (not implemented) | static member1 ... | {static} : member1 |
| Visibility | private member1 <br /> protected member2 <br /> public member3 | - member1 <br /> # member2 <br /> + member3 |
| In Keyword| operations <br /> op1 : type1 * type2 ==> Type; <br /> op1(in t1: type1, in t2: type2) == ( ... ); | op1(in t1: type1, in t2: type2) : Type |

