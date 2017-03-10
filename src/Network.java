/**
 * Created by laurynas on 3/3/17.
 */

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Network {
    public int maxIters = 10;
    public HashMap<String, Node> all_nodes = new HashMap<>();
    public boolean stable;
    public HashMap<Integer, ArrayList<Link>> failing_links = new HashMap<>();
    public ArrayList<String> rt_printout_nodes = new ArrayList<>();
    public ArrayList<Integer> rt_printout_iter = new ArrayList<>();
    public HashMap<Integer, HashMap<String, String>> best_routes = new HashMap<>();

    public HashMap<Integer, HashMap<Link, Integer>> cost_changes = new HashMap<>();


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

    public Link find_link(Node source, Node target) {
        return source.neighbours.get(target);
    }

    public void add_failing_link(String s, String t, int iteration) {
        Node source = all_nodes.get(s);
        Node target = all_nodes.get(t);
        Link failing_link = find_link(source, target);
        ArrayList<Link> all_links;
        all_links = failing_links.get(iteration);
        if (all_links == null) {
            all_links = new ArrayList<>();
            all_links.add(failing_link);
            failing_links.put(iteration, all_links);
        } else {
            all_links.add(failing_link);
            failing_links.put(iteration, all_links);
        }
    }

    public void add_get_best_routes(String start, String end, int iteration) {
        HashMap<String, String> all_routes;
        all_routes = best_routes.get(iteration);
        if (all_routes == null) {
            all_routes = new HashMap<>();
            all_routes.put(start, end);
            best_routes.put(iteration, all_routes);
        } else {
            all_routes.put(start, end);
            best_routes.put(iteration, all_routes);
        }
    }

    public boolean add_cost_change(String s, String t, int iteration, int new_cost) {
        Node source = all_nodes.get(s);
        Node target = all_nodes.get(t);
        if (source == null || target == null) {
            return false;
        }
        Link changing_link = find_link(source, target);
        if (changing_link == null) {
            return false;
        }
        HashMap<Link, Integer> all_links;
        all_links = cost_changes.get(iteration);
        if (all_links == null) {
            all_links = new HashMap<>();
            all_links.put(changing_link, new_cost);
            cost_changes.put(iteration, all_links);
        } else {
            all_links.put(changing_link, new_cost);
            cost_changes.put(iteration, all_links);
        }
        return true;
    }

    public void set_routing_table_printout(String[] ids, ArrayList<Integer> iter_indeces) {
        rt_printout_nodes = new ArrayList<>(Arrays.asList(ids));
        rt_printout_iter = iter_indeces;
    }


    public void delete_this_node_if_outgoing(Node to_delete) {
        for (Node node : all_nodes.values()) {
            ArrayList<Row> rows_to_delete = new ArrayList<>();
            for (Row associated_node_row : node.getRoutingTable()) {
                if (associated_node_row.getOutgoingLink().destination == to_delete) {
                    rows_to_delete.add(associated_node_row);
                }
            }
            if (rows_to_delete.size() != 0) {
                node.delete_rows_from_rt(rows_to_delete);
                node.init_routing_table();
            }

        }
    }

//    public void send_all_nodes_notification(){
//        for (Node node : all_nodes.values()){
//            node.setCostChanged();
//        }
//    }


    public void perform_exchange(boolean split_horizon) {
        StringBuffer all_tables = new StringBuffer();
        StringBuffer all_best_routes = new StringBuffer();
        for (int i = 0; i < maxIters && !isStable(); i++) {
            do_exchange(split_horizon);
//          Set up failing links
            if (failing_links.get(i) != null) {
                for (Link failing_link : failing_links.get(i)) {
                    failing_link.disableLink();
                    this.makeInstable();
                }
            }
//          Change costs of links
            if (cost_changes.get(i) != null) {
                for (Link changing_links : cost_changes.get(i).keySet()) {
                    int new_cost = cost_changes.get(i).get(changing_links);
                    int cost_diff = new_cost - changing_links.getCost();
                    changing_links.setCost(new_cost);
                    changing_links.getSource().update_routing_table_cost(cost_diff, changing_links.getDestination());
                    changing_links.getSource().drop_routing_table();
                    changing_links.getSource().init_routing_table();
                    this.makeInstable();
                }
            }

            if (rt_printout_iter.contains(i)) {
                all_tables.append("----------ITERATION " + i + " -----------\n" + combine_set_of_routing_tables(rt_printout_nodes) + "\n");
            }

            if (best_routes.get(i) != null) {
                for (String start : best_routes.get(i).keySet()) {
                    all_best_routes.append("----------ITERATION " + i + " -----------\n" + get_best_route(start, best_routes.get(i).get(start)) + "\n");
                }
            }
        }
        System.out.println("=============ROUTING TABLES=====================\n");
        System.out.println(all_tables.toString());
        System.out.println("=============BEST ROUTES========================\n");
        System.out.println(all_best_routes.toString());
    }


    public String get_best_route(String start, String end) {
        Node start_node = all_nodes.get(start);
        Node end_node = all_nodes.get(end);
        StringBuffer path = new StringBuffer();
        Node intermediate_node = start_node;
        while (intermediate_node != end_node) {
            path.append(intermediate_node.getID() + " -> ");
            intermediate_node = intermediate_node.getLocalRow(end_node).getOutgoingLink().getDestination();
        }
        path.append(intermediate_node.getID());
        return path.toString();
    }


    public void do_exchange(boolean splitHorizon) {
        ArrayList<Node> all_nodes_in_array = new ArrayList<>(all_nodes.values());
        for (Node receiver_node : all_nodes_in_array) {
            for (Node sender_node : all_nodes_in_array) {
                if (receiver_node != sender_node) {
                    receiver_node.set_did_update();
                    ArrayList<Row> senders_routing_table = sender_node.getRoutingTable();
                    receiver_node.receive_updates(senders_routing_table, sender_node, splitHorizon);
                }

            }
        }
        this.stable = checkStability();
    }

    public boolean checkStability() {
        ArrayList<Node> all_nodes_in_array = new ArrayList<>(all_nodes.values());
        for (Node node : all_nodes_in_array) {
            if (node.did_update_routing_table()) {
                return false;
            }
        }
        return true;
    }

    public boolean isStable() {
        return this.stable;
    }

    public void makeInstable() {
        this.stable = false;
    }

    public String combine_all_routing_tables() {
        StringBuffer table_set = new StringBuffer();
        for (Node node : all_nodes.values()) {
            table_set.append(node.getRoutingTableString() + "\n");
            table_set.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        }
        return table_set.toString();
    }

    public String combine_set_of_routing_tables(ArrayList<String> set) {
        StringBuffer table_set = new StringBuffer();
        for (String nodeID : set) {
            Node node = all_nodes.get(nodeID);
            table_set.append(node.getRoutingTableString() + "\n");
            table_set.append("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
        }
        return table_set.toString();
    }

    public void setMaxIters(int max) {
        this.maxIters = max;
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
            if (source != destination) {
                outgoing_links.add(new_link);
            }
        }
        return outgoing_links;
    }
}
