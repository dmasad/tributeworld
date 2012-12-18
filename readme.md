TributeWorld
============

An implementation and expansion of the Tribute Model presented in [Axelrod, "Building New Political Actors, 1995](http://www-personal.umich.edu/~axe/research/Building.pdf).

## Overview

There is a world with a set of political actors, in some spatial arrangement (traditionally just a one-dimensional space). Each tick, some of these actors are activated, and can choose to demand tribute from one of their neighbors. The neighbor then chooses whether to pay tribute, or fight. 

As actors pay tribute or fight, their loyalty to one another changes: tribute payment increases loyalty, as does fighting on the same side of a war; fighting onopposite sides reduces it. Actors will join wars if they are adjascent to them, and loyal to one side over another.

## Files
The model uses the [MASON](http://cs.gmu.edu/~eclab/projects/mason/) simulation framework. The specific files are:

* **Actor.java:** The code for the basic Actor agent.
* **MobileActor.java:** An extension of agent, with the added option of migrating in response to threats or if there are no neighbors.
* **Coalition.java:** The code for assembling a coalition, an ad-hoc aggregate agent used for war.
* **TributeWorld.java:** The overall model manager class.
* **TributeWorldWithUI.java:** The visualization for the model.
* **DataCollection.java:** Class for collecting data from the simulation, to feed into the visualization.
* **Scenario.java:** The base scenario class; all scenarios derive from it.
    * **Scenario0.java:** The original Axelrod model.
    * **Scenario1.java:** The Axelrod model extended into a 2D grid
    * **Scenario2.java:** The 2D model with heterogenous resources spread across the grid
    * **Scenario3.java:** The heterogenous resource model, with added migration behavior
* **ParameterSweep.java:** The code to perform a parameter sweep over the model and store the results. Modified frequently to perform different experiments.

### Analysis:

* **OutputAnalysis.py:** Python code to classify the polarity of a commitment graph.
* *.pynb files:* Several IPython Notebooks used in data investigation. Also edited frequently.



