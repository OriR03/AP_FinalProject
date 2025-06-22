package biu_project.configs;

import java.util.HashMap;

import biu_project.graph.Agent;
import biu_project.graph.Message;
import biu_project.graph.TopicManagerSingleton;
import biu_project.graph.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent {

	private TopicManager tm;
    private String [] subs;
    private String [] pubs;
    private HashMap<String, Message> Messages = new HashMap<>();
	private static int counter=1;
	private String name;

	/**
	 * IncAgent constructor
	 *
	 * @param subs the arr of subscribers to the topic
	 * @param pubs the arr of publishers to the topic
	 */
	public IncAgent(String[] subs, String[] pubs) {
		this.subs = subs;
		this.pubs = pubs;
		this.tm = TopicManagerSingleton.get();
		
		tm.getTopic(subs[0]).subscribe(this); //subscribing to the agent
		tm.getTopic(pubs[0]).addPublisher(this); // adding the agent as a publisher
		this.name="inc agent"+this.counter;
		this.counter++;
	}
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		tm.getTopic(pubs[0]).publish(new Message(0.0));
	}

	/**
	 * @param topic
	 * @param msg
	 * this method gets a message and sends it by callback function to all subscribers
	 */
	@Override
	public void callback(String topic, Message msg) {
		// TODO Auto-generated method stub
		if (!Double.isNaN(msg.asDouble)) { //if the msg is not Double.NaN
			Messages.put(topic,msg);
		}
		
		if(Messages.containsKey(subs[0])) { //publishing the msg
			Double x = Messages.get(subs[0]).asDouble;
			if(!Double.isNaN(x)) {
				tm.getTopic(pubs[0]).publish(new Message(x+1));
			}
		}
	}

	/**
	 * this method unsubscribes and removes the publishers to it, so it no longer exist in the graph
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		tm.getTopic(subs[0]).unsubscribe(this);
		tm.getTopic(subs[1]).unsubscribe(this);
		tm.getTopic(pubs[0]).removePublisher(this);
	}

    
}
