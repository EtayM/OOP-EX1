package ex1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import ex1.WGraph_DS.NodeInfo;

public class WGraph_Algo implements weighted_graph_algorithms {

	WGraph_DS graphPointer; //represents a pointer to the original graph.
	
	public WGraph_Algo() {
		this.graphPointer = null;
	}
	
	@Override
	public void init(weighted_graph g) {
		this.graphPointer = (WGraph_DS) g;
	}

	@Override
	public weighted_graph getGraph() {
		return this.graphPointer;
	}

	@Override
	public weighted_graph copy() {
		return graphPointer.deepCopy(); //perform a deep copy to the graph, so we can use algorithms on it without affecting the original values.
	}

	@Override
	public boolean isConnected() {
		int numOfNodes = this.graphPointer.getV().size(); //get number of nodes in the original graph
		if (numOfNodes == 0 || numOfNodes == 1 ) return true; //if the number of nodes is less than 2, the graph is surely connected.
		if (this.graphPointer.edgeSize() < numOfNodes-1) return false; //if the number of edges is less than the number of nodes -1, the graph is surely no connected.
		
		//if we reached here, we can't tell if the graph is connected by the number of edges & number of nodes
		//so we check using BFS algorithm.
		WGraph_DS g = (WGraph_DS)this.copy();
		Iterator <node_info> itr = g.getV().iterator(); //we make an iterator to get the first node in the graph, then we will traverse through it's neighbors using BFS.
		if (itr.hasNext()) {
			WGraph_DS.NodeInfo startingNode = (NodeInfo)itr.next();
			Queue<NodeInfo> q = new LinkedList<NodeInfo>();
			q.add(startingNode); //add the first node to the queue
			int numOfNodesInSubGraph = 1;
			startingNode.setInfo("BLACK"); //mark the node black, so we know we already visited this node.
			
			while(!q.isEmpty()) {
				NodeInfo currentNode = q.remove();
				for (Map.Entry<Integer, Double> entry : currentNode.getNi().entrySet()) { //iterate through all of the neighbors that we haven't visited yet.
					WGraph_DS.NodeInfo nodeNeighbor = (WGraph_DS.NodeInfo)g.getNode(entry.getKey());
					if (nodeNeighbor.getInfo() != "BLACK") { //if it's not black, than we haven't visited that node yet
						numOfNodesInSubGraph++;
						nodeNeighbor.setInfo("BLACK"); //mark the node black, so we know we already visited this node.
						q.add(nodeNeighbor); 
					}
				}
			}
			return g.nodeSize() == numOfNodesInSubGraph ? true : false; //if the number of nodes in the graph equals the number of nodes we visited using BFS, the graph is surely connected.
		}
		return false;
	}

	@Override
	public double shortestPathDist(int src, int dest) {
		WGraph_DS g = (WGraph_DS)this.copy();
		PriorityQueue<NodeInfo> pq = new PriorityQueue<>();
		
		NodeInfo sourceNode = (NodeInfo)g.getNode(src); //get the source node
		NodeInfo destNode = (NodeInfo)g.getNode(dest); //get the destination node
		sourceNode.setTag(0); //set the Tag (weight) of the source node to 0
		
		pq.add(sourceNode); //add the source node to the priority queue
		while (pq.size() > 0) { //loop through all nodes in the priority queue
			NodeInfo prioritezedNode = pq.poll();
			prioritezedNode.setInfo("BLACK"); //mark that node as visited.
			for (Map.Entry<Integer, Double> neighborNodeEntry : prioritezedNode.getNi().entrySet()) { //loop through all neightbor nodes
				NodeInfo neighborNode = (NodeInfo)g.getNode(neighborNodeEntry.getKey());
				if (neighborNode.getInfo() == "WHITE") {
					double edgeWeight = neighborNodeEntry.getValue();
					double prioritzedNodeWeight = prioritezedNode.getTag();
					if (prioritzedNodeWeight + edgeWeight < neighborNode.getTag()) //if we found a path with less weight - update the node weight
						neighborNode.setTag(prioritzedNodeWeight + edgeWeight);
					if (!pq.contains(neighborNode)) pq.add(neighborNode); //add the node to the priority queue only if it's not there yet.
				}
			}
		}
		return destNode.getTag() != Double.MAX_VALUE ? destNode.getTag() : -1; //return the destination weight or -1 if there is no path from src to dest.
	}

