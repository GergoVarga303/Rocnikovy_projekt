package flows;

import graphs.Graph;
import graphs.IdentifyVertices;
import sat.SatEncoder;
import sat.SatSolverWrapper;

public class IsCritical {
    public static boolean is_A_flow_critical(Graph g, int k){
        if(new AFlowCheck(g,k).hasNonZeroFlow()){
            return false;
        }
//        SatEncoder encoder = new SatEncoder(g,k);
//        if(SatSolverWrapper.solveWithSat4j(encoder.getCNF())){
//            return false;
//        }

        for(int u = 0; u < g.getVertexCount()-1; u++){
            for(int v = u+1; v < g.getVertexCount(); v++){
                Graph g2 = IdentifyVertices.identifyVertices(g,u,v);
                if (!(new AFlowCheck(g2,k).hasNonZeroFlow())){
                    return false; //"po identifikovani lubovolnych dvoch vrcholov graf nikde nulovy A-tok mat bude"
                }
            }
        }
        return true;
    }
}
