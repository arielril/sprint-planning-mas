// Agent sample_agent in project sprint_planning

/* Initial beliefs and rules */

/* Initial goals */

!hello.

/* Plans */

+!hello : true <- .print("hello world from the sample one.").

{ include("$jacamoJar/templates/common-cartago.asl") }
