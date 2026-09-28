# Stellaris 2.0.5 for MC 26.1.2

## Changes
- Show generator energy production per tick in the tooltip
- Show machine energy consumption per tick in the tooltip
- Leaving a rocket during the countdown cancels the launch and refunds the fuel
- Players can no longer leave a rocket or a lander during flight
- Lander crash explosion radius now increase based on impact speed
- Bump minimum Architectury API version to 20.1.16
- Improve the antenna tooltip
- New pumpjack model and animation
- Improve titanium door

## Fixes
- Fix jetpack and lander controls ignoring key remapping
- Fix a small memory leak
- Fix torches not being consumed when placed without oxygen
- Fix cable energy distribution with low energy
- Fix space station option only showing when the blueprint is in the 1st rocket slot
- Fix rocket flying forever if player leaves a rocket during flight
- Fix players getting stuck on death screen after a lander crash
- Fix antennas deleting the block under the launch pad instead of dropping it
- Fix space farm destroying its content when a player try to change it
- Fix space farm not dropping its content when broken
- Fix bone meal being used even if the crops growing is done
- Fix cargo unloader deleting items when full
- Fix cargo unloader emptying landers that aren't landed
- Fix flag loot table
- Fix oil finder not showing the no energy message
- Fix vaccine not being consumed when used
- Fix fuel refinery output tanks never filling completely
- Fix electrolyzer doesn't stop before getting full (which can cause it to consume to much water)