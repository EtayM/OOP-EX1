package ex1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WGraph_DS implements weighted_graph, java.io.Serializable {
	
	private int numOfEdges = 0;
	private int modeCount = 0; //represents number of changes.
	private HashMap<Integer, node_info> nodes;
	
	public WGraph_DS() {
		this.numOfEdges = 0;
		this.nodes = new HashMap<Integer, node_info>();
	}
	
	public WGraph_DS(WGraph_DS g) {
		this.numOfEdges = g.edgeSize();
		this.modeCount = g.getMC();
	}
	
	@Override
	public node_info getNode(int key) {
		return this.nodes.get(key);
	}

	@Override
	public boolean hasEdge(int node1, int node2) {
		if (node1 == node2) return true;
		NodeInfo n = (NodeInfo) this.nodes.get(node1);
		return n.hasNi(node2);
	}

	@Override
	public double getEdge(int node1, int node2) {
		NodeInfo n = (NodeInfo) this.nodes.get(node1);
		return n.getWeight(node2); //getWeight function returns the weight / -1 if the node is not a neighbor. 
	}

	@Override
	public void addNode(int key) {
		if (!this.nodes.containsKey(key)) { //check if node already exists in the graph, if not continue
			NodeInfo n = new NodeInfo(key); //create a new node with the given key
			this.nodes.put(key, n); //add the new node to the graph
			this.modeCount++;
		}
	}

	@Override
	public void connect(int node1, int node2, double w) {
		if (node1 == node2) return; //do nothing if trying to connect a node to itself
		if (w < 0) return; //do nothing if weight is less than 0
		NodeInfo getNode1 = (NodeInfo)this.nodes.get(node1);
		NodeInfo getNode2 = (NodeInfo)this.nodes.get(node2);
		numOfEdges += getNode1.hasNi(node2) ? 0 : 1; // add 1 to numOfEdges if there is no connection yet. 
		getNode1.connectEdge(node2, w); //connect node 1 to node 2 // update weight
		getNode2.connectEdge(node1, w); //connect node 2 to node 1 // update weight
		modeCount++;
	}

	@Override
	public Collection<node_info> getV() {
		return this.nodes.values();
	}

	@Override
	public Collection<node_info> getV(int node_id) {
		NodeInfo n = (NodeInfo)this.nodes.get(node_id);
		HashMap<Integer,Double> neighborsHashMap = n.neighborNodes; //get the neighbors HashMap (we will use it's keys to make a Collection that holds the actual Nodes that corresponds to those keys.)
		Collection<node_info> neighborsArrayList = new ArrayList<node_info>(neighborsHashMap.size()); //init the collection that holds the Neighbors Nodes
		for (Map.Entry<Integer, Double> entry : neighborsHashMap.entrySet()) { //loop through all HashMap
			neighborsArrayList.add(this.nodes.get(entry.getKey())); //add the actual Node to the Collection
		}
		return neighborsArrayList;
	}

	@Override
	public node_info removeNode(int key) {
		if (!this.nodes.containsKey(key)) return null; // return null if the node we wish to remove does not exist in the graph
			
		this.modeCount++;
		NodeInfo nodeToRemove = (NodeInfo)this.nodes.get(key);
		this.numOfEdges -= nodeToRemove.neighborNodes.size();
		Iterator<Map.Entry<Integer, Double>> itr = nodeToRemove.neighborNodes.entrySet().iterator(); //I got help from StackOverFlow - https://stackoverflow.com/questions/26494197/java-util-concurrentmodificationexception-when-removing-elements-from-a-hashmap
		while (itr.hasNext()) { //loop through all neighbor nodes and remove the edge
			Map.Entry<Integer, Double> entry = itr.next();
			NodeInfo neighnorNode = (NodeInfo)this.nodes.get(entry.getKey());
			
			neighnorNode.removeNode(nodeToRemove); //Remove the edge from the neighbor
			itr.remove(); //Remove the edge from the node that we wish to remove
			this.modeCount++;
		}
		this.nodes.remove(key); //remove the node from the graph
		return nodeToRemove;
	}

	@Override
	public void removeEdge(int node1, int node2) {
		if (this.nodes.containsKey(node1) && this.nodes.containsKey(node2)) {
			NodeInfo getNode1 = (NodeInfo)this.nodes.get(node1); //get node1
			NodeInfo getNode2 = (NodeInfo)this.nodes.get(node2); //get node2
			if (getNode1.hasNi(node2)) {
				this.modeCount++;
				getNode1.removeNode(getNode2); //remove the edge from node1
				getNode2.removeNode(getNode1); //remove the edge from node2
				this.numOfEdges--;
			}
		}
	}

	@Override
	public int nodeSize() {
		return this.nodes.size();
	}

	@Override
	public int edgeSize() {
		return this.numOfEdges;
	}

	@Override
	public int getMC() {
		return this.modeCount;
	}
	
	public weighted_graph deepCopy() {
		WGraph_DS copyGraph = new WGraph_DS(this); //create a new graph with the original graph data (only primitives)
		HashMap<Integer, node_info> copyMap = new HashMap<Integer, node_info>(); //create a new nodes HashMap for the new graph
		for (Map.Entry<Integer, node_info> node : this.nodes.entrySet()) { //loop through all nodes in the original graph
			copyMap.put(node.getKey(), new NodeInfo((NodeInfo)node.getValue())); //makes a duplicate of the original HashMap
		}
		copyGraph.nodes = copyMap; //set the new graph nodes to the new HashMap we made.
		return copyGraph;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true; //both pointers pointing to the same address in memory
		if (o == null || this.getClass() !=o.getClass()) return false; //if o is null it is surely not equal to "this" because "this" is defined. also if they are made of different classes then they obviously not equal.
		
		WGraph_DS other = (WGraph_DS) o;
		
		for (node_info node : this.nodes.values()) {
			if (!other.nodes.containsKey(node.getKey())) return false;
		}
		
		for (node_info node : other.nodes.values()) {
			if (!this.nodes.containsKey(node.getKey())) return false;
		}
		return true;
	}
	
	class NodeInfo implements node_info, Comparable<NodeInfo>, java.io.Serializable {
		
		private int key; //represents the node ID
		private String remark; //represents color for Dijkstra (BLACK for a node that Dijkstra already checked, WHITE otherwise.)
		private double tag; // represents the current distance (weight) from the source node to this node. (Double.MAX_VALUE means there's no connection from source to this node)
		private int parent; //represents parent node key for Dijkstra (-1 for root)
		
		private HashMap<Integer, Double> neighborNodes; //represents neighborNodes and their weights. key is the node key and value is the weight.
		
		public NodeInfo(int key) {
			this.key = key;
			this.remark = "WHITE";
			this.tag = Double.MAX_VALUE;
			this.parent = -1;
			this.neighborNodes = new HashMap<Integer, Double>();
		}
		

		public NodeInfo(NodeInfo node) {
			this.key = node.getKey();
			this.remark = node.getInfo();
			this.tag = node.getTag();
			this.parent = node.getParent();
			this.neighborNodes = (HashMap<Integer, Double>)node.neighborNodes.clone();
		}

		@Override
		public int getKey() {
			return this.key;
		}

		@Override
		public String getInfo() {
			return this.remark;
		}

		@Override
		public void setInfo(String s) {
			this.remark = s;
		}

		@Override
		public double getTag() {
			return this.tag;
		}

		@Override
		public void setTag(double t) {
			this.tag = t;
		}
		
		public boolean hasNi(int node2) {
			return this.neighborNodes.containsKey(node2) ? true : false;
		}
		
		public HashMap<Integer, Double> getNi() {
			return this.neighborNodes;
		}
		
		public double getWeight(int key) {
			if (!neighborNodes.containsKey(key)) return -1; //return -1 if the node is not a neighbor.
			return neighborNodes.get(key); //return the weight.
		}
		
		public void connectEdge(int key, double weight) {
			this.neighborNodes.put(key, weight);
		}
		
		public void removeNode(NodeInfo node) {
			this.neighborNodes.remove(node.getKey());
		}


		public int getParent() {
			return this.parent;
		}


		public void setParent(int parent) {
			this.parent = parent;
		}


		@Override
		public int compareTo(NodeInfo n) {
			return this.tag < n.getTag() ? -1 : this.tag > n.getTag() ? 1 : 0;
		}
	}


}
