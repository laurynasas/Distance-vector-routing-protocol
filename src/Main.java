/**
 * Created by laurynas on 3/3/17.
 */
public class Main {
    public static void main(String[] args){
        String dir = "/home/laurynas/workspace/ANC4/src/network_bigger.txt";
        Network network = new Network(dir);
        int iterations = 5;
        boolean linkFail = false;
        boolean linkCostChange = false;

        String sourceFail = "S";
        String targetFail = "E";
        int faileAfterIter = 0;
        linkFail = true;

        String sourceChange = "E";
        String targetChange = "D";
        int new_cost = 10;
        int changeAfterIter = 0;
        linkCostChange = true;


        for (int i=0; i<iterations && !network.isStable();i++){
            network.do_exchange();
            System.out.println(network.isStable());
            if (i ==faileAfterIter && linkFail){
                Node source_node = network.all_nodes.get(sourceFail);
                Node target_node = network.all_nodes.get(targetFail);
                Link link_neigh = source_node.getLinkToNeighbour(target_node);
                link_neigh.disableLink();
                network.makeInstable();
            }

            if (i ==changeAfterIter && linkCostChange){
                Node source_node = network.all_nodes.get(sourceChange);
                Node target_node = network.all_nodes.get(targetChange);
                Link link_neigh = source_node.getLinkToNeighbour(target_node);
                link_neigh.setCost(new_cost);
                source_node.update_routing_table_cost(new_cost,target_node);
                network.makeInstable();
            }
        }
        System.out.println(network.combine_all_routing_tables());

    }
}
