package notebook;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Reproducible reference comparisons for KACTL-inspired coverage additions. */
public final class KactlGapTest {
    private static int checks;
    private static final Random RANDOM = new Random(20261025);
    private static void expect(boolean good) {
        checks++;
        if (!good) throw new AssertionError("KACTL gap check " + checks + " failed");
    }
    private static void equal(long actual, long expected) { expect(actual == expected); }

    private static void dataStructures() {
        RollbackDSU dsu = new RollbackDSU(5);
        int original = dsu.snapshot();
        dsu.union(0, 1); dsu.union(1, 2);
        int middle = dsu.snapshot();
        dsu.union(3, 4); dsu.union(2, 4);
        equal(dsu.components(), 1);
        dsu.rollback(middle);
        equal(dsu.components(), 3);
        equal(dsu.size(0), 3);
        expect(dsu.find(0) != dsu.find(4));
        dsu.rollback(original);
        equal(dsu.components(), 5);
        for (int trial = 0; trial < 120; trial++) {
            int rows = 1 + RANDOM.nextInt(6), columns = 1 + RANDOM.nextInt(6);
            Fenwick2D fenwick = new Fenwick2D(rows, columns);
            long[][] plain = new long[rows][columns];
            for (int step = 0; step < 60; step++) {
                if (RANDOM.nextBoolean()) {
                    int r = RANDOM.nextInt(rows), c = RANDOM.nextInt(columns), delta = RANDOM.nextInt(21)-10;
                    fenwick.add(r, c, delta); plain[r][c] += delta;
                } else {
                    int a = RANDOM.nextInt(rows+1), b = RANDOM.nextInt(rows+1);
                    int c = RANDOM.nextInt(columns+1), d = RANDOM.nextInt(columns+1);
                    int top = Math.min(a,b), bottom = Math.max(a,b), left = Math.min(c,d), right = Math.max(c,d);
                    long expected = 0;
                    for (int r = top; r < bottom; r++) for (int col = left; col < right; col++) expected += plain[r][col];
                    equal(fenwick.rectangle(top,left,bottom,right), expected);
                }
            }
        }
        for (int trial = 0; trial < 200; trial++) {
            int n = RANDOM.nextInt(25), q = 1 + RANDOM.nextInt(20);
            int[] values = new int[n]; int[][] ranges = new int[q][2];
            for (int i = 0; i < n; i++) values[i] = RANDOM.nextInt(9)-4;
            for (int i = 0; i < q; i++) {
                int a = RANDOM.nextInt(n+1), b = RANDOM.nextInt(n+1);
                ranges[i] = new int[] {Math.min(a,b),Math.max(a,b)};
            }
            int[] actual = MoQueries.distinctCounts(values,ranges);
            for (int i = 0; i < q; i++) {
                Set<Integer> distinct = new HashSet<>();
                for (int j = ranges[i][0]; j < ranges[i][1]; j++) distinct.add(values[j]);
                equal(actual[i], distinct.size());
            }
        }
        long[] xs = new long[21];
        for (int i = 0; i < xs.length; i++) xs[i] = i-10;
        LiChaoTree tree = new LiChaoTree(xs);
        equal(tree.query(0), Long.MAX_VALUE);
        List<long[]> lines = new ArrayList<>();
        for (int step = 0; step < 80; step++) {
            long slope = RANDOM.nextInt(21)-10, intercept = RANDOM.nextInt(41)-20;
            tree.add(slope,intercept); lines.add(new long[] {slope,intercept});
            for (long x : xs) {
                long best = Long.MAX_VALUE;
                for (long[] line : lines) best = Math.min(best,line[0]*x+line[1]);
                equal(tree.query(x),best);
            }
        }
    }

    private static int bruteMatching(boolean[][] edge, int left, int used) {
        if (left == edge.length) return 0;
        int best = bruteMatching(edge,left+1,used);
        for (int right = 0; right < edge[left].length; right++) if (edge[left][right] && (used & (1<<right))==0)
            best = Math.max(best,1+bruteMatching(edge,left+1,used | (1<<right)));
        return best;
    }

