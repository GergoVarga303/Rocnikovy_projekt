import flows.IsCritical;
import graphs.Graph;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
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
    public void petersen(){
        Graph petersen = new Graph(10);

// Vonkajší päťuholník (vrcholy 0-4)
        petersen.addEdge(0, 1);
        petersen.addEdge(1, 2);
        petersen.addEdge(2, 3);
        petersen.addEdge(3, 4);
        petersen.addEdge(4, 0);

// Vnútorná hviezda (vrcholy 5-9)
        petersen.addEdge(5, 7);
        petersen.addEdge(7, 9);
        petersen.addEdge(9, 6);
        petersen.addEdge(6, 8);
        petersen.addEdge(8, 5);

// Prepojenie vonkajšieho a vnútorného cyklu (zodpovedajúce vrcholy)
        petersen.addEdge(0, 5);
        petersen.addEdge(1, 6);
        petersen.addEdge(2, 7);
        petersen.addEdge(3, 8);
        petersen.addEdge(4, 9);

        boolean res = IsCritical.is_A_flow_critical(petersen,4);
        assertTrue(res);
    }

}
