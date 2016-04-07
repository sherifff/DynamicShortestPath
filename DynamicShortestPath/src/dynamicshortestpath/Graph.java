/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicshortestpath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author OmarElfarouk
 */
public class Graph {

    private static Graph instance = null;

    Map<Integer, Map<Integer, Edge>> topolgy;
    Map<Integer, Map<Integer, Integer>> costMap;

    private Graph() {
        topolgy = new HashMap<>();
        costMap = new HashMap<>();
    }

    public static Graph getInstance() {
        if (instance == null) {
            instance = new Graph();
        }
        return instance;
    }

    public void addEdge(int source, int destination) {
        if (!topolgy.containsKey(source)) {
            topolgy.put(source, new HashMap<>());
        }
        if (!topolgy.containsKey(destination)) {
            topolgy.put(destination, new HashMap<>());
        }
        if (!topolgy.get(source).containsKey(destination)) {
            topolgy.get(source).put(destination, new Edge());
        }
    }

    public Edge deleteEdge(int source, int destination) {
        if (!topolgy.containsKey(source)) {
            return new Edge();
        }
        if (topolgy.get(source).containsKey(destination)) {
            return topolgy.get(source).remove(destination);
        }
        return new Edge();
    }

    public void addCost(int source, Map<Integer, Integer> map) {
        costMap.put(source, map);
    }

    // inComplete
    public void migrateCost(int source, int oldSource, int costSource) {
        Map<Integer, Integer> map = costMap.get(oldSource);
        if (!costMap.containsKey(source)) {
            costMap.put(source, new HashMap<>());
        }
        for (Map.Entry pair : map.entrySet()) {
            System.out.println(pair.getKey() + " = " + pair.getValue());
            if (!costMap.get(source).containsKey(pair.getKey())) {
                costMap.get(source).put((int) pair.getKey(), (int) pair.getValue());
            } else if ((int) pair.getValue() < costMap.get(source).get((int) pair.getKey())) {
                costMap.get(source).put((int) pair.getKey(), (int) pair.getValue());
            }
        }
    }

    public int getCost(int source, int dest) {
        if (costMap.containsKey(source) && costMap.get(source).containsKey(dest)) {
            return costMap.get(source).get(dest);
        }

        return -1;
    }

    /*
     Iterator it = mp.entrySet().iterator();
     while (it.hasNext()) {
     Map.Entry pair = (Map.Entry)it.next();
     System.out.println(pair.getKey() + " = " + pair.getValue());
     it.remove(); // avoids a ConcurrentModificationException
     }
    
     */
    public Iterator getChilds(int source) {
        return topolgy.get(source).entrySet().iterator();
    }

    public Iterator getIteratorTopolgy() {
        return topolgy.entrySet().iterator();
    }

    public int getSize() {
        return topolgy.size();
    }

    public Map getMapChilds(int source) {
        return topolgy.get(source);
    }

    public Map<Integer, Integer> getIndex() {
        Map<Integer, Integer> index = new HashMap<>();
        Iterator iter = topolgy.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            index.put((int) pair.getKey(), i);
            i++;
            iter.remove(); // avoids a ConcurrentModificationException
        }
        return index;
    }

    Edge getEdge(int src, int dest) {
        return topolgy.get(src).get(dest);
    }

    void resetCost(int source) {
        costMap.put(source, new HashMap<>());
    }
}
