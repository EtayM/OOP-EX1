package ex1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WGraph_DS_Test {
	
	weighted_graph g;
	
	//Create a new 5 nodes graph before each test.
	@BeforeEach
	void createSmallGraph() {
		g = new WGraph_DS();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
	}
	
	@Test
	void NodeSize() {
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
		g.connect(0, 1, 2.0);
		g.connect(0, 2, 3.1);
		g.connect(2, 4, 1.4);
		assertEquals(1.4, g.getEdge(2, 4));
		g.connect(4, 2, 2.9); //reversed order but same nodes, also updating weight to 2.9
		assertEquals(2.9, g.getEdge(2, 4));
	}
	
	@Test
	void HasEdge() {
		assertFalse(g.hasEdge(0, 1));
		g.connect(0, 1, 1.5);
		assertTrue(g.hasEdge(0, 1));
	}
	
	@Test
	void RemoveEdge() {
		assertFalse(g.hasEdge(0, 1));
		g.connect(0, 1, 1.5);
		assertTrue(g.hasEdge(0, 1));
		g.removeEdge(1, 0);
		assertFalse(g.hasEdge(0, 1));
	}
	
	@Test
	void GetMC() {
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
}
