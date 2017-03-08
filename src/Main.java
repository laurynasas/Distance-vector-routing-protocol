/**
 * Created by laurynas on 3/3/17.
 */
public class Main {
    public static void main(String[] args){
        String dir = "/home/laurynas/workspace/ANC4/src/network_bigger.txt";
        Network network = new Network(dir);
        int iterations = 5;

        for (int i=0; i<iterations && !network.isStable();i++){
            network.do_exchange();
            System.out.println(network.isStable());
        }
        System.out.println(network.combine_all_routing_tables());

    }
}
