package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Exact minimum of binary unary and submodular pairwise energies by an s-t cut. */
public final class BinaryGraphCut {
    private BinaryGraphCut() {}
    public record Result(long energy, boolean[] labelOne) {}

    private static final class Edge {
        int to, reverse;
        long capacity;
        Edge(int to,int reverse,long capacity) { this.to=to; this.reverse=reverse; this.capacity=capacity; }
    }

    /**
     * Minimize sum_i unary[i][label_i] plus pairwise[e][label_u][label_v].
     * Each pair must satisfy E00+E11 <= E01+E10. Uses Dinic max flow.
     */
    public static Result minimize(long[][] unary, int[][] endpoints, long[][][] pairwise) {
        int n=unary.length;
        if(endpoints.length!=pairwise.length) throw new IllegalArgumentException("pair count mismatch");
        long constant=0;
        long[] oneCost=new long[n];
        for(int i=0;i<n;i++) {
            if(unary[i].length!=2) throw new IllegalArgumentException("two unary costs required");
            constant=Math.addExact(constant,unary[i][0]);
            oneCost[i]=Math.subtractExact(unary[i][1],unary[i][0]);
        }
        long[][] pairCapacity=new long[endpoints.length][3];
        for(int e=0;e<endpoints.length;e++) {
            if(endpoints[e].length!=2 || pairwise[e].length!=2
                    || pairwise[e][0].length!=2 || pairwise[e][1].length!=2)
                throw new IllegalArgumentException("pairwise table must be 2 by 2");
            int u=endpoints[e][0],v=endpoints[e][1];
            if(u<0||u>=n||v<0||v>=n||u==v) throw new IllegalArgumentException("invalid pair");
            long e00=pairwise[e][0][0],e01=pairwise[e][0][1];
            long e10=pairwise[e][1][0],e11=pairwise[e][1][1];
            long capacity=Math.subtractExact(Math.addExact(e01,e10),Math.addExact(e00,e11));
            if(capacity<0) throw new IllegalArgumentException("pairwise energy is not submodular");
            constant=Math.addExact(constant,e00);
            oneCost[u]=Math.addExact(oneCost[u],Math.subtractExact(e10,e00));
            oneCost[v]=Math.addExact(oneCost[v],Math.subtractExact(e11,e10));
            pairCapacity[e][0]=u;pairCapacity[e][1]=v;pairCapacity[e][2]=capacity;
        }
        int source=n,sink=n+1;
        Dinic flow=new Dinic(n+2);
        for(int i=0;i<n;i++) {
            if(oneCost[i]>=0) flow.add(source,i,oneCost[i]);
            else {
                constant=Math.addExact(constant,oneCost[i]);
                flow.add(i,sink,Math.negateExact(oneCost[i]));
            }
        }
        for(long[] edge:pairCapacity) flow.add((int)edge[0],(int)edge[1],edge[2]);
        long cut=flow.maxFlow(source,sink);
        boolean[] reachable=flow.reachable(source),labelOne=new boolean[n];
        for(int i=0;i<n;i++) labelOne[i]=!reachable[i];
        return new Result(Math.addExact(constant,cut),labelOne);
    }

    private static final class Dinic {
        final List<List<Edge>> graph;
        final int[] level,next;
        Dinic(int n) {
            graph=new ArrayList<>();for(int i=0;i<n;i++)graph.add(new ArrayList<>());
            level=new int[n];next=new int[n];
        }
        void add(int from,int to,long capacity) {
            Edge forward=new Edge(to,graph.get(to).size(),capacity);
            Edge reverse=new Edge(from,graph.get(from).size(),0);
            graph.get(from).add(forward);graph.get(to).add(reverse);
        }
        long maxFlow(int source,int sink) {
            long total=0;
            while(buildLevels(source,sink)) {
                Arrays.fill(next,0);
                long sent;
                while((sent=send(source,sink,Long.MAX_VALUE))>0) total=Math.addExact(total,sent);
            }
            return total;
        }
        boolean buildLevels(int source,int sink) {
            Arrays.fill(level,-1);level[source]=0;
            ArrayDeque<Integer> queue=new ArrayDeque<>();queue.add(source);
            while(!queue.isEmpty()) {
                int vertex=queue.remove();
                for(Edge edge:graph.get(vertex)) if(edge.capacity>0&&level[edge.to]<0) {
                    level[edge.to]=level[vertex]+1;queue.add(edge.to);
                }
            }
            return level[sink]>=0;
        }
        long send(int vertex,int sink,long amount) {
            if(vertex==sink)return amount;
            for(;next[vertex]<graph.get(vertex).size();next[vertex]++) {
                Edge edge=graph.get(vertex).get(next[vertex]);
                if(edge.capacity==0||level[edge.to]!=level[vertex]+1)continue;
                long sent=send(edge.to,sink,Math.min(amount,edge.capacity));
                if(sent==0)continue;
                edge.capacity-=sent;
                Edge reverse=graph.get(edge.to).get(edge.reverse);
                reverse.capacity=Math.addExact(reverse.capacity,sent);
                return sent;
            }
            return 0;
        }
        boolean[] reachable(int source) {
            boolean[] seen=new boolean[graph.size()];
            ArrayDeque<Integer> queue=new ArrayDeque<>();seen[source]=true;queue.add(source);
            while(!queue.isEmpty()) {
                int vertex=queue.remove();
                for(Edge edge:graph.get(vertex))if(edge.capacity>0&&!seen[edge.to]) {
                    seen[edge.to]=true;queue.add(edge.to);
                }
            }
            return seen;
        }
    }
}
