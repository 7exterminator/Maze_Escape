/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet cells = new DisjointSet(w.cells());
        int[] broken = new int[w.cells() - 1];
        int count = 0;
        for (int k = 0; k < w.count(); k++) {
            if (cells.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int m = w.count();
        long[] keys = new long[m];                       // cost in the high bits, wall index in the low bits
        for (int k = 0; k < m; k++) keys[k] = ((long) w.cost()[k] << 32) | k;
        java.util.Arrays.sort(keys);

        DisjointSet cells = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int i = 0; i < m && count < broken.length; i++) {
            int k = (int) keys[i];
            if (cells.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), m = w.count();

        // for every cell: the walls around it, stored in one array
        int[] start = new int[n + 1];
        for (int k = 0; k < m; k++) { start[w.a()[k] + 1]++; start[w.b()[k] + 1]++; }
        for (int i = 0; i < n; i++) start[i + 1] += start[i];
        int[] fill = java.util.Arrays.copyOf(start, n);
        int[] around = new int[2 * m];
        for (int k = 0; k < m; k++) { around[fill[w.a()[k]]++] = k; around[fill[w.b()[k]]++] = k; }

        boolean[] inside = new boolean[n];
        java.util.PriorityQueue<Long> queue = new java.util.PriorityQueue<>();
        long total = 0;
        int taken = 1;
        inside[0] = true;
        for (int i = start[0]; i < start[1]; i++) { int k = around[i]; queue.add(((long) w.cost()[k] << 32) | k); }

        while (!queue.isEmpty() && taken < n) {
            int k = (int) (long) queue.poll();
            int next = inside[w.a()[k]] ? w.b()[k] : w.a()[k];
            if (inside[next]) continue;                  // both cells are already inside: the wall stays
            inside[next] = true;
            taken++;
            total += w.cost()[k];
            for (int i = start[next]; i < start[next + 1]; i++) {
                int j = around[i];
                int other = w.a()[j] == next ? w.b()[j] : w.a()[j];
                if (!inside[other]) queue.add(((long) w.cost()[j] << 32) | j);
            }
        }
        return total;
    }
}
