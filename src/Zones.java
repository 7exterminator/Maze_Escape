/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);
        int[] stack = new int[n];

        // pass 1: DFS on the slides, cells are written in the order they are finished
        boolean[] seen = new boolean[n];
        int[] next = new int[n];
        int[] finished = new int[n];
        int done = 0;
        for (int root = 0; root < n; root++) {
            if (seen[root]) continue;
            int top = 0;
            stack[top++] = root;
            seen[root] = true;
            while (top > 0) {
                int u = stack[top - 1];
                if (next[u] < out[u].length) {
                    int v = s.to()[out[u][next[u]++]];
                    if (!seen[v]) { seen[v] = true; stack[top++] = v; }
                } else {
                    top--;
                    finished[done++] = u;
                }
            }
        }

        // pass 2: from the last finished cell to the first, DFS on the reversed slides
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = finished[i];
            if (zoneOf[root] != -1) continue;
            int top = 0;
            stack[top++] = root;
            zoneOf[root] = zones;
            while (top > 0) {
                int u = stack[--top];
                for (int slide : in[u]) {
                    int v = s.from()[slide];
                    if (zoneOf[v] == -1) { zoneOf[v] = zones; stack[top++] = v; }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int m = s.count();

        // the graph of zones: the slides that join two different zones, grouped by the zone they start in
        int[] first = new int[zoneCount + 1];
        for (int i = 0; i < m; i++) {
            int x = zoneOf[s.from()[i]], y = zoneOf[s.to()[i]];
            if (x != y) first[x + 1]++;
        }
        for (int z = 0; z < zoneCount; z++) first[z + 1] += first[z];
        int[] fill = java.util.Arrays.copyOf(first, zoneCount);
        int[] edges = new int[first[zoneCount]];
        for (int i = 0; i < m; i++) {
            int x = zoneOf[s.from()[i]], y = zoneOf[s.to()[i]];
            if (x != y) edges[fill[x]++] = i;
        }

        // zone numbers are in topological order: when we reach zone z, all slides into z are already used
        final long unreachable = Long.MAX_VALUE;
        long[] cost = new long[zoneCount];
        java.util.Arrays.fill(cost, unreachable);
        cost[zoneOf[0]] = 0;
        int[] chain = new int[zoneCount];
        java.util.Arrays.fill(chain, 1);
        long stages = 0;
        for (int z = 0; z < zoneCount; z++) {
            stages = Math.max(stages, chain[z]);
            for (int e = first[z]; e < first[z + 1]; e++) {
                int slide = edges[e];
                int y = zoneOf[s.to()[slide]];
                chain[y] = Math.max(chain[y], chain[z] + 1);
                if (cost[z] != unreachable) cost[y] = Math.min(cost[y], cost[z] + s.time()[slide]);
            }
        }
        return new long[]{stages, cost[zoneOf[s.n() - 1]]};
    }
}
