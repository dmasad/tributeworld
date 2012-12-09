'''
TributeWorld Analysis
'''

import json
import numpy as np
import networkx as nx


def get_power_configuration(graph, cutoff_factor=2):
    '''
    Evaluates a Commitment Matrix in terms of Wilkinson's Power Configuration scale.

    Args:
        graph: a graph based on the Axelrod commitment matrix
        cutoff_factor: the relative scale of each order of powers;
            e.g. a superpower is cutoff_factor greater than a great power.
            Defaults to 2.

    Returns:
        An integer, 0-6, as follows:

        6: universal state (one superpower, no great powers, no more than two local
            powers)
        5:  hegemony (either one superpower, no great powers, three or more
            local powers; or no superpowers, one great power, no more than one local
            power)
        4:  unipolar (all other configurations with one superpower)
        3:  bipolar (two great powers)
        2:  tripolar (three great powers)
        1:  multipolar (more than three great powers)
        0 : nonpolar (no great powers)

        [http://www.irows.ucr.edu/papers/irows14/irows14.htm]

        Non-powers, local power, great power, superpower.
        There may only be one superpower.

    '''

    # Figure out the number of superpowers, great powers and local powers:
    full_components = nx.connected_components(graph) # Pre-sorted
    components = [len(c) for c in full_components]
    
    superpower = []
    great_powers = []
    local_powers = []

    index = 0

    # Check to see if the greatest component is a superpower
    if components[0] >= (cutoff_factor * components[1]):
        superpower.append(components[0])
        index += 1
    try:
        # Great powers:
        if components[index] > 2:
            great_powers.append(components[index])
            index += 1
            while max(great_powers) <= (cutoff_factor * components[index]) and components[index] > 2:
                great_powers.append(components[index])
                index += 1

        # Local powers:
        if components[index] > 1:
            local_powers.append(components[index])
            index += 1
            while max(local_powers) <= (cutoff_factor * components[index]):
                local_powers.append(components[index])
                index += 1
    except:
        pass

    # Characterize:
    superpower = len(superpower)
    great_powers = len(great_powers)
    local_powers = len(local_powers)

    power_config = -1

    if superpower and not great_powers and local_powers <= 2:
        power_config = 6 # Universal state
    elif superpower and not great_powers and local_powers >= 3:
        power_config = 5 # Hegemony
    elif not superpower and great_powers==1 and local_powers <= 1:
        power_config = 5 # Hegemony
    elif superpower:
        power_config = 4 # Unipolar
    elif great_powers==2:
        power_config = 3 # Biploar
    elif great_powers == 3:
        power_config = 2 # Tripolar
    elif great_powers > 3:
        power_config = 1 # Multipolar
    elif not great_powers:
        power_config = 0 # Non-polar

    return power_config



