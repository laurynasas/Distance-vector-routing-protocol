/**
 * Created by laurynas on 3/3/17.
 */
public class Row {
    public Node destination;
    public int distance;
    public Link outgoing_link;

    public Row(Node destination, int distance, Link outgoing_link){
        this.destination = destination;
        this.distance = distance;
        this.outgoing_link = outgoing_link;
    }
}
