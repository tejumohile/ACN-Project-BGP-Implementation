package main;

import java.util.*;

public class Controller {

    public static HashMap<Integer, ArrayList<Integer>> connectionTable = new HashMap<Integer, ArrayList<Integer>>(); // Routing Table
    public static HashMap<Integer, ArrayList<Integer>> multicastTable = new HashMap<Integer, ArrayList<Integer>>(); // Multicast Table
    static String[] last_output_final_index; // Controller reads the output file and is written by node and we will save the size of the output_node (size of list )

    // To read the configuration file and make the input and output files for each node
    public static void readConfig() {
        List<String> list = Utility.readFile("config.txt");


        createConnectionTable(list);

        Object[] k = connectionTable.keySet().toArray(); // to know abt the node numbers and to name input and output file accordinagly
        for (int i = 0; i < connectionTable.size(); i++) {
            Utility.writeFile("output_" + ((Integer) k[i]) + ".txt", null); // Creating an input file for each available node in the network
            for (int j = 0; j < connectionTable.get((Integer) k[i]).size(); j++) {
                Utility.writeFile("input_" + connectionTable.get((Integer) k[i]).get(j) + ".txt", null);
            }
        }


    }

    public static void createConnectionTable(List configList) {
        ListIterator<String> listI = configList.listIterator();
        while (listI.hasNext()) {
            String link[] = listI.next().split(" ");

            if (connectionTable.containsKey(Integer.parseInt(link[0]))) {
                connectionTable.get(Integer.parseInt(link[0])).add(Integer.parseInt(link[1]));

            } else {
                ArrayList<Integer> k = new ArrayList<Integer>();
                connectionTable.put(Integer.parseInt(link[0]), k);
                k.add(Integer.parseInt(link[1]));
            }

        }
    }
// Shruti starts 	

    public static void sendMessage(String message, int forwardtoneighbours) {
        String s = message.split(" ")[0];
        if (s.equals("HELLO")) {

            forward_hello(message);

        } else if (s.equals("DATAMESSAGE")) {
        } else if (s.equals("JOINMESSAGE")) {
            joinNodeMessage(message, forwardtoneighbours);
        } else if (s.equals("LINKSTATEMESSAGE")) {
            forward_LinkState_Message(message, forwardtoneighbours);
        } else if (s.equals("REFRESHMESSAGE")) {
        }
        else if(s.equals("DATA"))
        {
            forward_Data(message, forwardtoneighbours);
        }




    }

    //This function is to read all the output files from the last updated pointer which is in last_output_final_index
    public static void readAllFiles() {

        Object[] k = connectionTable.keySet().toArray();

        for (int i = 0; i < connectionTable.size(); i++) {


            List<String> list = Utility.readFile("output_" + ((Integer) k[i]) + ".txt"); // Reading the output file for each node
           
            int last_index_position = 0;
            for (int p = 0; p < last_output_final_index.length; p++) {
                if (last_output_final_index[p].split(",")[0].equals(k[i].toString())) {
                    last_index_position = p;
                    break;
                }
            }
            if (list != null && list.size() > 0
                    && list.size() > Integer.parseInt(last_output_final_index[last_index_position].split(",")[1])) {

                // System.out.println(list.size());
//                System.out.println("last_output_final_index[(Integer.parseInt(k[i].toString()))]"
                //              +  last_output_final_index[(Integer.parseInt(k[i].toString()))]);

                List<String> newlist = list.subList(Integer.parseInt(
                        last_output_final_index[last_index_position].split(",")[1]), list.size());

                last_output_final_index[last_index_position] = (k[i].toString()) + "," + String.valueOf(list.size());
                //    System.out.println("New last_output_final_index[(Integer.parseInt(k[i].toString()))]" +  last_output_final_index[(Integer.parseInt(k[i].toString()))]);


                for (int key = 0; key < newlist.size(); key++) {
                    //    System.out.println("newlist size " + newlist.size());

                    //  System.out.println("newlist.get(key)).toString()" + newlist.get(key));
                    sendMessage((newlist.get(key)).toString(), (Integer) k[i]);

                }
                //shruti
//                     ArrayList<Integer> neighborList = connectionTable.get((Integer) k[i]);
//                     //writing in neigbors input files
//
//                       String listString = " ";
//                         for(int m = 0 ; m < newlist.size(); m ++)
//                         {
//                           listString  += newlist;
//                              listString += " ";
//
//                  //  System.out.println("Sowjanya :::::::::::::" + s[]);
//                         }
//                     for (int J = 0; J< neighborList.size(); J++)
//                      {
//
//                         Utility.writeFile("input_" + J + ".txt", listString);
//
//                     }
            }
        }
    }

    public static void joinNodeMessage(String message, int nodeID) {

        ArrayList<Integer> outgoingNeighborList = connectionTable.get(nodeID);
        //writing in neigbors input files
        for (int i = 0; i < outgoingNeighborList.size(); i++) {
            int j = outgoingNeighborList.get(i);

            Utility.writeFile("input_" + j + ".txt", message);
        }
    }

    

    //message will come like HELLO 1 , split it from space get 1 and chk the connectiontable to chk the outgoing neighbours
    // and then retrieve them in the arraylist of neighbour list and write hello message wo the corresponding input files for the
    // neighbours
    public static void forward_hello(String message) {

        ArrayList<Integer> outgoingNeighborList = connectionTable.get(Integer.parseInt(message.split(" ")[1]));
       //writing in neigbors input files
        for (int i = 0; i < outgoingNeighborList.size(); i++) {
            int j = outgoingNeighborList.get(i);

            Utility.writeFile("input_" + j + ".txt", message);
        }


    }
    public static void forward_LinkState_Message(String message, int nodeId) {

        ArrayList<Integer> neighborList = connectionTable.get(nodeId);
        //writing in neigbors input files
        for (int i = 0; i < neighborList.size(); i++) {
            int j = neighborList.get(i);
            Utility.writeFile("input_" + j + ".txt", message);
        }
    }

    public static void main(String[] args) {
        try {
            //intialization
            readConfig();
            initialze_function();

            while (true) {

                readAllFiles();
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO Auto-generated method stub

    }

    private static void initialze_function() {
        last_output_final_index = new String[connectionTable.size()];  // Controller reads the output file and is written by node and we will save the size of the output_node (size of list )
        for (int i = 0; i < last_output_final_index.length; i++) {
            last_output_final_index[i] = connectionTable.keySet().toArray()[i] + ",0";
        }
    }

    private static void forward_Data(String message, int forwardtoneighbours) {
        ArrayList<Integer> neighborList = connectionTable.get(forwardtoneighbours);
        for (int i = 0; i < neighborList.size(); i++) {
            int j = neighborList.get(i);
            Utility.writeFile("input_" + j + ".txt", message);
        }
    }
    //Shruti ends
}