    private static void graphs() {
        for (int trial = 0; trial < 250; trial++) {
            int left = RANDOM.nextInt(6), right = RANDOM.nextInt(6);
            boolean[][] edge = new boolean[left][right];
            BipartiteMatching matching = new BipartiteMatching(left,right);
            for (int i = 0; i < left; i++) for (int j = 0; j < right; j++) if (RANDOM.nextBoolean()) {
                edge[i][j]=true; matching.addEdge(i,j);
            }
            int best = bruteMatching(edge,0,0);
            equal(matching.maximumMatching(),best);
            BipartiteMatching.Cover cover = matching.minimumVertexCover();
            equal(cover.left().length+cover.right().length,best);
            for (int i = 0; i < left; i++) for (int j = 0; j < right; j++) if (edge[i][j]) {
                boolean covered = false;
                for (int v : cover.left()) if (v==i) covered=true;
                for (int v : cover.right()) if (v==j) covered=true;
                expect(covered);
            }
        }
        for (int trial = 0; trial < 180; trial++) {
            int n = 2+RANDOM.nextInt(6);
            long[][] weights = new long[n][n];
            for (int i = 0; i < n; i++) for (int j = i+1; j < n; j++)
                weights[i][j]=weights[j][i]=RANDOM.nextInt(7);
            long expected = Long.MAX_VALUE;
            for (int mask = 1; mask < (1<<n)-1; mask++) {
                long cut=0;
                for (int i = 0; i < n; i++) for (int j = i+1; j < n; j++)
                    if (((mask>>i)&1)!=((mask>>j)&1)) cut+=weights[i][j];
                expected=Math.min(expected,cut);
            }
            equal(GlobalMinCut.weight(weights),expected);
        }
    }

    private static void math() {
        int[] primes = {3,5,7,11,13,17,19,23,29,31,37,41,43};
        for (int prime : primes) for (int base = 1; base < prime; base++) {
            long value=1;
            for (int exponent = 0; exponent < prime; exponent++) {
                equal(ModularExtras.discreteLog(base,value,prime),firstExponent(base,value,prime));
                value=value*base%prime;
            }
        }
        for (int prime : primes) for (int value = 0; value < prime; value++) {
            long root=ModularExtras.modularSquareRoot(value,prime);
            boolean hasRoot=false;
            for (int x=0;x<prime;x++) if (x*x%prime==value) hasRoot=true;
            expect(root==-1 ? !hasRoot : root*root%prime==value);
        }
        int[] mu=ModularExtras.mobius(500);
        for (int n=1;n<mu.length;n++) {
            int sum=0;
            for (int d=1;d<=n;d++) if (n%d==0) sum+=mu[d];
            equal(sum,n==1?1:0);
        }
        equal(CombinatoricsExtras.derangements(0).longValue(),1);
        equal(CombinatoricsExtras.derangements(5).longValue(),44);
        equal(CombinatoricsExtras.stirlingSecond(5,2).longValue(),15);
        equal(CombinatoricsExtras.partitions(10)[10].longValue(),42);
        equal(CombinatoricsExtras.necklaces(4,2).longValue(),6);
        LinearAlgebra.Solution solution=LinearAlgebra.solve(new double[][] {{2,1},{1,-1}},new double[] {5,1});
        expect(solution.consistent() && solution.rank()==2);
        expect(Math.abs(solution.values()[0]-2)<1e-8 && Math.abs(solution.values()[1]-1)<1e-8);
        expect(!LinearAlgebra.solve(new double[][] {{1,1},{1,1}},new double[] {2,3}).consistent());
        expect(Math.abs(LinearAlgebra.determinant(new double[][] {{2,1},{1,-1}})+3)<1e-8);
        for (int trial=0;trial<180;trial++) {
            long[] values=new long[1<<RANDOM.nextInt(6)];
            for(int i=0;i<values.length;i++) values[i]=RANDOM.nextInt(21)-10;
            long[] transformed=values.clone();
            SubsetTransforms.subsetZeta(transformed);
            for(int mask=0;mask<values.length;mask++) {
                long expected=0;
                for(int sub=mask;;sub=(sub-1)&mask) {
                    expected+=values[sub];
                    if(sub==0) break;
                }
                equal(transformed[mask],expected);
            }
            SubsetTransforms.subsetMobius(transformed);
            expect(Arrays.equals(transformed,values));
            SubsetTransforms.xorHadamard(transformed,false);
            SubsetTransforms.xorHadamard(transformed,true);
            expect(Arrays.equals(transformed,values));
        }
        equal(MatrixTreeTheorem.count(1,new int[0][]).longValue(),1);
        equal(MatrixTreeTheorem.count(3,new int[][] {{0,1},{1,2},{0,2}}).longValue(),3);
        for(int trial=0;trial<100;trial++) {
            int vertices=2+RANDOM.nextInt(4), edgeCount=RANDOM.nextInt(10);
            int[][] edges=new int[edgeCount][2];
            for(int i=0;i<edgeCount;i++) {
                edges[i][0]=RANDOM.nextInt(vertices); edges[i][1]=RANDOM.nextInt(vertices);
            }
            long expected=0;
            for(int mask=0;mask<(1<<edgeCount);mask++) if(Integer.bitCount(mask)==vertices-1) {
                int[] parent=new int[vertices];
                for(int i=0;i<vertices;i++) parent[i]=i;
                boolean cycle=false;
                for(int i=0;i<edgeCount;i++) if((mask&(1<<i))!=0) {
                    int a=edges[i][0],b=edges[i][1];
                    while(parent[a]!=a) a=parent[a];
                    while(parent[b]!=b) b=parent[b];
                    if(a==b) { cycle=true; break; }
                    parent[a]=b;
                }
                if(!cycle) expected++;
            }
            equal(MatrixTreeTheorem.count(vertices,edges).longValue(),expected);
        }
    }

