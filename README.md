# Transformations Between VDM and UML

## Contents


## Title 1

## Basic Data Types
Basic data types are included in PlantUML as the type of a class attribute.


## Compound Types

### multiplicity types
Non associative multiplicity types are included as the type of a class attribute.




#### set:
non associative: set of element
associative: class -> "*" object

#### seq:
non associative: seq of element
associative: class -> "(*)" object

#### seq1:
non associative: seq1 of element
associative: class "1" -> "(*)" object

### qualified association types

#### map:
non associative: "mapName" map qualifier to multiplicity element
associative: class "[qualifier]" -> multiplicity object

#### inmap:
non associative: "mapName" map qualifier to multiplicity element
associative: class "[(qualifier)]" -> multiplicity object

### Other compound types


#### optinal type: "[]"
#### composite type: "::" 
#### union type: "|"
#### product type: "*"


## TODOs

- Fix declated types showing up as the "type of the type" instead of the name of type when used with an instance variable. Eg. Qualification is defined as a union, reqQuali is the type "Qualification", but shows up as just being a union.
