package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		PartialTreeList L = new PartialTreeList();
		
		for (Vertex v : graph.vertices){
			PartialTree T = new PartialTree(v);
			for (Vertex.Neighbor ptr = v.neighbors; ptr!= null; ptr=ptr.next){
				T.getArcs().insert(new PartialTree.Arc(v,ptr.vertex,ptr.weight));
			}
			L.append(T);
		}
		
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		ArrayList<PartialTree.Arc> fin = new ArrayList<PartialTree.Arc>();
		while (ptlist.size()>1){
			PartialTree PTX = ptlist.remove();
			PartialTree.Arc a = PTX.getArcs().deleteMin();
			Vertex v2 = a.v2;
			while (containsVertex(PTX, v2)){
//				System.out.println(PTX.getArcs().toString());
				a = PTX.getArcs().deleteMin();
				v2 = a.v2;
			}
			fin.add(a);
			PartialTree PTY = ptlist.removeTreeContaining(v2);
			PTX.merge(PTY);
			ptlist.append(PTX);
		}

		return fin;
	}
	
    private static boolean containsVertex(PartialTree pt, Vertex v){
    	
    	Vertex ptr = pt.getRoot();
    	if (ptr.equals(v))
    		return true;
    	while (!v.equals(v.parent)){
    		if (v.parent.equals(ptr))
    			return true;
    		v = v.parent;
    	}
    	
    	return false;
    }
}
    