	@Override
	public List<node_info> shortestPath(int src, int dest) {
		WGraph_DS g = (WGraph_DS)this.copy();
		PriorityQueue<NodeInfo> pq = new PriorityQueue<>();
		
		NodeInfo sourceNode = (NodeInfo)g.getNode(src); //get the source node
		NodeInfo destNode = (NodeInfo)g.getNode(dest); //get the destination node
		sourceNode.setTag(0); //set the Tag (weight) of the source node to 0
		
		pq.add(sourceNode); //add the source node to the priority queue
		while (pq.size() > 0) { //loop through all nodes in the priority queue
			NodeInfo prioritezedNode = pq.poll();
			prioritezedNode.setInfo("BLACK"); //mark that node as visited.
			for (Map.Entry<Integer, Double> neighborNodeEntry : prioritezedNode.getNi().entrySet()) { //loop through all neightbor nodes
				NodeInfo neighborNode = (NodeInfo)g.getNode(neighborNodeEntry.getKey());
				if (neighborNode.getInfo() == "WHITE") {
					double edgeWeight = neighborNodeEntry.getValue();
					double prioritzedNodeWeight = prioritezedNode.getTag();
					if (prioritzedNodeWeight + edgeWeight < neighborNode.getTag()) { //if we found a path with less weight - update the node weight and parent
						neighborNode.setTag(prioritzedNodeWeight + edgeWeight);
						neighborNode.setParent(prioritezedNode.getKey());
					}
					if (!pq.contains(neighborNode)) pq.add(neighborNode); //add the node to the priority queue only if it's not there yet.
				}
			}
		}
		
		NodeInfo nextParent = destNode;
		if (nextParent.getParent() == -1) return null; //there's no path from src to dest.
		Stack<node_info> pathReversed = new Stack<node_info>(); //represents the path from the source to the destination
		while(nextParent.getParent() != -1) { //while we did not reach the src
			pathReversed.add(nextParent); //add the node the the path.
			nextParent = (NodeInfo) g.getNode(nextParent.getParent());
		}
		pathReversed.add(nextParent);
		Stack<node_info> path = new Stack<node_info>(); //used to reverse the order of the path, so the source will be first and dest last.
		while (pathReversed.size() > 0)
			path.add(pathReversed.pop());
		return path;
	}

	@Override
	public boolean save(String file) {
		try {
			//saving the graph using Serializable, which we implemented in WGraph_DS and NodeInfo
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			
			//writes the object to the file with the name passed as the parameter `file`
			objectOutputStream.writeObject(this.graphPointer);
			
			//closes the streams to avoid memory leaks
			fileOutputStream.close();
			objectOutputStream.close();
		} catch (IOException e) {
			//print the stack trace in case of an error and return false so we know the save function did not work properly and the file most probably did not save
			e.printStackTrace();
			return false;
		}
		//if we reached here everything went through successfully so we return true
		return true;
	}

	@Override
	public boolean load(String file) {
		try {
			//loading the graph using Serializable, which we implemented in WGraph_DS and NodeInfo
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			
			//reads the graph from the saved file with a name passed as argument `file`
			WGraph_DS deserializedGraph = (WGraph_DS) objectInputStream.readObject();
			this.graphPointer = deserializedGraph;
			
			
			fileInputStream.close();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			//print the stack trace in case of an error and return false so we know the load function did not work properly and the file most probably did not loaded
			e.printStackTrace();
			return false;
		}
		//if we reached here everything went through successfully so we return true
		return true;
	}

}
