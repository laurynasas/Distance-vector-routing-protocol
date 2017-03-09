import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by laurynas on 3/9/17.
 */
public class CommandLineInterface {


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide appropriate arguments");
            System.exit(0);
        }
        boolean split_horizon = false;

        Scanner read_input = new Scanner(System.in);
        String file_directory = args[0];

        Network network = new Network(file_directory);

        while (true) {
            String instruction = read_input.nextLine();
            String[] instructions = instruction.split(" ");


            if (instructions.length > 1) {
                System.out.println("Please provide one argument at the time");
                continue;
            } else if (instructions.length == 0) {
                System.out.println("Please provide arguments");
                continue;
            }

            instruction = instructions[0];

            switch (instruction) {
                case "set_max_iterations":
                    System.out.println("Please provide number of max iterations: ");
                    String max_iter = read_input.next();
                    int max;
                    try {
                        max = Integer.parseInt(max_iter);
                    } catch (Exception e) {
                        System.out.println("Please provide a number");
                        continue;
                    }
                    network.setMaxIters(max);
                    System.out.println("Succesfully set maximum iterations");
                    break;
                case "enable_split_horizon":
                    split_horizon = true;
                    System.out.println("Split horizon is successfully enabled");
                    break;
                case "change_link_cost":
                    System.out.println("Please provide source node ID: ");
                    String source_change = read_input.next();
                    System.out.println("Please provide target node ID: ");
                    String target_change = read_input.next();
                    System.out.println("Please enter new link cost: ");
                    String new_cost = read_input.next();
                    int new_cost_int;
                    try {
                        new_cost_int = Integer.parseInt(new_cost);
                    } catch (Exception e) {
                        System.out.println("Please provide a number");
                        continue;
                    }
                    System.out.println("Please enter after which iteration should it change cost");
                    String change_after = read_input.next();
                    int change_cost_after;
                    try {
                        change_cost_after = Integer.parseInt(change_after);
                    } catch (Exception e) {
                        System.out.println("Please provide a number");
                        continue;
                    }
                    boolean status = network.add_cost_change(source_change, target_change, change_cost_after, new_cost_int);
                    if (status) {
                        System.out.println("Succesfully added cost change");
                    } else{
                        System.out.println("The link does not exist or node IDS are incorrect!");
                    }
                    break;
                case "make_link_fail":
                    System.out.println("Please provide source node ID: ");
                    String source_fail = read_input.next();
                    System.out.println("Please provide target node ID: ");
                    String target_fail = read_input.next();
                    System.out.println("Please enter after which iteration should it fail");
                    String fail_after = read_input.next();
                    int fail_after_iter;
                    try {
                        fail_after_iter = Integer.parseInt(fail_after);
                    } catch (Exception e) {
                        System.out.println("Please provide a number");
                        continue;
                    }
                    network.add_failing_link(source_fail, target_fail, fail_after_iter);
                    System.out.println("Succesfully added failing link");
                    break;
                case "print_best_route":
                    System.out.println("Please provide start node ID: ");
                    String start = read_input.next();
                    System.out.println("Please provide end node ID: ");
                    String end = read_input.next();
                    System.out.println("Please enter after which iteration");
                    String print_iter = read_input.next();
                    int best_route_after;
                    try {
                        best_route_after = Integer.parseInt(print_iter);
                    } catch (Exception e) {
                        System.out.println("Please provide a number");
                        continue;
                    }
                    network.add_get_best_routes(start, end, best_route_after);
                    System.out.println("Succesfully added route request");
                    break;
                case "get_routing_tables":
                    System.out.println("Please provide set of comma separated node ID's");
                    String list = read_input.next();
                    String[] ids = list.split(",");
                    for (String id : ids) {
                        if (!network.all_nodes.keySet().contains(id)) {
                            System.out.println(id + " - the id does not exist in the network!");
                            continue;
                        }
                    }
                    System.out.println("Please provide set of comma separated iteration numbers");
                    String numbers_str = read_input.next();
                    ArrayList<Integer> iter_rt_numbers = new ArrayList<>();
                    for (String number : numbers_str.split(",")) {
                        iter_rt_numbers.add(Integer.parseInt(number));
                    }
                    network.set_routing_table_printout(ids, iter_rt_numbers);
                    System.out.println("Succesfully set routing tables printout");
                    break;
                case "exchange":
                    System.out.println("Will do information exchange with preset params ");
                    network.perform_exchange(split_horizon);

                    break;
                case "clean":
                    System.out.println("Will be setting all params to default");
                    network = new Network(file_directory);
                    break;
            }


        }

    }
}
