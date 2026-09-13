---
title: Energy
entryId: stellaris:machines
iconType: item
associatedBlocks: [stellaris:cable_t1]
---

Every machine in Stellaris runs on the same thing : energy, counted in FE. This page explains how it moves around your base. The machines themselves each have their own page.

[image=stellaris:textures/wiki/rendered_blocks/power_bank width=96 height=96]



**Everything has a buffer** 

A machine is not wired to a generator the way a lamp is wired to a socket. Every machine, every generator and every [ref=stellaris:machines/power_bank]Power Bank[ref] holds its own private pool of energy, and all a [ref=stellaris:machines/cables]Cable[ref] does is move energy from a fuller buffer to an emptier one.

That has one consequence worth remembering : a machine keeps running for a while after you cut its power, because it is spending what it had already stored. A [ref=stellaris:machines/oxygen]Oxygen Distributor[ref] sitting on a full 12 800 FE buffer will keep a room breathable long after the generator has gone out.



**Making it** 

Four generators, in rough order of how much trouble they are : 
- [ref=stellaris:machines/solar_panel]Solar Panel[ref] : 1 FE/t, and only while the sun is on it. 
- [ref=stellaris:machines/star_light_panel]Star Light Panel[ref] : 1 FE/t, the version that works out in space. 
- [ref=stellaris:machines/coal_generator]Coal Generator[ref] : 3 FE/t, burning coal. 
- [ref=stellaris:machines/diesel_generator]Diesel Generator[ref] : 5 FE/t, burning [ref=stellaris:items/diesel]Diesel[ref], at 25 FE per mB. 

None of these are large numbers. A single diesel generator at 5 FE/t is enough to hold up a distributor, a propagator and a light or two, and not much more. Bases grow by adding generators, not by finding a bigger one.



**Moving it** 

Cables carry 20 FE/t and nothing else — they will not move fluids, which is what [ref=stellaris:machines/pipes]Pipes[ref] are for. One cable run can therefore feed four diesel generators' worth of output before the cable itself becomes the limit.



**Storing it** 

A Power Bank is a buffer with nothing attached : it takes in surplus and gives it back when the generators cannot keep up. Tier 1 holds 16 000 FE.

It is also where you charge equipment. Put a Space Suit helmet in its output slot to fill the battery that the Night Vision and Oil Finder modules run from. The Solar and Star Light Panels will charge an item directly too, at 10 FE/t, which is faster than they generate.



*Fun fact*: a Power Bank keeps its charge when you break it, so you can carry a full one to a site that has no generators yet.

[item=stellaris:cable_t1 onlyIcon]
