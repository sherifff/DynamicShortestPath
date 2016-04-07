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
import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchReader {

    static BufferedReader bufferRead = new BufferedReader(
            new InputStreamReader(System.in));
    String line[];
    String src, dest;
    char oper;
    private boolean needToRecompute;
    private HashSet<String> filteredOperations = new HashSet<String>();
    private ArrayList<String> operations = new ArrayList<String>();
    Graph graph = Graph.getInstance();

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
        needToRecompute = finalOperations.size() == 0 ? false : true;

        return finalOperations;
    }

    public boolean needToRecompute() {
        return needToRecompute;
    }

    public boolean constructGraph(String operation, Graph g) {

        line = operation.split(" ");

        src = line[0];
        if (src.compareTo("S") == 0) {
            System.out.println("END Batch");
            return false;
        }
        dest = line[1];

        // Add edge in graph or delete edge from graph
        System.out.println("Add " + src + " " + dest);
        g.addEdge(Integer.parseInt(src), Integer.parseInt(dest));
        return true;
    }

    public boolean processBatch(String operation) {

        line = operation.split(" ");
        oper = line[0].charAt(0);
        if (oper == 'F') {
            System.out.println("END Batch");
            return false;
        }
        src = line[1];
        dest = line[2];

        // optimize queries
//		System.out.println((oper == 'A' ? "Add" : "Delete") + " " + src + " "
//				+ dest);
        if (oper == 'Q') {
            operations = optimizeBatch(operations);
            //TODO do add or delete operations
            if (needToRecompute()) {
                //TODO do query
                int cost = graph.getCost(Integer.parseInt(src), Integer.parseInt(dest));
                System.out.println("Cost" + cost);
            } else {
                int cost = graph.getCost(Integer.parseInt(src), Integer.parseInt(dest));
                System.out.println("Cost" + cost);
            }
            operations = new ArrayList<String>();

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

//    public static void main(String[] args) throws IOException {
//        // TODO Auto-generated method stub
//
//        // input graph then R then batches separated S then F at the end.
//        
//
////        System.out.println(s);
//
//    }
}
