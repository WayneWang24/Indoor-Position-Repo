import java.util.*;
import org.opencv.core.Mat;
public class AStar {

    private final List<Integer> s_start;
    private final List<Integer> s_goal;
    private final String heuristic_type;
    private final Env Env; // Assuming Env is another class defined elsewhere
    private final int[][] u_set; // feasible input set
    private final Set<List<Integer>> obs; // position of obstacles
    private final PriorityQueue<Node> OPEN; // priority queue / OPEN set
    // private final Set<List<Integer>> CLOSED; // CLOSED set / VISITED order
    private final Map<List<Integer>, List<Integer>> PARENT; // recorded parent
    private final Map<List<Integer>, Double> g; // cost to come

    public AStar(List<Integer> s_start, List<Integer> s_goal, Mat mask, String heuristic_type) {
        this.s_start = s_start;
        this.s_goal = s_goal;
        this.heuristic_type = heuristic_type;

        this.Env = new Env(mask); // class Env
        this.u_set = Env.getMotions(); // Assuming getMotions() returns List<int[]>
        this.obs = Env.getObs(); // Assuming getObs() returns int[][]

        this.OPEN = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f_value));
        // this.CLOSED = new HashSet<>();
        this.PARENT = new HashMap<>();
        this.g = new HashMap<>();
    }

    public List<List<Integer>> searching() {
        this.PARENT.put(this.s_start,this.s_start);
        this.g.put(this.s_start, 0.0);
        this.g.put(this.s_goal, Double.POSITIVE_INFINITY);
        this.OPEN.add(new Node(f_value(this.s_start), this.s_start));

        while (!OPEN.isEmpty()) {
            Node currentNode = OPEN.poll();
            List<Integer> s = currentNode.state;
            // CLOSED.add(s);

            if (s.get(0).equals(this.s_goal.get(0)) && s.get(1).equals(this.s_goal.get(1))) { // stop condition
                break;
            }

            for (List<Integer> s_n : get_neighbor(s)) {
                double new_cost = g.get(s) + cost(s, s_n);

                g.putIfAbsent(s_n, Double.POSITIVE_INFINITY);

                if (new_cost < g.get(s_n)) { // conditions for updating Cost
                    g.put(s_n, new_cost);
                    this.PARENT.put(s_n, s);
                    this.OPEN.add(new Node(f_value(s_n), s_n));
                }
            }
        }

        return extract_path(PARENT);
    }

    private List<List<Integer>> get_neighbor(List<Integer> s) {
        List<List<Integer>> neighbors = new ArrayList<>();
        for (int[] u : u_set) {
            neighbors.add(List.of(s.get(0) + u[0], s.get(1) + u[1]));
        }
        return neighbors;
    }

    private double cost(List<Integer> s_start, List<Integer> s_goal) {
        if (is_collision(s_start, s_goal)) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.hypot(s_goal.get(0) - s_start.get(0), s_goal.get(1) - s_start.get(1));
    }

    private boolean is_collision(List<Integer> s_start, List<Integer> s_end) {
        if (obs.contains(s_start) || obs.contains(s_end)) {
            return true;
        }

        if (!s_start.get(0).equals(s_end.get(0)) && !s_start.get(1).equals(s_end.get(1))) {
            List<Integer> s1, s2;
            if (s_end.get(0) - s_start.get(0) == s_start.get(1) - s_end.get(1)) {
                s1 = List.of(Math.min(s_start.get(0), s_end.get(0)), Math.min(s_start.get(1), s_end.get(1)));
                s2 = List.of(Math.max(s_start.get(0), s_end.get(0)), Math.max(s_start.get(1), s_end.get(1)));
            } else {
                s1 = List.of(Math.min(s_start.get(0), s_end.get(0)), Math.max(s_start.get(1), s_end.get(1)));
                s2 = List.of(Math.max(s_start.get(0), s_end.get(0)), Math.max(s_start.get(1), s_end.get(1)));
            }

            if (obs.contains(s1) || obs.contains(s2)) {
                return true;
            }
        }

        return false;
    }

    private List<List<Integer>> extract_path(Map<List<Integer>, List<Integer>> PARENT) {
        List<List<Integer>> path = new ArrayList<>();
        path.add(this.s_goal);
        List<Integer> s = this.s_goal;

        while (true) {
            s = PARENT.get(s);
            path.add(s);
            if (s.equals(this.s_start)) {
                break;
            }
        }

        // Collections.reverse(path);
        return path;
    }

    private double f_value(List<Integer> s) {
        return g.get(s) + heuristic(s); // Assuming heuristic(s) is defined elsewhere
    }

    private static class Node {
        double f_value;
        List<Integer> state;

        Node(double f_value, List<Integer> state) {
            this.f_value = f_value;
            this.state = state;
        }
    }
    private double heuristic(List<Integer> s) {
        String heuristicType = heuristic_type; // heuristic type
        List<Integer> goal = s_goal; // goal node

        if (heuristicType.equals("manhattan")) {
            return Math.abs(goal.get(0) - s.get(0)) + Math.abs(goal.get(1) - s.get(1));
        } else {
            return Math.hypot(goal.get(0) - s.get(0), goal.get(1) - s.get(1));
        }
    }
    public Set<List<Integer>> getObs() {
        return this.obs;
    }
}
