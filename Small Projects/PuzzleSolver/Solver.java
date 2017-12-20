package hw3.puzzle;

/**
 * Created by vip on 3/17/17.
 */

import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;


public class Solver {
    int moves = 0;
    ArrayList<WorldState> solution;

    public Solver(WorldState initial) {
        solution = new ArrayList<WorldState>();
        MinPQ<SearchNode> searchnodes = new MinPQ<>();
        SearchNode begin = new SearchNode(initial, 0, null);
        searchnodes.insert(begin);
        boolean findreallove = false;
        while (!searchnodes.isEmpty() && !findreallove) {
            SearchNode current = searchnodes.min();
            if (current.node.estimatedDistanceToGoal() != 0) {
                searchnodes.delMin();
                for (WorldState a : current.node.neighbors()) {
                    if (current.previous == null) {
                        SearchNode newnode = new SearchNode(a, current.movestoreach + 1, current);
                        searchnodes.insert(newnode);
                    } else if (!a.equals(current.previous.node)) {
                        SearchNode newnode = new SearchNode(a, current.movestoreach + 1, current);
                        searchnodes.insert(newnode);
                    }
                }
            } else {
                SearchNode tobeadd = current;
                while (tobeadd != null) {
                    solution.add(0, tobeadd.node);
                    moves = moves + 1;
                    tobeadd = tobeadd.previous;
                }
                moves = moves - 1;
                findreallove = true;
            }
        }
    }


    private class SearchNode implements Comparable<SearchNode> {
        WorldState node;
        int movestoreach;
        SearchNode previous;
        int togoal;
        int overall;

        SearchNode(WorldState me, int reachme, SearchNode prev) {
            node = me;
            movestoreach = reachme;
            previous = prev;
            togoal = me.estimatedDistanceToGoal();
            overall = togoal + movestoreach;
        }

        @Override
        public int compareTo(SearchNode T) {
            return this.overall - T.overall;
        }
    }

    public int moves() {
        return moves;
    }

    public Iterable<WorldState> solution() {
        return solution;
    }
}
