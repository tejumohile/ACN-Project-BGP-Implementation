/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** *
 * @author Shruti
 */
public class Node {

    static int nodeid = 0;
    public static HashMap<Integer, ArrayList<Integer>> routingTable = new HashMap<Integer, ArrayList<Integer>>(); // Routing Table fo each node that includes the node id ,TS and list of all incoming neighbours learnd from hello protocol
    int last_input_final_index = 0; // Node reads its input file and is written by controller and we will save the size of the input_node (size of list )
    static ArrayList<Integer> neighborList = new ArrayList<Integer>();
    static int lastinputfileread_index = 0;
    static HashMap<Integer, Integer> linkstateTS = new HashMap<Integer, Integer>();
    static int source = -1;
    static String dataMessage = null;
    static int parent = -1;
    static int joinTreeRootedAtSource = -1;

    public static void readinputfiles(int nodeid1) {
        List<String> list1 = Utility.readFile("input_" + nodeid1 + ".txt");
        for (int index = lastinputfileread_index; index < list1.size(); index++) {
            String[] s = list1.get(index).split(" ");
            if (s[0].equals("HELLO")) {
                if (!neighborList.contains(Integer.parseInt(s[1]))) {
                    neighborList.add(Integer.parseInt(s[1])); // List of incoming neighbours learned from Hello protocol
                }
                //adding incoming neighbors
                if (!routingTable.containsKey(nodeid)) {
                    ArrayList<Integer> k = new ArrayList<Integer>();
                    routingTable.put(nodeid, k);

                } else if (routingTable.containsKey(nodeid)) {
                    ArrayList<Integer> k = routingTable.get((nodeid));
                    if (!k.contains(Integer.parseInt(s[1]))) {
                        k.add(Integer.parseInt(s[1]));
                    }
                }
            } else if (s[0].equals("JOINMESSAGE")) {
                String newJoin = s[0] + " " + s[1];
//                System.out.println("Received join message " + list1.get(index));
                if (s.length > 4) {
                    if (Integer.parseInt(s[4]) == nodeid) {
                        newJoin = newJoin + " " + s[2] + " " + s[3];
                        if (s.length > 5) {
                            for (int p = 5; p < s.length; p++) {
                                newJoin = newJoin + " " + s[p];
                            }
                        }
                    } else {
                        newJoin = null;
                    }

                }
                if (s.length == 4) {
                    if (Integer.parseInt(s[3]) == nodeid) {
                        joinTreeRootedAtSource = Integer.parseInt(s[2]);
                        newJoin = newJoin + " " + s[2];

                    } else {
                        newJoin = null;
                    }
                }
                if (s.length == 3) {


                    if (Integer.parseInt(s[2]) == nodeid) {
                        newJoin = null;
                        sendData();
                    } else {
                        HashMap<Integer, ArrayList<Integer>> pathList = new Dijkstra().doDijkstra(routingTable, Integer.parseInt(s[1]));
                        if (pathList.get(Integer.parseInt(s[2])).contains(nodeid)) {
                            joinTreeRootedAtSource = Integer.parseInt(s[2]);
                            newJoin = newJoin + " " + s[2];
                        } else {
                            newJoin = null;
                        }
                    }
                }

                if (newJoin != null) {
                    System.out.println("Forwarding join message " + newJoin);
                    Utility.writeFile("output_" + nodeid + ".txt", newJoin);
                }



            } else if (s[0].equals("LINKSTATEMESSAGE")) {


                if (!linkstateTS.containsKey(Integer.parseInt(s[1]))) {
                    linkstateTS.put(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                    routing_table_update(list1.get(index));
                    Utility.writeFile("output_" + nodeid1 + ".txt", list1.get(index));
                } else {
                    if (linkstateTS.get(Integer.parseInt(s[1])) < Integer.parseInt(s[2])) {
                        linkstateTS.put(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                        routing_table_update(list1.get(index));
                        Utility.writeFile("output_" + nodeid1 + ".txt", list1.get(index));
                    }
                }
            } else if (s[0].equals("DATA")) {
                System.out.println("In data forwading ");
                if (source == -1 || Integer.parseInt(s[2]) != source) {
                    if (joinTreeRootedAtSource == Integer.parseInt(s[2])) {


                        String message = "";
                        s[1] = String.valueOf(nodeid);
                        for (int s1 = 0; s1 < s.length; s1++) {
                            message = message + s[s1];
                            if (s1 + 1 < s.length) {
                                message = message + " ";
                            }
                        }
                        System.out.println("what is message ? " +message);
                        Utility.writeFile("output_" + nodeid + ".txt", message);
                    }
                } else if (Integer.parseInt(s[2]) == source) {
                    if (Integer.parseInt(s[1]) == parent) {
                        String message = "";
                        for (int p = 3; p < s.length; p++) {
                            message = message + s[p];
                            if (p + 1 < s.length) {
                                message = message + " ";
                            }
                        }
                        System.out.println("what is message ? " +message);
                        Utility.writeFile(nodeid + "_received_from_" + source+".txt", message);
                    }
                }

            }
        }
        // lastinputfileread_index = list1.size();
        lastinputfileread_index = list1.size();
    }

    private static void sendData() {
        if (!dataMessage.equals(null)) {
            Utility.writeFile("output_" + nodeid + ".txt", "DATA " + nodeid + " " + nodeid + " " + dataMessage);
        }

    }

    //shruti starts
    public static void routing_table_update(String get) {

        ArrayList<Integer> k;
        String[] s = get.split(" ");



        if (!routingTable.containsKey(Integer.parseInt(s[1]))) {

            if (s.length > 2) {
                k = new ArrayList<Integer>();
                routingTable.put(Integer.parseInt(s[1]), k);
                for (int m = 3; m < s.length; m++) {
                    k.add(new Integer(s[m]));
                }
            }
        } else if (routingTable.containsKey(Integer.parseInt(s[1]))) {

            if (s.length > 2) {
                k = routingTable.get(Integer.parseInt(s[1]));
                for (int m = 3; m < s.length; m++) {
                    if (!k.contains(Integer.parseInt(s[m]))) {
                        k.add(new Integer(s[m]));
                    }
                }
            }
        }

        System.out.println("routingTable for node id ::::: " + nodeid + ":::" + routingTable);
    }

    private static String getJoinMessage(int source) {
        try {
            if (source == -1) {
                return null;
            } else {
                String message = "JOINMESSAGE" + " " + nodeid + " " + source;
                HashMap<Integer, ArrayList<Integer>> shortestPathTableFromSource = new Dijkstra().doDijkstra(routingTable, nodeid);
                ArrayList<Integer> shortestPathLink = shortestPathTableFromSource.get(source);
                parent = shortestPathLink.get(1);

                message = message + " " + parent;

                shortestPathTableFromSource = new Dijkstra().doDijkstra(routingTable, parent);
                shortestPathLink = shortestPathTableFromSource.get(nodeid);
                System.out.print("Shortest path link ::: " + shortestPathLink);
                for (int p = shortestPathLink.size() - 2; p > 0; p--) {
                    message = message + " " + shortestPathLink.get(p);
                }
                return message;
            }
        } catch (Exception e) {
            parent = -1;
            return null;
        }
    }

    //shruti ends
    public static void main(String args[]) {

        try {
            System.out.println("Enter the node id ::::: ");
            Scanner scan = new Scanner(System.in);
            nodeid = scan.nextInt();

            System.out.println("Is it receiver? Type receiver");
            String yesOrNo = new BufferedReader(new InputStreamReader(System.in)).readLine();

            if (yesOrNo.equals("receiver")) {
                System.out.println("Enter the source");
                source = scan.nextInt();
            }

            System.out.println("Is it source? Type source");
            if (new BufferedReader(new InputStreamReader(System.in)).readLine().equals("source")) {
                System.out.println("Enter the data message");
                dataMessage = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }


            //Shruti starts
            //This function is to write to the ouput file of the node so that it can be read by the controller node

            for (int i = 0; i < 50; i++) {


                Utility.writeFile("output_" + nodeid + ".txt", "HELLO " + nodeid); // nodeid is the id of the node sending the message
                Thread.sleep(15000);
                readinputfiles(nodeid);

                String linkstateMessage = "LINKSTATEMESSAGE " + nodeid + " " + i;
                for (int m = 0; m < neighborList.size(); m++) {
                    //Thread.sleep(10000);
                    linkstateMessage = linkstateMessage + " " + neighborList.get(m).toString();
                }

                Utility.writeFile("output_" + nodeid + ".txt", linkstateMessage); /// here i is Time stamp

                //join sending
                if (source == -1) {
                    Thread.sleep(10000);
                } else {
                    if (routingTable.containsKey(source)) {
                        String joinMsg = getJoinMessage(source);
                        if (joinMsg != null) {
                            Utility.writeFile("output_" + nodeid + ".txt", joinMsg);
                        }

                    }

                }

            }
//            while(dataMessage!=null)
//            {
//                readinputfiles(nodeid);
//            }

        } catch (Exception ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }



        //Shruti ends


    }

    private static boolean isTotalNodesEqual() {
        boolean flag = false;
        HashSet<Integer> localSet = new HashSet<Integer>();
        localSet.addAll(routingTable.keySet());
        ArrayList<Integer> newlist = new ArrayList<Integer>();
        while (routingTable.values().iterator().hasNext()) {
            newlist = routingTable.values().iterator().next();
            for (int i = 0; i < newlist.size(); i++) {
                localSet.add(newlist.get(i));
            }
        }
        HashSet<Integer> remoteSet = new HashSet<Integer>();
        remoteSet.addAll(Controller.connectionTable.keySet());

        if (remoteSet.size() == localSet.size()) {
            flag = true;
        }

        return flag;

    }
}
