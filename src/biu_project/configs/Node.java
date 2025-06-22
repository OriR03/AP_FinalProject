package biu_project.configs;

import biu_project.graph.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Node {
	/**
	 * Node attributes
	 */
    private String name;
    private List<Node> edges;
    private Message msg;

	/**
	 * Node constructor
	 * @param name node's name
	 */
	public Node(String name) {
    	this.name = name;
    	this.edges = new ArrayList<>();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Node> getEdges() {
		return edges;
	}

	public void setEdges(List<Node> edges) {
		this.edges = edges;
	}

	public Message getMsg() {
		return msg;
	}

	public void setMsg(Message msg) {
		this.msg = msg;
	}
	
	public void addEdge(Node n) {
		edges.add(n);
	}

	/**
	 * Checks if this node is part of a cycle
	 * @return if this node is part of a cycle
	 */
	public boolean hasCycles() {
		HashSet<Node> visited = new HashSet<>();
		HashSet<Node> recStack = new HashSet<>();
		return hasCyclesHelper(visited, recStack);
	}

	/**
	 * helps the hasCycles() method to check if there's a cycle recursively
	 * @param visited all nodes that already visited
	 * @param recStack stack temp to check if there's a cycle
	 * @return
	 */
	private boolean hasCyclesHelper(HashSet<Node> visited, HashSet<Node> recStack) {

		if (recStack.contains(this)) return true;     // back edge → cycle
		if (visited.contains(this)) return false;     // already processed

		visited.add(this);
		recStack.add(this);

		for (Node neighbor : this.edges) {
			if (neighbor.hasCyclesHelper(visited, recStack)) {
				return true;
			}
		}

		recStack.remove(this); // backtrack
		return false;
	}

}