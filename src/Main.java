/**
 * Created by laurynas on 3/3/17.
 */
public class Main {
    public static void main(String[] args){
        String dir = "/home/laurynas/workspace/ANC4/src/network_bigger.txt";
        Network network = new Network(dir);
        int iterations = 5;
        String sourceFail = "S";
        String targetFail = "E";
        int faileAfterIter = 0;
        for (int i=0; i<iterations && !network.isStable();i++){
            network.do_exchange();
            System.out.println(network.isStable());
            if (i ==faileAfterIter){
                Node source_node = network.all_nodes.get(sourceFail);
                Node target_node = network.all_nodes.get(targetFail);
                Link link_neigh = source_node.getLinkToNeighbour(target_node);
                link_neigh.disableLink();
                network.makeInstable();
            }
        }
        System.out.println(network.combine_all_routing_tables());

    }
}
