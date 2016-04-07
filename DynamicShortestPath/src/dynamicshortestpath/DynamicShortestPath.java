/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicshortestpath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author OmarElfarouk
 */
public class DynamicShortestPath {

    static BufferedReader bufferRead = new BufferedReader(
            new InputStreamReader(System.in));
    String line[];
    String src, dest;
    char oper;
    boolean needToRecompute;
    boolean needToRecomputeAll;
    ArrayList<Integer> nodesToRecompute = new ArrayList<Integer>();
    private HashSet<String> filteredOperations = new HashSet<String>();
    private ArrayList<String> operations = new ArrayList<String>();
    Graph graph = Graph.getInstance();
    static int coresNum = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        DynamicShortestPath shortestPath = new DynamicShortestPath();
        Graph graph = Graph.getInstance();
        shortestPath.initGraph();
        System.out.println("Size: " + graph.getSize());
        long startTime = System.currentTimeMillis();
        shortestPath.runBFS();
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime));
        shortestPath.takeQuery();
    }

    public ArrayList<String> optimizeBatch(ArrayList<String> batchOperations) {

        String src, dest;
        char oper;
        String line[];
        for (int i = 0; i < batchOperations.size(); i++) {

            line = batchOperations.get(i).split(" ");
            oper = line[0].charAt(0);
            src = line[1];
            dest = line[2];

            if (filteredOperations.contains("A " + src + " " + dest)) {
                if (oper == 'A') {
                    continue;
                } else {
                    filteredOperations.remove("A " + src + " " + dest);
                }
            } else if (filteredOperations.contains("D " + src + " " + dest)) {
                if (oper == 'A') {
                    filteredOperations.remove("D " + src + " " + dest);

                } else {
                    continue;
                }

            } else {
                filteredOperations.add(oper + " " + src + " " + dest);
            }

        }

        Iterator<String> setIter;
        ArrayList<String> finalOperations = new ArrayList<String>();
        setIter = filteredOperations.iterator();

        while (setIter.hasNext()) {
            finalOperations.add(setIter.next());
        }

        needToRecompute = !operations.isEmpty();

        return finalOperations;
    }

    public boolean needToRecompute() {
        return needToRecompute;
    }

    public boolean constructGraph(String operation, Graph g) {

        line = operation.split(" ");

        src = line[0];

        if (src.charAt(0) == 'S') {
            System.out.println("END GRAPH");
            return false;
        }
        dest = line[1];

        // Add edge in graph or delete edge from graph
        g.addEdge(Integer.parseInt(src), Integer.parseInt(dest));
        return true;
    }

    public boolean processBatch(String operation) {

        line = operation.split(" ");
        oper = line[0].charAt(0);
        if (oper == 'F') {
            return false;
        }
        src = line[1];
        dest = line[2];

        if (oper == 'Q') {
            operations = optimizeBatch(operations);

            //TODO do add or delete operations
            String line2[];
            String src2, dest2;
            char oper2;
            for (int i = 0; i < operations.size(); i++) {
                line2 = operations.get(i).split(" ");
                oper2 = line2[0].charAt(0);
                src2 = line2[1];
                dest2 = line2[2];
                if (oper2 == 'A') {
                    graph.addEdge(Integer.parseInt(src2), Integer.parseInt(dest2));
                    needToRecomputeAll = true;
                } else {
                    Edge edge = graph.deleteEdge(Integer.parseInt(src2), Integer.parseInt(dest2));
                    if (!needToRecomputeAll) {
                        nodesToRecompute.addAll(edge.getNodes());
                    }
                }

            }
            operations = new ArrayList<String>();
            filteredOperations = new HashSet<String>();

            if (needToRecompute()) {
                //TODO do query
                if (needToRecomputeAll) {
                    runBFS();
                } else {
                    String tempAppend = "Run BFS for ";

                    ExecutorService executor = Executors.newFixedThreadPool(coresNum);

                    for (Integer source : nodesToRecompute) {
                        Runnable worker = new ShortestPathFinder(source, graph);
                        executor.execute(worker);
                        tempAppend += source + ", ";
                    }
                    executor.shutdown();
                    while (!executor.isTerminated());
                    tempAppend += "nodes only";
                }
                int cost = graph.getCost(Integer.parseInt(src), Integer.parseInt(dest));
                System.out.println("Cost from " + src + " to " + dest + " = " + cost);
                needToRecompute = false;
                needToRecomputeAll = false;
                nodesToRecompute = new ArrayList<Integer>();
            } else {
                int cost = graph.getCost(Integer.parseInt(src), Integer.parseInt(dest));
                System.out.println("Cost from " + src + " to " + dest + " = " + cost);
            }
        } else {
            operations.add(operation);
        }

        return true;
    }

    public void initGraph() {

        try {
            String s = bufferRead.readLine();

            while (constructGraph(s, graph)) {
                s = bufferRead.readLine();
            }
            // END reading in graph

            // compute shortest paths
        } catch (IOException ex) {
            Logger.getLogger(BatchReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void takeQuery() {
        try {
            String s = bufferRead.readLine();
            while (processBatch(s)) {
                s = bufferRead.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(BatchReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runBFS() {
//        BatchReader batchReader = new BatchReader();
        DynamicShortestPath shortestPath = new DynamicShortestPath();
        ExecutorService executor = Executors.newFixedThreadPool(coresNum);
        Iterator it = graph.getIteratorTopolgy();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Runnable worker = new ShortestPathFinder((int) pair.getKey(), graph);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated());
    }
}
