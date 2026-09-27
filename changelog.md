# Stellaris 2.0.5 for MC 26.1.2

## Changes
- Show generator energy production per tick in the tooltip
- Show machine energy consumption per tick in the tooltip
- Leaving a rocket during the countdown cancels the launch and refunds the fuel
- Players can no longer leave a rocket or a lander during flight
- Lander crash explosion radius now increase based on impact speed
- Bump minimum Architectury API version to 20.1.16
- Improve the antenna tooltip

## Fixes
- Fix jetpack and lander controls ignoring key remapping
- Fix a small memory leak
- Fix torches not being consumed when placed without oxygen
- Fix cable energy distribution with low energy
- Fix space station option only showing when the blueprint is in the 1st rocket slot
- Fix rocket flying forever if the player leaves the rocket during flight
- Fix players getting stuck on the death screen after a lander crash
- Fix antennas deleting the block under the launch pad instead of dropping it
- Fix the space farm destroying its content when a player try to change it
- Fix the space farm not dropping its content when broken
- Fix bone meal being used even if the crops growing is done