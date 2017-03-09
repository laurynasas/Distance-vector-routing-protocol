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

        String sourceFail = "A";
        String targetFail = "B";
        int faileAfterIter = 0;
        linkFail = false;

        String sourceChange = "D";
        String targetChange = "A";
        int new_cost = 10;
        int changeAfterIter = 0;
        linkCostChange = true;

        boolean splitHorizon = false;

        for (int i=0; i<iterations && !network.isStable();i++){
            network.do_exchange(splitHorizon);
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
                int cost_diff = new_cost - link_neigh.getCost();
                link_neigh.setCost(new_cost);
                source_node.update_routing_table_cost(cost_diff,target_node);
                source_node.drop_routing_table();
                source_node.init_routing_table();
//                source_node.tell_neighbours_to_delete_me_from_rt();
                network.delete_this_node_if_outgoing(source_node);
//                target_node.update_routing_table_cost(new_cost,);
                network.makeInstable();
            }
            System.out.println("----------#########----after iter "+i+"------###################--");
            System.out.println(network.combine_all_routing_tables());
        }
//        System.out.println(network.combine_all_routing_tables());
        System.out.println(network.get_best_route("S","A"));
    }
}
