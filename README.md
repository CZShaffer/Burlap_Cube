#Background on the Domain Representation:
This domain represents a 3x3 rubik's cube object as a 2D array 
of size 6 by 9, 6 faces of 9 elements each. That being said, 
the 5th element (index 4) of each face will never change and is
for comparisons only. The index of each piece is modeled as per
the diagram below:


              0 1 2
              3 4 5 
              6 7 8
        0 1 2 0 1 2 0 1 2 0 1 2
        3 4 5 3 4 5 3 4 5 3 4 5 
        6 7 8 6 7 8 6 7 8 6 7 8	
              0 1 2
              3 4 5 
              6 7 8
      
The faces of the cube are, going in order from top to bottom
and left to right: (Top/White), (Left/Blue), (Front, Red),
(Right,Green), (Back, Orange), (Down, Yellow).

For each face there are 3 actions, corresponding to a clockwise
turn (such as U), a counterclockwise turn (U_Prime) and a turn 
equivalent to two turns in a single direction (U2).

The cube is considered solved when all of the elements on each 
face have the same value as the 5th element (index 4) of that 
face.

---------------------------------------------------------------

#Running a demo:
Simply running the CubeLearning.java file will solve a depth 4 
cube using IDA*. However, if you wish to try other depths or 
other cubes of the same depth, you will need to modify line 49
of the CubeLearning.java file, copied below.

initialState = CubeDomain.getExampleState(domain,0,4);

The first integer represents the seed, and it will initialize 
a random cube from that seed. Changing this number will give a
different cube of the same depth provided the second integer 
remains the same.

The second integer represents the depth. I don't recommend 
changing it for this demo, as I can't efficiently solve 
anything beyond depth 4, but if you would like to test it for 
lower depths, then then this integer corresponds to how many 
turns away from a solved cube the initialized cube should be.
