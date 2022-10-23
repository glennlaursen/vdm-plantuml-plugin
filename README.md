




## Bi-directional mapping:


### Basic Data Types

There is a one-to-one mapping of basic VDM data types between VDM and PlantUML, 
with the VDM symbol for the construct being represented as the type in a UML attribute or operation.


### Set and Sequence types

If the type of the elements in a set is a Class, the set is considered to be associative. 
If the type of the elements in a set is any other type, including a compound type that refer to a class, the set is considered to be non-associative.



#### Non-Associative sets and sequences:




#### Associative sets and sequences:
	
```
Syntax:    
     
    type = association type

    association type = class [qualificaition] ‘-->’ [multiplicity] class' ‘:’ ‘-’ variable    
``` 

Where `class` is the identifier of the associating object, `class'` is the identifier of the associated object and `variable` is the identifier of the instance variable that is defined by the association.


``` 

    qualificaition = general map type
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



### Non bi-directional mapping: VDM2UML

#### VDM Structure Abstraction

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

Optinal type: "[]"
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

