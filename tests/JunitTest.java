package ex1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class JunitTest {
	final double epsilon = 0.001; // how accurate should doubles equation be (assertEquals will check if Math.abs(expected - actual) < epsilon)
	
	@Test
	void NodeSize() {
		weighted_graph g = createSmallGraph();
		g.addNode(0); //duplicate
		g.addNode(0); //duplicate
		g.addNode(1); //duplicate
		g.addNode(1); //duplicate
		assertEquals(5, g.nodeSize());
		g.removeNode(0);
		g.removeNode(0); //removing a node that was already removed
		g.removeNode(1);
		assertEquals(3, g.nodeSize());
	}
	
	@Test
	void EdgeSize() {
		weighted_graph g = createSmallGraph();
		assertEquals(0, g.edgeSize());
		g.connect(0, 1, 2.0);
		g.connect(0, 2, 3.1);
		g.connect(2, 4, 1.4);
		g.connect(4, 2, 2.9); //reversed order but same nodes
		g.connect(0, 0, 5.0); //connecting node to self, should do nothing.
		assertEquals(3, g.edgeSize());
	}
	
	@Test
	void EdgeWeight() {
		weighted_graph g = createSmallGraph();
		g.connect(0, 1, 2.0);
		g.connect(0, 2, 3.1);
		g.connect(2, 4, 1.4);
		assertEquals(1.4, g.getEdge(2, 4));
		g.connect(4, 2, 2.9); //reversed order but same nodes, also updating weight to 2.9
		assertEquals(2.9, g.getEdge(2, 4));
	}
	
	@Test
	void HasEdge() {
		weighted_graph g = createSmallGraph();
		assertFalse(g.hasEdge(0, 1));
		g.connect(0, 1, 1.5);
		assertTrue(g.hasEdge(0, 1));
	}
	
	@Test
	void RemoveEdge() {
		weighted_graph g = createSmallGraph();
		assertFalse(g.hasEdge(0, 1));
		g.connect(0, 1, 1.5);
		assertTrue(g.hasEdge(0, 1));
		g.removeEdge(1, 0);
		assertFalse(g.hasEdge(0, 1));
	}
	
	@Test
	void GetMC() {
		weighted_graph g = createSmallGraph();
		assertEquals(5, g.getMC());
		g.addNode(5);
		g.connect(0, 1, 1.5);
		g.connect(0, 0, 1.7);
		g.connect(0, 2, 1.9);
		g.connect(0, 3, 2.1);
		g.removeEdge(0, 1);
		assertEquals(10, g.getMC());
		g.removeNode(0);
		assertEquals(13, g.getMC());	
	}
	
	//Creates a new 5 nodes graph
	weighted_graph createSmallGraph() {
		weighted_graph g = new WGraph_DS();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		return g;
	}
	
	@Test
	void isConnected1() {
		weighted_graph g = createEmptyGraph(0);
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertTrue(ga.isConnected());
	}
	
	@Test
	void isConnected2() {
		weighted_graph g = createEmptyGraph(1);
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertTrue(ga.isConnected());
	}
	
	@Test
	void isConnected3() {
		weighted_graph g = createEmptyGraph(2);
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertFalse(ga.isConnected());
	}
	
	@Test
	void isConnected4() {
		weighted_graph g = createEmptyGraph(2);
		g.connect(0, 1, 1.0);
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertTrue(ga.isConnected());
	}

	@Test
	void isConnected5() {
		weighted_graph g = createEmptyGraph(2);
		g.connect(0, 0, 1.0);
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertFalse(ga.isConnected());
	}
	
	@Test
	void isConnected6() {
		weighted_graph g = createShortGraph();
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertTrue(ga.isConnected());
		g.removeEdge(6, 5);
		g.removeEdge(6, 0);
		assertFalse(ga.isConnected());
	}
	
	@Test
	void shortestPathDist() {
		weighted_graph g = createShortGraph();
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		assertEquals(2.40, ga.shortestPathDist(0, 5), epsilon);
		g.removeEdge(6, 5);
		assertEquals(36.26, ga.shortestPathDist(0, 5), epsilon);
	}
	
	@Test
	void shortestPath() {
		weighted_graph g = createShortGraph();
		weighted_graph_algorithms ga = new WGraph_Algo();
		ga.init(g);
		List<node_info> sp = ga.shortestPath(0, 5);
        double[] checkTag = {0.0, 1.17, 2.40};
        int[] checkKey = {0, 6, 5};
        int i = 0;
        for(node_info n: sp) {
        	assertEquals(checkTag[i], n.getTag(), epsilon);
        	assertEquals(checkKey[i], n.getKey(), epsilon);
        	i++;
        }
        i=0;
		g.removeEdge(6, 5);
		sp = ga.shortestPath(0, 5);
		double[] checkTag2 = {0.0, 18.17, 29.24, 36.26};
        int[] checkKey2 = {0, 7, 3, 5};
		for(node_info n: sp) {
        	assertEquals(checkTag2[i], n.getTag(), epsilon);
        	assertEquals(checkKey2[i], n.getKey(), epsilon);
        	i++;
        }
		assertEquals(36.26, ga.shortestPathDist(0, 5), epsilon);
	}
	
	private weighted_graph createShortGraph() {
		weighted_graph g = createEmptyGraph(8);
		g.connect(0, 7, 18.17);
		g.connect(0, 6, 1.17);
		g.connect(1, 3, 21.15);
		g.connect(1, 4, 1.30);
		g.connect(1, 5, 14.97);
		g.connect(1, 7, 12.44);
		g.connect(2, 4, 4.36);
		g.connect(2, 7, 16.17);
		g.connect(3, 5, 7.02);
		g.connect(3, 7, 11.07);
		g.connect(5, 6, 1.23);
		g.connect(1, 3, 7.76); //updating existing edge
		return g;
	}

	private weighted_graph createEmptyGraph(int numOfVertices) {
		weighted_graph g = new WGraph_DS();
		for (int i=0; i<numOfVertices; i++) {
			g.addNode(i);
		}
		return g;
	}
}
