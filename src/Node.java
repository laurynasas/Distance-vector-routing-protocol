import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by laurynas on 3/3/17.
 */
public class Node {

    private final String ID;
    public HashMap<Node, Link> neighbours = new HashMap<>();
    public ArrayList<Row> routing_table = new ArrayList<>();
    public boolean did_update;
//    public boolean did_cost_change;

    public Node(String ID) {
        this.ID = ID;
        this.did_update = true;
//        this.did_cost_change = false;
    }

    public String getID() {
        return this.ID;
    }


    public void init_routing_table() {
        for (Link link_to_neigh : neighbours.values()) {
            Row new_row = new Row(link_to_neigh.getDestination(), link_to_neigh.getCost(), link_to_neigh, this);
            if (! get_all_destinations().contains(link_to_neigh.getDestination())) {
                this.routing_table.add(new_row);
            }
        }
    }

    public ArrayList<Node> get_all_destinations(){
        ArrayList<Node> destinations = new ArrayList<>();
        for (Row row:routing_table){
            destinations.add(row.getDestination());
        }
        return destinations;
    }

    public void delete_rows_from_rt(ArrayList<Row> rows_to_delete){
        routing_table.removeAll(rows_to_delete);
    }

    public void drop_routing_table(){
        this.routing_table = new ArrayList<>();
    }

    public Link getLinkToNeighbour(Node destination) {
        for (Link link : neighbours.values()) {
            if (destination == link.destination) {
                return link;
            }
        }
        return null;
    }

    public void setNeighbours(ArrayList<Link> links_to_neighbours) {
        for (Link link : links_to_neighbours) {
            neighbours.put(link.getDestination(), link);
        }
    }

    public ArrayList<Row> getRoutingTable() {
        return this.routing_table;
    }


    public Row getCorrespondingLocalRow(Row received_row) {
        Node destination = received_row.getDestination();
        for (Row local_row : this.routing_table) {
            if (local_row.getDestination() == destination) {
                return local_row;
            }
        }
        return null;
    }

    public Row getLocalRow(Node destination) {
        for (Row local_row : this.routing_table) {
            if (local_row.getDestination() == destination) {
                return local_row;
            }
        }
        return null;
    }
    public void update_routing_table_cost(int cost_diff, Node destination){
        for (Row local_row : this.routing_table) {
            if (local_row.outgoing_link.destination == destination) {
//                int cost_diff = new_cost-local_row.getCost();
                System.out.println("before>"+local_row.toString());

                local_row.setCost(local_row.getCost() + cost_diff);
                System.out.println("after >"+local_row.toString());
            }
        }
    }

//    public void setCostChanged(){
//        did_cost_change = true;
//    }

    public void update_routing_table(Row local_row, Row received_row, Node advertiser) {
        did_update = false;
        String name = this.getID();
        int cost_to_advertiser = getLocalRow(advertiser).getCost();
        int total_new_cost = received_row.getCost() + cost_to_advertiser;
        if (received_row.getCost() + cost_to_advertiser < local_row.getCost() || local_row.getOutgoingLink().getDestination() == advertiser) {
            for (int i = 0; i < routing_table.size(); i++) {
                if (routing_table.get(i) == local_row) {
//                    did_cost_change = false;
                    did_update = true;
                    Row updated_row = routing_table.get(i);
                    Link outgoing_link_to_advertiser = getLocalRow(advertiser).getOutgoingLink();
                    updated_row.setCost(received_row.getCost() + cost_to_advertiser);
                    updated_row.setOutgoingLink(outgoing_link_to_advertiser);
                    routing_table.set(i, updated_row);
                }
            }
        }
    }

    public void receive_updates(ArrayList<Row> received_routing_table, Node advertiser, boolean splitHorizon) {
        for (Row received_row : received_routing_table) {
            Row local_row = getCorrespondingLocalRow(received_row);
            Row local_advertiser_row = getLocalRow(advertiser);
            String current_node = this.getID();
            String advertiser_name = advertiser.getID();
            String path = advertiser_name+" -> "+received_row.getDestination().getID() + " for " + received_row.getCost();
            if (splitHorizon){
                Node portal_to_advertiser;
                if (local_advertiser_row != null && received_row.advertiser == this){
                    portal_to_advertiser = getLocalRow(received_row.advertiser).getOutgoingLink().getDestination();
                } else{
                    continue;
                }

                if (portal_to_advertiser == this || received_row.destination  == this){
                    continue;
                }
            }

            if (local_advertiser_row != null && local_advertiser_row.getOutgoingLink().getStatus()) {
                if (local_row != null) {
                    update_routing_table(local_row, received_row, advertiser);
                } else if (local_row == null) {
//              Add non existing new destination
                    did_update = true;
                    int cost_to_advertiser = getLocalRow(advertiser).getCost();
                    Link outgoing_link_to_advertiser = getLocalRow(advertiser).getOutgoingLink();
                    Row updated_new_row = new Row(received_row.getDestination(), received_row.getCost() + cost_to_advertiser, outgoing_link_to_advertiser, advertiser);
                    this.routing_table.add(updated_new_row);
                }
            }
        }

    }

    public String getRoutingTableString() {
        StringBuffer table = new StringBuffer();
        table.append("             NODE " + this.ID + " ROUTING TABLE             \n");
        table.append("| DESTINATION | COST | OUTGOING LINK | IS OUTGOING LINK ACTIVE | ADVERTISED BY\n");
        for (Row row : this.routing_table) {
            if (!row.outgoing_link.active) {
                continue;
            }
            table.append(row.toString() + "\n");
            table.append("---------------------------------\n");
        }
        return table.toString();
    }

    public boolean did_update_routing_table() {
        return this.did_update;
    }

    public void set_did_update() {
        this.did_update = false;
    }


}
