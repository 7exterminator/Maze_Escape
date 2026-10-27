# Assignment Maze Escape

Student ID: 253160
Name: Kozhabekov Arslan
Group: SE-2536
Maze size (rows x columns): 10 x 12
GitHub: https://github.com/7exterminator

This is the report.

Program output

    Broken: 119
    Kept: 99
    Last broken wall: 177
    MST cost: 37349
    Heaviest broken wall: 843
    Path length: 22
    DFS visited: 94
    Dead ends: 35
    Zones: 120
    Largest zone: 1
    Is DAG: 1
    Flow stages: 17
    Fastest route cost: 20

My maze:

java tools/Check.java`: DSU checks pass, all 3 examples (tiny, small, dag) are right.

Questions

0. What do find and union do, and what makes them fast in your code?

find goes up from a cell to the root of its set. On the way back it points every cell directly to the root (path compression). union finds both roots. If they are equal, it returns false. If not, the smaller tree goes under the larger one (union by size), the set count goes down by 1 and it returns true. Both tricks keep trees almost flat, so one operation is almost O(1).

1. Why is Broken always R*C - 1, and Kept always (R-1)*(C-1)?

At the start every cell is a separate set: R*C sets. A wall is broken only when union returns true, and each such union makes the number of sets smaller by 1. The maze grid is connected, so at the end there is 1 set. So R*C - 1 walls are broken. All walls: R*(C-1) + C*(R-1) = 2RC - R - C. Kept = 2RC - R - C - (RC - 1) = RC - R - C + 1 = (R-1)(C-1). My maze: 10*12 - 1 = 119 broken, 9*11 = 99 kept.

2. My MST cost from Kruskal and from Prim, and why they are equal:

Kruskal: 37349. Prim: 37349. Heaviest broken wall: 843. All wall costs are different, so the cheapest maze is only one. Both algorithms take only "safe" walls: the cheapest wall that joins two different parts is always in the cheapest maze. So both build the same maze and the same cost.

3. Why is there exactly one path between two cells? How did you avoid a stack overflow on a huge maze?

The broken walls are R*C - 1 edges on R*C cells, and they have no cycle (a wall is broken only if its cells are in different sets). It is a tree. In a tree two different paths between two cells would make a cycle, so there is exactly one path. DFS uses my own array as stack, no recursion. Every cell goes on the stack at most once, so an array of size n is enough. I tested a 1000 x 1000 maze with a very small thread stack: no overflow.

4. Why does the second pass of Kosaraju go on the reversed graph? Why do the slides have no cycle exactly when Zones equals the number of cells?

After pass 1 the last finished cell is in a zone that has no slides coming in from other zones. On the reversed graph, a DFS from this cell reaches only cells that can reach it. That is only its own zone. On the normal graph the DFS would run out into other zones. So every DFS of pass 2 gives exactly one zone, and zones come in topological order. About cycles: if there is a cycle, its cells reach each other and sit in one zone, so Zones < n. If Zones < n, some zone has 2 cells that reach each other, so there is a cycle. If Zones = n, every zone is one cell, and there is no cycle (no slide goes from a cell to itself).

5. Why is it enough to go through the zones in the order of their numbers? What does this pass cost?

The numbers are a topological order: every slide between two zones goes from a smaller number to a larger one. When I reach zone z, all zones with slides into z are already done. So the cost and the chain length of z are final. No priority queue needed. The pass looks at every zone and every slide once: O(n + m) (n cells, m slides). My data: 120 zones, 289 slides.

AI use

Tool: Claude I used it to review the code (DisjointSet, DisjointSetTest, Maze, Escape, Zones) and the text of this README.

Timing (7)

Milliseconds, average of 5 runs, from `java -cp out Bench`. Machine: Linux, 1 CPU (Intel Xeon 2.8 GHz), Java 21.

| maze | Kruskal | Prim |
|---|---|---|
| 100 x 100 | 16 | 15 |
| 300 x 300 | 56 | 99 |
| 1000 x 1000 | 360 | 1534 |

What do the numbers say about how the time grows?

The cells grow x9 and then x11. Kruskal time grows x3.5 and x6.4, Prim time grows x6.6 and x15.5: a bit more than linear, like n log n. Prim is slower on the big maze (4 times): the heap has many entries and every entry is a boxed number. Small sizes are also noisy: the first runs are slow because Java is warming up.

Complexity

n is the number of cells, m the number of walls (or slides).

| algorithm | time | space | assumptions |
|---|---|---|---|
| DSU find and union | O(alpha(n)) per operation, almost O(1) | O(n) | union by size and path compression |
| Kruskal | O(m log m) | O(n + m) | sort of the walls by cost is the main cost; DSU part is O(m alpha(n)) |
| Prim | O(m log m) | O(n + m) | binary heap (PriorityQueue); a wall goes in the queue at most once; walls stored in arrays per cell |
| DFS | O(n + m), here O(n) | O(n) | maze is a tree: m = n - 1; 4 doors per cell; own stack |
| Kosaraju | O(n + m) | O(n + m) | two DFS passes with own stacks; slide lists in both directions |
| Route in the zone graph | O(n + m) | O(n + m) | one pass in zone order, no priority queue; zone graph built with arrays |
