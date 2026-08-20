---
title: Water Pump
entryId: stellaris:machines
iconType: item
associatedBlocks: [stellaris:water_pump]
---

The Water Pump draws water from the world and feeds it into your fluid network.

[item=stellaris:water_pump scale=5]

**How to use :** 
Place the pump **directly on top of a water source block**, and power it. Nothing else works : a water block next to the pump or one block lower will be ignored. 

Each cycle it swallows the source block below it and stores 1 000 mB of water, then pushes that water out of its **top** face. Connect a [ref=stellaris:machines/pipes]Pipe[ref] above it to carry the water to an [ref=stellaris:machines/electrolyzer]Electrolyzer[ref] or a [ref=stellaris:machines/fluid_tank]Fluid Tank[ref]. 

Because the source block is consumed, build the pump over an infinite water source so the water flows back and the pump never runs dry. 



**Stats :** 
- Power Consumption : 100 FE per 1 000 mB pumped 
- Tank Capacity : 1 000 mB 
- Internal Buffer : 100 FE
