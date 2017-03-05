import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by laurynas on 3/3/17.
 */
public class Node {

    private final String ID;
    public HashMap<Node, Link> neighbours;
    public ArrayList<ArrayList<?>> routing_table = new ArrayList<>();
    public ArrayList<Node> destinations;
    public ArrayList<Link> outgoing_links;


    public Node(String ID) {
        this.ID = ID;
    }

    public void setDestination(Node destination) {
        if (!destinations.contains(destination)){
            destinations.add(destination);
        }
    }

    public void setNeighbours(ArrayList<Link> links_to_neighbours){
        for (Link link: links_to_neighbours){
            neighbours.put(link.getDestination(), link);
        }
    }

    public void setOutgoingLinks(ArrayList<Link> outgoing_links){
        this.outgoing_links = outgoing_links;
    }

    public void setRoutingTable() {
        routing_table.add(destinations);
        routing_table.add(outgoing_links);
    }
    public ArrayList<ArrayList<?>> getRoutingTable(){
        return this.routing_table;
    }

    public ArrayList<Node> getNeighbours(){
        return new ArrayList<>(neighbours.keySet());
    }

    public ArrayList<Node> getActiveNeighbours(){
        ArrayList<Node> active_neighbours = new  ArrayList();
        for (Link link:neighbours.values()){
            if (link.active){
                active_neighbours.add(link.destination);
            }
        }
        return active_neighbours;
    }

    public void receive_updates(ArrayList<ArrayList<?>> received_table){

    }

    public void add_neighbour(Node new_neighbour, int cost) {
        Link new_link = new Link(this, new_neighbour, cost);
        if (!neighbours.keySet().contains(new_neighbour)) {
            neighbours.put(new_neighbour, new_link);
        }
    }


}