    private static long firstExponent(int base,long target,int modulus) {
        long value=1;
        for (int exponent=0;exponent<modulus;exponent++) {
            if (value==target) return exponent;
            value=value*base%modulus;
        }
        return -1;
    }

    private static long cubicMerge(long[] weights) {
        int n=weights.length;
        if (n==0) return 0;
        long[][] dp=new long[n][n];
        long[] prefix=new long[n+1];
        for(int i=0;i<n;i++) prefix[i+1]=prefix[i]+weights[i];
        for(int length=2;length<=n;length++) for(int left=0;left+length<=n;left++) {
            int right=left+length-1; dp[left][right]=Long.MAX_VALUE;
            for(int middle=left;middle<right;middle++)
                dp[left][right]=Math.min(dp[left][right],dp[left][middle]+dp[middle+1][right]+prefix[right+1]-prefix[left]);
        }
        return dp[0][n-1];
    }

    private static void dpAndStrings() {
        for(int trial=0;trial<350;trial++) {
            long[] weights=new long[RANDOM.nextInt(12)];
            for(int i=0;i<weights.length;i++) weights[i]=RANDOM.nextInt(20);
            equal(KnuthOptimization.optimalAdjacentMergeCost(weights),cubicMerge(weights));
            int length=RANDOM.nextInt(12);
            StringBuilder text=new StringBuilder();
            for(int i=0;i<length;i++) text.append((char)('a'+RANDOM.nextInt(4)));
            String input=text.toString(), best=input;
            for(int i=0;i<length;i++) {
                String rotated=input.substring(i)+input.substring(0,i);
                if(rotated.compareTo(best)<0) best=rotated;
            }
            expect(StringExtensions.minimumRotation(input).equals(best));
            SuffixAutomaton automaton=new SuffixAutomaton(input);
            Set<String> substrings=new HashSet<>();
            for(int i=0;i<length;i++) for(int j=i+1;j<=length;j++) substrings.add(input.substring(i,j));
            equal(automaton.distinctSubstringCount(),substrings.size());
            for(String substring:substrings) expect(automaton.contains(substring));
            expect(automaton.contains(""));
            String other="abcabc";
            int common=0;
            for(String substring:substrings) if(other.contains(substring)) common=Math.max(common,substring.length());
            equal(automaton.longestCommonSubstringLength(other),common);
        }
    }

    private static void geometry() {
        CircleGeometry.Point origin=new CircleGeometry.Point(0,0);
        CircleGeometry.Point x=new CircleGeometry.Point(1,0), y=new CircleGeometry.Point(0,1);
        CircleGeometry.Point cross=CircleGeometry.lineIntersection(origin,new CircleGeometry.Point(2,2),
                new CircleGeometry.Point(0,2),new CircleGeometry.Point(2,0));
        expect(Math.abs(cross.x()-1)<1e-9 && Math.abs(cross.y()-1)<1e-9);
        expect(Math.abs(CircleGeometry.pointSegmentDistance(y,origin,x)-1)<1e-9);
        equal(CircleGeometry.circleLine(origin,1,new CircleGeometry.Point(-2,0),new CircleGeometry.Point(2,0)).size(),2);
        equal(CircleGeometry.circleCircle(origin,1,new CircleGeometry.Point(2,0),1).size(),1);
        equal(CircleGeometry.circleCircle(origin,1,new CircleGeometry.Point(5,0),1).size(),0);
    }

    public static void main(String[] args) {
        dataStructures(); graphs(); math(); dpAndStrings(); geometry();
        System.out.println("OK: "+checks+" KACTL gap checks");
    }
}
