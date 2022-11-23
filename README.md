# Support for PlantUML Visualizations of VDM Models 
The VDM-PlantUML plugin is integrated into the VDM-VSCode extension, which provides VDM language support for Visual Studio Code (VS Code). 
The plugin enables bi-directional translations between VDM models and the textually based diagram tool, [PlantUML](https://plantuml.com/). 
The object-oriented (OO) structure of the VDM models are represented in UML as [PlantUML class diagrams](https://plantuml.com/class-diagram).

For information about using the plugin on VS Code see the [VDM-VSCode wiki](https://github.com/overturetool/vdm-vscode/wiki/Translation#Translate-to-UML), as well as the wiki for [this plugin](https://github.com/jolnd/vdm-plantuml-plugin/wiki).

## Vdm-PlantUML Cheatsheet 

| Component | VDM | PlantUML |
| ----------- | ----------- | ----------- |
| Class Declarations| Class A ... End A | class A{ ... } |
| Instance Variable Definitions| instance variables <br /> var1 : Type; | var1 : Type |
| Value Definitions| values <br /> val1 : Type = value1 | val1 : Type «value» |
| Type Definitions| types <br /> type1 = Type | type1 : Type «type»  |
| Operation Definitions| operations <br /> op1 : Type ==> Type; <br /> op1() == ( ... ); | op1() : Type |
| Function Definitions| functions <br /> func1 : Type ==> Type; <br /> func1() == ( ... ); | func1() : Type «function» |
| Associations | class A <br /> ... <br /> instance variables <br /> asoc1 : B;| A --> B : asoc1 |
| Associative set | asoc1 : set of B | A --> "0..*" B : asoc1 |
| Associative set1 | asoc1 : set1 of B | A --> "1..*" B : asoc1 | 
| Associative seq | asoc1 : seq of B | A --> "(0..*)" B : asoc1 |
| Associative seq1 | asoc1 : seq1 of B | A --> "(1..*)" B : asoc1 |
| Associative map | asoc1 : map Type to B; | A "[Type]" --> B : asoc1 |
| Associative inmap | asoc1 : inmap Type to B; | A "[(Type)]" --> B : asoc1 |
| Static Keyword (not implemented) | static member1 ... | {static} : member1 |
| Visibility | private member1 <br /> protected member2 <br /> public member3 | - member1 <br /> # member2 <br /> + member3 |
| In Keyword| operations <br /> op1 : type1 * type2 ==> Type; <br /> op1(in t1: type1, in t2: type2) == ( ... ); | op1(in t1: type1, in t2: type2) : Type |
