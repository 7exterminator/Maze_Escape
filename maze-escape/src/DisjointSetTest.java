/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        check(!d.union(2, 2), "union of a cell with itself returns false");
        check(d.count() == 3, "count does not change after a useless union");

        int n = 200000;
        DisjointSet chain = new DisjointSet(n);
        boolean allJoined = true;
        for (int i = 0; i + 1 < n; i++) allJoined &= chain.union(i, i + 1);
        check(allJoined, "a long chain: every union joins two different sets");
        check(chain.count() == 1, "a long chain ends in one set");
        check(chain.connected(0, n - 1), "the two ends of the chain are together");
        check(chain.find(0) == chain.find(n - 1), "both ends have the same root");

        DisjointSet halves = new DisjointSet(10);
        for (int i = 0; i < 4; i++) halves.union(i, i + 1);
        for (int i = 5; i < 9; i++) halves.union(i, i + 1);
        check(halves.count() == 2, "two groups of five after eight unions");
        check(!halves.connected(0, 9), "the two groups are apart");
        check(halves.union(4, 5) && halves.count() == 1, "one more union makes one set");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
