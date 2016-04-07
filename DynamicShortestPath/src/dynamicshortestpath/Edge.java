/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicshortestpath;

import java.util.ArrayList;

public class Edge {

    private ArrayList<Integer> nodes;

    public Edge() {
        nodes = new ArrayList<>();
    }

    public void addNode(int n) {
        synchronized (nodes) {
            nodes.add(n);
        }
    }

    public ArrayList<Integer> getNodes() {
        synchronized (nodes) {
            return nodes;
        }
    }
}
