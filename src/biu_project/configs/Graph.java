package biu_project.configs;

import java.util.ArrayList;
import java.util.HashMap;

import biu_project.graph.Agent;
import biu_project.graph.Topic;
import biu_project.graph.TopicManagerSingleton;
import biu_project.graph.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node>{

	public boolean hasCycles() {
		for(Node n:this) { //iterating over the nodes checking it the graph has cycles
			if (n.hasCycles()) {return true;}
		}
		return false;
	}

	/**
	 * this method creates a graph out of all topics and agents, in the order of their subscriptions and publishers
	 */
	public void createFromTopics(){
		this.clear();//new biu_project.graph
		TopicManager tm = TopicManagerSingleton.get();
		HashMap<String, Node> nodes= new HashMap<>();//name to node - for search

		for(Topic t:tm.getTopics()) {//first add all topics and agents to hash to find them when adding edges
			//use hashmap to avoid duplicate creation of nodes when adding edges. each name has a single node and not creating new node for each edge
			Node t_node = new Node("T"+t.name);
			this.add(t_node);
			nodes.put("T"+t.name,t_node);

			for(Agent a:t.subs) {
				if(!nodes.containsKey("A"+a.getName())) {//avoid dups
					Node a_node = new Node("A"+a.getName()); //adding new node
					this.add(a_node);
					nodes.put("A"+a.getName(),a_node);
				}

			}
			for(Agent a:t.pubs) {
				if(!nodes.containsKey("A"+a.getName())) {//avoid dups
					Node a_node = new Node("A"+a.getName());//adding new node
					this.add(a_node);
					nodes.put("A"+a.getName(),a_node);
				}

			}
		}

		for(Topic t:tm.getTopics()) {
			Node n = nodes.get("T"+t.name);// get the topic, and find the agents connected to it via the map

			for(Agent a:t.subs) {
				Node n_sub = nodes.get("A"+a.getName());
				if(n_sub!=null) {
					n.addEdge(n_sub);
				}

			}
			for(Agent a:t.pubs) {
				Node n_pub = nodes.get("A"+a.getName());
				if(n_pub!=null) {
					n_pub.addEdge(n);
				}
			}
		}
	}


}
