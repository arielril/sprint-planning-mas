// Agent sample_agent in project sprint_planning

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world from the sample one.").

{ include("$jacamoJar/templates/common-cartago.asl") }
