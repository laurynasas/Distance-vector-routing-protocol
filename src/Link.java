/**
 * Created by laurynas on 3/3/17.
 */
public class Link {
    public boolean active;
    public int cost;
    public Node source;
    public Node destination;

    public Link(Node source, Node destination, int cost) {
        this.source = source;
        this.destination = destination;
        this.active = true;
        this.cost = cost;
    }

    public Node getDestination() {
        return this.destination;
    }

    public void disableLink() {
        this.active = false;
    }

    public void enableLink() {
        this.active = true;
    }


    public boolean getStatus(){
        return this.active;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

}
