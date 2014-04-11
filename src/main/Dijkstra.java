/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class Router implements Comparable<Router>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Router previous;
    public Router(String argName) { name = argName; }
    public String toString() { return name; }
    public int compareTo(Router other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge
{
    public final Router target;
    public final double weight;
    public Edge(Router argTarget, double argWeight)
    { target = argTarget; weight = argWeight; }
}

public class Dijkstra
{
    public static void computePaths(Router source)
    {
        source.minDistance = 0.;
        PriorityQueue<Router> vertexQueue = new PriorityQueue<Router>();
      	vertexQueue.add(source);

	while (!vertexQueue.isEmpty()) {
	    Router u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Router v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
		if (distanceThroughU < v.minDistance) {
		    vertexQueue.remove(v);
		    v.minDistance = distanceThroughU ;
		    v.previous = u;
		    vertexQueue.add(v);
		}
            }
        }
    }

    public static  List<Router> getShortestPathTo(Router target)
    {
        List<Router> path = new ArrayList<Router>();
        for (Router vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args)
    {
        
        ArrayList<Integer> k ;
            HashMap<Integer, ArrayList<Integer>> routingTable = new HashMap<Integer, ArrayList<Integer>>();
            routingTable.put(0,k = new ArrayList<Integer>());
            k.add(8);
            k.add(4);


            routingTable.put(1,k = new ArrayList<Integer>());
        
            k.add(3);
          

            routingTable.put(3,k = new ArrayList<Integer>());
            k.add(1);
            k.add(0);
            k.add(5);

            routingTable.put(8,k = new ArrayList<Integer>());
            
            k.add(0);
       

            routingTable.put(4,k = new ArrayList<Integer>());
            k.add(0);
             k.add(5);
               routingTable.put(5,k = new ArrayList<Integer>());
            k.add(3);
            k.add(9);
            k.add(4);
               routingTable.put(9,k = new ArrayList<Integer>());
            k.add(3);

            doDijkstra(routingTable, 3);
         

    }
    public  static HashMap<Integer, ArrayList<Integer>> doDijkstra(HashMap<Integer, ArrayList<Integer>> routingTable, int src)
    {
        Router r[] = new Router[routingTable.size()];
        Object [] nodes = routingTable.keySet().toArray();
        for(int i = 0; i < routingTable.size() ; i++)
        {
            r[i] = new Router(nodes[i].toString());

        }
        for(int i = 0; i < routingTable.size() ; i++)
        {
            r[i].adjacencies = new Edge[routingTable.get(Integer.parseInt(nodes[i].toString())).size()];

            for(int j = 0 ; j< routingTable.get(Integer.parseInt(nodes[i].toString())).size() ; j++)
            {
                for(int k = 0 ; k < routingTable.size() ; k++)
                {
                    if(r[k].name.equals(routingTable.get(Integer.parseInt(nodes[i].toString())).get(j).toString()))
                        r[i].adjacencies[j] = new Edge(r[k],1);
                }
            }
        }
	

        for(int i = 0; i < routingTable.size() ; i++)
                {
                    if(r[i].name.equals(String.valueOf(src)))
                       computePaths(r[i]);
                }

        HashMap<Integer, ArrayList<Integer>> shortestPathTable = new HashMap<Integer, ArrayList<Integer>>();
        for (Router v : r)
	{
            System.out.println("Distance to " + v + ": " + v.minDistance);
	    List<Router> path = getShortestPathTo(v);
            ArrayList<Integer> newPath = new ArrayList<Integer>();
            for (int i = 0 ; i < path.size() ; i++)
                newPath.add(Integer.parseInt(path.get(i).toString()));
            shortestPathTable.put(Integer.parseInt(v.name), newPath);
	    System.out.println("In Dijkstra" + shortestPathTable);
	}
        return shortestPathTable;
    }
}