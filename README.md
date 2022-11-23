# Support for PlantUML Visualizations of VDM Models 
The VDM-PlantUML plugin is integrated into the VDM-VSCode extension, which provides VDM language support for Visual Studio Code (VS Code). 
The plugin enables bi-directional translations between VDM models and the textually based diagram tool, [PlantUML](https://plantuml.com/). 
The object-oriented (OO) structure of the VDM models are represented in UML as [PlantUML class diagrams](https://plantuml.com/class-diagram).

For information about using the plugin on VS Code see the [VDM-VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#Translate-to-UML).


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
| map set of (nat \| char) to [nat]| 3 | map set... to [...] |



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

