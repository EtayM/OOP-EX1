This is a submission of EX1 by Etay Matzliah - 205987795

WGraph_DS is an implementation of weighted_graph which represents a Weighted Graph.
a Weighted Graph holds Nodes and each Edge between nodes is weighted.

NodeInfo is an implementation of node_info interface which represents a Node in a Graph.
a Node holdes data of neighbor Nodes (including the weight of the edge to them) and variables for algorithms.

WGraph_Algo is an implementation of weighted_graph_algorithms interface.
WGraph_Algo has a couple usefull functions on Weighted Graphs and it does a deep copy beforehand, making sure the original Graph stays unchanged.
It also has a Save/Load to/from a file functions which we achieve by implementing Serializable.