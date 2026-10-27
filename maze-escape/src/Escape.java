/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int exit = n - 1;
        int[][] open = Graphs.open(w, broken);

        // door[4 * cell + d]: the cell behind the door in direction d (0 up, 1 right, 2 down, 3 left), or -1
        int[] door = new int[4 * n];
        java.util.Arrays.fill(door, -1);
        int deadEnds = 0;
        for (int u = 0; u < n; u++) {
            if (open[u].length == 1) deadEnds++;
            for (int v : open[u]) door[4 * u + direction(u, v, cols)] = v;
        }

        // DFS with our own stack: no recursion, so a huge maze cannot overflow the call stack
        boolean[] seen = new boolean[n];
        int[] parent = new int[n];
        int[] tried = new int[n];
        int[] stack = new int[n];
        int top = 0, visited = 1;
        stack[top++] = 0;
        seen[0] = true;
        boolean found = exit == 0;
        while (top > 0 && !found) {
            int u = stack[top - 1];
            if (tried[u] == 4) { top--; continue; }
            int v = door[4 * u + tried[u]++];
            if (v < 0 || seen[v]) continue;
            seen[v] = true;
            visited++;
            parent[v] = u;
            if (v == exit) found = true;
            else stack[top++] = v;
        }

        int[] path = new int[0];
        if (found) {
            int length = 1;
            for (int v = exit; v != 0; v = parent[v]) length++;
            path = new int[length];
            int v = exit;
            for (int i = length - 1; i >= 0; i--) { path[i] = v; v = parent[v]; }
        }
        return new Result(path, visited, deadEnds);
    }

    /** Direction from cell u to its neighbour v: 0 up, 1 right, 2 down, 3 left. */
    private static int direction(int u, int v, int cols) {
        int d = v - u;
        if (d == -cols) return 0;
        if (d == cols) return 2;
        return d == 1 ? 1 : 3;
    }
}
