---
title: Oxygen Machines
entryId: stellaris:machines
iconType: item
associatedBlocks: [stellaris:oxygen_propagator,stellaris:oxygen_distributor]
---

The Oxygen System is the most important system in the universe. Off Earth, an area without oxygen deals 0.5 damage every second to anything that breathes.

[image=stellaris:textures/wiki/rendered_blocks/oxygen_distributor width=96 height=96]

**Oxygen Distributor**

The oxygen distributor fills a sealed room with breathable air. Give it power and oxygen, either through a [ref=stellaris:machines/pipes]Pipe[ref] coming from an [ref=stellaris:machines/electrolyzer]Electrolyzer[ref] or directly with a [ref=stellaris:items/fluid_cell]Fluid Cell[ref] in its input slot. 

The room is filled by air spreading from the distributor through every block that is not a full solid block, and it must respect three rules : 
- The room must be **completely sealed from the sky**. If the air reaches a single spot that can see the sky, the whole room loses its oxygen. 
- The air stays inside the **3 x 3 chunks centred on the distributor**, over the full height of the world. Anything past that border is not filled. 
- A room can hold at most 50 000 blocks of air. 

Oxygen is only spent on breathing : 1 mB per living creature standing in the room, each update. An empty room costs nothing but the energy. A player wearing a full [ref=stellaris:items/space_suit]Space Suit[ref] with oxygen in the helmet breathes from the suit instead and costs the distributor nothing. 



Stats : 
- Energy Consumption : 1 FE per update, by default one update per second 
- Oxygen Consumption : 1 mB per breathing creature per update 
- Tank Capacity : 10 000 mB 
- Internal Buffer : 12 800 FE

[image=stellaris:textures/wiki/rendered_blocks/oxygen_propagator width=96 height=96]

**Oxygen Propagator**

The oxygen propagator extends the reach of a distributor. It needs energy, but no oxygen of its own. 

While powered, it adds its own 3 x 3 chunks to the area a distributor is allowed to fill, as long as it stands inside an area that is already allowed. Chain several of them to run air down a long corridor or across a big base. 



*Fun fact*: fire cannot burn without oxygen. A torch is fine, but flames go out instantly outside an oxygenated room, and normal crops refuse to grow or even to be planted. Use a [ref=stellaris:machines/space_farm]Space Farm[ref] to farm without air.

[item=stellaris:oxygen_distributor onlyIcon]
