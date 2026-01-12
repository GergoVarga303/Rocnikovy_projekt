package flows;

import graphs.Graph;

public class Demo {
    public static void main(String[] args) {
        Graph k4 = new Graph(4);
        k4.addEdge(0, 1);
        k4.addEdge(0, 2);
        k4.addEdge(0, 3);
        k4.addEdge(1, 2);
        k4.addEdge(1, 3);
        k4.addEdge(2, 3);

        boolean critical = IsCritical.is_A_flow_critical(k4, 3);
        System.out.println("Is K4 3-flow critical? " + critical);
    }
}
