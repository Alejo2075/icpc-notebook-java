package notebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** Successive shortest augmenting paths with potentials; initial edge costs must be nonnegative. */
public final class MinCostFlow {
    public record Result(long flow, long cost) {}
    private static final class Edge {
        int to, reverse;
        long capacity, cost;
        Edge(int to, int reverse, long capacity, long cost) {
            this.to = to; this.reverse = reverse; this.capacity = capacity; this.cost = cost;
        }
    }
    private final List<List<Edge>> graph = new ArrayList<>();

    public MinCostFlow(int vertices) {
        if (vertices < 0) throw new IllegalArgumentException();
        for (int i = 0; i < vertices; i++) graph.add(new ArrayList<>());
    }

    public void addEdge(int from, int to, long capacity, long cost) {
        if (capacity < 0 || cost < 0) throw new IllegalArgumentException();
        int reverseIndex = graph.get(to).size() + (from == to ? 1 : 0);
        Edge forward = new Edge(to, reverseIndex, capacity, cost);
        Edge backward = new Edge(from, graph.get(from).size(), 0, -cost);
        graph.get(from).add(forward); graph.get(to).add(backward);
    }

    /** Send up to requestedFlow units. O(flow * E log V) for unit capacities. */
    public Result send(int source, int sink, long requestedFlow) {
        if (requestedFlow < 0) throw new IllegalArgumentException();
        int n = graph.size();
        long[] potential = new long[n];
        long flow = 0, cost = 0;
        while (flow < requestedFlow) {
            long[] dist = new long[n];
            int[] previousVertex = new int[n], previousEdge = new int[n];
            Arrays.fill(dist, Long.MAX_VALUE);
            PriorityQueue<long[]> queue = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
            dist[source] = 0; queue.add(new long[] {0, source});
            while (!queue.isEmpty()) {
                long[] current = queue.remove();
                int v = (int) current[1];
                if (current[0] != dist[v]) continue;
                for (int i = 0; i < graph.get(v).size(); i++) {
                    Edge edge = graph.get(v).get(i);
                    if (edge.capacity == 0) continue;
                    long reduced = Math.addExact(edge.cost, potential[v] - potential[edge.to]);
                    long candidate = Math.addExact(dist[v], reduced);
                    if (candidate < dist[edge.to]) {
                        dist[edge.to] = candidate;
                        previousVertex[edge.to] = v;
                        previousEdge[edge.to] = i;
                        queue.add(new long[] {candidate, edge.to});
                    }
                }
            }
            if (dist[sink] == Long.MAX_VALUE) break;
            for (int v = 0; v < n; v++) if (dist[v] != Long.MAX_VALUE)
                potential[v] = Math.addExact(potential[v], dist[v]);
            long amount = requestedFlow - flow;
            for (int v = sink; v != source; v = previousVertex[v])
                amount = Math.min(amount, graph.get(previousVertex[v]).get(previousEdge[v]).capacity);
            for (int v = sink; v != source; v = previousVertex[v]) {
                Edge edge = graph.get(previousVertex[v]).get(previousEdge[v]);
                edge.capacity -= amount;
                Edge reverse = graph.get(v).get(edge.reverse);
                reverse.capacity += amount;
                cost = Math.addExact(cost, Math.multiplyExact(amount, edge.cost));
            }
            flow += amount;
        }
        return new Result(flow, cost);
    }
}
