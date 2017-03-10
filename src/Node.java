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

    public Node(String ID) {
        this.ID = ID;
        this.did_update = true;
    }

    public String getID() {
        return this.ID;
    }

    //Initialises all routing tables at the beginning
    public void init_routing_table() {
        for (Link link_to_neigh : neighbours.values()) {
            Row new_row = new Row(link_to_neigh.getDestination(), link_to_neigh.getCost(), link_to_neigh, this);
            if (!get_all_destinations().contains(link_to_neigh.getDestination())) {
                this.routing_table.add(new_row);
            }
        }
    }

    // Gets all known destinations of the node
    public ArrayList<Node> get_all_destinations() {
        ArrayList<Node> destinations = new ArrayList<>();
        for (Row row : routing_table) {
            destinations.add(row.getDestination());
        }
        return destinations;
    }

    public void drop_routing_table() {
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

    // initialises all neighbours
    public void setNeighbours(ArrayList<Link> links_to_neighbours) {
        for (Link link : links_to_neighbours) {
            neighbours.put(link.getDestination(), link);
        }
    }

    public ArrayList<Row> getRoutingTable() {
        return this.routing_table;
    }

    // given some received row checks if has the route in local routing table to that destination
    public Row getCorrespondingLocalRow(Row received_row) {
        Node destination = received_row.getDestination();
        for (Row local_row : this.routing_table) {
            if (local_row.getDestination() == destination) {
                return local_row;
            }
        }
        return null;
    }

    // The same as above just gives local row by destination node
    public Row getLocalRow(Node destination) {
        for (Row local_row : this.routing_table) {
            if (local_row.getDestination() == destination) {
                return local_row;
            }
        }
        return null;
    }

    //  Updates the local routing table with cost difference (i.e. when cost is changed)
    public void update_routing_table_cost(int cost_diff, Node destination) {
        for (Row local_row : this.routing_table) {
            if (local_row.outgoing_link.destination == destination) {
                local_row.setCost(local_row.getCost() + cost_diff);
            }
        }
    }

    //  updates routing table if new given row provides smaller cost to that destination
//    or the advertiser is the one who gave the exisiting route (might be changes in cost that should be overriden)
//    or the existing outgoing link to that destination became inactive (failed).
    public void update_routing_table(Row local_row, Row received_row, Node advertiser) {
        did_update = false;
        int cost_to_advertiser = getLocalRow(advertiser).getCost();
        if (received_row.getCost() + cost_to_advertiser < local_row.getCost() || local_row.getOutgoingLink().getDestination() == advertiser || !local_row.getOutgoingLink().active) {
            for (int i = 0; i < routing_table.size(); i++) {
                if (routing_table.get(i) == local_row) {
                    if (received_row.getCost() + cost_to_advertiser != local_row.getCost()) {
                        did_update = true;
                    }
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
            if (received_row.destination == this) {
                continue;
            }
//  Checks if the advertising direction matches the direction were the advertiser learned the route him/herself from
//  This is split horizon criteria
            if (splitHorizon) {
                Node learning_portal;
                Node advertising_destination_was_learned_from = received_row.advertiser;
                Row direction_learning_portal_row = advertiser.getLocalRow(advertising_destination_was_learned_from);

                Row trying_to_send_direction_row = advertiser.getLocalRow(this);
                Node trying_to_send_portal;

                if (direction_learning_portal_row != null && trying_to_send_direction_row != null) {
                    learning_portal = direction_learning_portal_row.getOutgoingLink().getDestination();
                    trying_to_send_portal = trying_to_send_direction_row.getOutgoingLink().getDestination();

                    if (trying_to_send_portal == learning_portal) {
                        continue;
                    }

                }

            }

//  If no split horizon doe the update given that we now the path to advertiser and it's active
            if (local_advertiser_row != null && local_advertiser_row.getOutgoingLink().getStatus()) {
                if (local_row != null) {
                    update_routing_table(local_row, received_row, advertiser);
                } else if (local_row == null) {
//  Add non existing new destination
                    did_update = true;
                    int cost_to_advertiser = getLocalRow(advertiser).getCost();
                    Link outgoing_link_to_advertiser = getLocalRow(advertiser).getOutgoingLink();
                    Row updated_new_row = new Row(received_row.getDestination(), received_row.getCost() + cost_to_advertiser, outgoing_link_to_advertiser, advertiser);
                    this.routing_table.add(updated_new_row);
                }
            }
        }

    }

    // Formats the routin table
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
