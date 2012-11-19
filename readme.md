TributeWorld
============

An implementation and expansion of the Tribute Model presented in [Axelrod, "Building New Political Actors, 1995](http://www-personal.umich.edu/~axe/research/Building.pdf).

## Overview

There is a world with a set of political actors, in some spatial arrangement (traditionally just a one-dimensional space). Each tick, some of these actors are activated, and can choose to demand tribute from one of their neighbors. The neighbor then chooses whether to pay tribute, or fight. 

As actors pay tribute or fight, their loyalty to one another changes: tribute payment increases loyalty, as does fighting on the same side of a war; fighting onopposite sides reduces it. Actors will join wars if they are adjascent to them, and loyal to one side over another.

## Files
The model uses the [MASON](http://cs.gmu.edu/~eclab/projects/mason/) simulation framework. The specific files are:

* **Actor.java:** The code for the basic Actor agent.
* **Coalition.java:** The code for assembling a coalition, an ad-hoc aggregate agent used for war.
* **TributeWorld.java:** The overall model manager class.
* **TributeWorldWithUI.java:** The visualization for the model.
* **DataCollection.java:** Class for collecting data from the simulation, to feed into the visualization.

