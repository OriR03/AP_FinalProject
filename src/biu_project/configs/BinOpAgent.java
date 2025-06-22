package biu_project.configs;

import java.util.HashMap;
import java.util.function.BinaryOperator;

import biu_project.graph.Agent;
import biu_project.graph.Message;
import biu_project.graph.TopicManagerSingleton;
import biu_project.graph.TopicManagerSingleton.TopicManager;
/**
 * BinOpAgent is a
 */
public class BinOpAgent implements Agent {
	/**
	 * BinOpAgent attributes
	 */
	private String name;
    private String topic1;
    private String topic2;
    private String outputT;
    private BinaryOperator<Double> op;
    private HashMap<String, Message> Messages = new HashMap<>();
    private TopicManager tm;

	/**
	 * BinOpAgent Constructor
	 *
	 * @param name agent name
	 * @param topic1 first topic
	 * @param topic2 sec topic
	 * @param outputT output topic
	 * @param op the binary operator
	 */
	public BinOpAgent(String name, String topic1, String topic2, String outputT, BinaryOperator<Double> op) {
		// TODO Auto-generated constructor stub
		this.name =name;
		this.topic1 = topic1;
		this.topic2 = topic2;
		this.outputT = outputT;
		this.op = op;
		this.tm = TopicManagerSingleton.get();
		
		tm.getTopic(topic1).subscribe(this); //subscribing the input topics  to the output topic
		tm.getTopic(topic2).subscribe(this);
		tm.getTopic(outputT).addPublisher(this); //adding the output topic as a publisher
		
	}

	/**
	 * @return the agent's name
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * resets the agent by sending 0.0 message to the input topics
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		Messages.put(topic1, new Message(0.0));//put 0 as written
        Messages.put(topic2, new Message(0.0));
	}

	/**
	 * called when a msg is received in the subscribed topics and publishing the result if got valid inputs
	 *
	 * @param topic the topic that sent the msg
	 * @param msg the msg
	 */
	@Override
	public void callback(String topic, Message msg) {

		// TODO Auto-generated method stub
		if (!Double.isNaN(msg.asDouble)) { //adding the msg if its not NaN
			Messages.put(topic,msg);
		}
		
		if(Messages.containsKey(topic1) && Messages.containsKey(topic2)) {
			Double v1 = Messages.get(topic1).asDouble;
			Double v2 = Messages.get(topic2).asDouble;
			if(!Double.isNaN(v1) && !Double.isNaN(v2)) {//check if both are doubles
				tm.getTopic(outputT).publish(new Message(op.apply(v1, v2)));//apply the op and publish
			}
		}
		
		
	}

	/**
	 * closing the agent
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
        tm.getTopic(topic1).unsubscribe(this); //unsubscribing and removing the output topic as a publisher
        tm.getTopic(topic2).unsubscribe(this);
        tm.getTopic(outputT).removePublisher(this);
	}
	
    
}
