/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicshortestpath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ShortestPathFinder implements Runnable {

    Map<Integer, Integer> cost;
    Map<Integer, Integer> path;
    int node;
    Graph graph;

    public ShortestPathFinder(int node, Graph graph) {
        cost = new HashMap<>();
        path = new HashMap<>();
        this.node = node;
        this.graph = Graph.getInstance();
    }

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName() + " Start BFS Node: " + node);
        bfs(node);
//        System.out.println(Thread.currentThread().getName() + " End Node: " + node);
    }

    public void bfs(int source) {
        graph.resetCost(source);
        HashSet<Integer> visited = new HashSet<>();
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(source);
        visited.add(source);
        cost.put(source, 0);
        path.put(source, -1);
        while (!q.isEmpty()) {
            int elem = q.poll();
            Map<Integer, Edge> map = graph.getMapChilds(elem);
            if (map != null ) {
                for (Map.Entry<Integer, Edge> entry : map.entrySet()) {
                    int nodeNum = entry.getKey();
                    if (!visited.contains(nodeNum)) {
                        visited.add(nodeNum);
                        entry.getValue().addNode(source);
                        path.put(nodeNum, elem);
                        if (cost.containsKey(elem)) {
                            cost.put(nodeNum, cost.get(elem) + 1);
                        }
                        q.add(nodeNum);
                    }
                }
            }
        }
        if(cost == null){
            System.out.println("null");
        }
        graph.addCost(source, cost);
    }
}
