/**
 * Created by laurynas on 3/3/17.
 */

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Network {
    public HashMap<String, Node> all_nodes = new HashMap<>();

    public Network(String file_directory) {
        try {
            Scanner in = new Scanner(new FileReader(file_directory));
            while (in.hasNext()) {
                String line = in.nextLine();
                String[] info = line.split(" \\| ");
                String node_ID = info[0];

                Node source;
                if (!all_nodes.keySet().contains(node_ID)) {
                    source = new Node(node_ID);
                    all_nodes.put(node_ID, source);
                } else {
                    source = all_nodes.get(node_ID);
                }
                ArrayList<Link> links_to_neighbours = getLinks(info[1], source);
                source.setNeighbours(links_to_neighbours);
                source.init_routing_table();
            }

        } catch (Exception e) {
            System.out.print("Something went wrong with file processing!! " + e);
        }


    }

    public void do_exchange(){
        ArrayList<Node> all_nodes_in_array = new ArrayList<>(all_nodes.values());
        for (Node receiver_node: all_nodes_in_array){
            for (Node sender_node : all_nodes_in_array){

                if (receiver_node != sender_node){
                    ArrayList<Row> senders_routing_table = sender_node.getRoutingTable();
                    receiver_node.receive_updates(senders_routing_table, sender_node);
                }

            }
        }
    }

    public String combine_all_routing_tables(){
        StringBuffer table_set = new StringBuffer();
        for (Node node: all_nodes.values()){
            table_set.append(node.getRoutingTableString()+"\n");
            table_set.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        }
        return table_set.toString();
    }

    private ArrayList<Link> getLinks(String info, Node source) {
        ArrayList<Link> outgoing_links = new ArrayList<>();
        for (String connection : info.split(", ")) {
            String destination_ID = connection.split(":")[0];
            int cost = Integer.parseInt(connection.split(":")[1]);

            Node destination;
            if (!all_nodes.keySet().contains(destination_ID)) {
                destination = new Node(destination_ID);
                all_nodes.put(destination_ID, destination);
            } else {
                destination = all_nodes.get(destination_ID);
            }

            Link new_link = new Link(source, destination, cost);
            outgoing_links.add(new_link);
        }
        return outgoing_links;
    }
}