#README NOTES----------------

TODO check if structure signifier abstraction is correct
The Object Reference type?
function types?


## VDM2UML

### VDM Structure Abstraction

To avoid excessive information in the class diagram certain VDM structures are abstracted.

#### Set and Sequence Types

set of set of ClassA (defined in ClassA, no association)

map set of set of alarm to set of set of expert

#### Map Types


#### Other Compound types
The compound types product, composite, union, optional and product types are abstracted in their UML representation, 
by omiting the subtypes of the compound type in the UML model and instead showing the types using VDM2UML type signifiers.

The Type signifiers for the mentioned compound types are:

Optinal type: []
Composite type: ::
Union type: |
Product type: *


For union and product types, the number of subtypes determine the number of signifier tokens.

Examples: 


----------UML2VDM----------
in keyword


