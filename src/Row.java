/**
 * Created by laurynas on 3/3/17.
 */
public class Row {
    public Node destination;
    public int cost;
    public Link outgoing_link;
    public Node advertiser;

    public Row(Node destination, int cost, Link outgoing_link, Node advertiser){
        this.destination = destination;
        this.cost = cost;
        this.outgoing_link = outgoing_link;
        this.advertiser = advertiser;
    }

    public void setCost(int cost){
        this.cost = cost;
    }

    public void setOutgoingLink(Link outgoing_link){
        this.outgoing_link = outgoing_link;
    }

    public Node getDestination(){
        return this.destination;
    }

    public int getCost(){
        return this.cost;
    }

    public Link getOutgoingLink(){
        return this.outgoing_link;
    }

    @Override
    public String toString(){
        return "| "+destination.getID()+" | "+cost+" | via: " + outgoing_link.destination.getID()+" | "+outgoing_link.active + " | by "+advertiser.getID();
    }
}