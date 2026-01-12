import flows.IsCritical;
import graphs.Graph;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class AFlowCriticalGraphs {
    @Test
    public void two_flow(){
        Graph g = new Graph(2);
        g.addEdge(0,1);

        boolean res = IsCritical.is_A_flow_critical(g,2);

        assertTrue(res);
    }

    @Test
    public void three_flow(){
        Graph g = new Graph(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);

        boolean res = IsCritical.is_A_flow_critical(g,3);

        assertTrue(res);
    }


    @Test
    public void petersen(){
        Graph petersen = new Graph(10);

        petersen.addEdge(0, 1);
        petersen.addEdge(1, 2);
        petersen.addEdge(2, 3);
        petersen.addEdge(3, 4);
        petersen.addEdge(4, 0);

        petersen.addEdge(5, 7);
        petersen.addEdge(7, 9);
        petersen.addEdge(9, 6);
        petersen.addEdge(6, 8);
        petersen.addEdge(8, 5);

        petersen.addEdge(0, 5);
        petersen.addEdge(1, 6);
        petersen.addEdge(2, 7);
        petersen.addEdge(3, 8);
        petersen.addEdge(4, 9);

        boolean res = IsCritical.is_A_flow_critical(petersen,4);
        assertTrue(res);
    }

}
