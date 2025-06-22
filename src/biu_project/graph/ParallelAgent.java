package biu_project.graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {


	/**
	 * TM container is a class to bundle up topic+message in a single object
	 */
	class TMcontainer{
		String topic;
		Message m;
		public TMcontainer(String topic,Message m)//topic+msg container
		{
			this.topic = topic;
			this.m = m;
		}
	}
	
	
	private Agent agent;
	private BlockingQueue <TMcontainer> q;
	private volatile boolean stop = false;  
	private Thread t;

	/**
	 * ParallelAgent constructor, creating an ArrayBlockingQueue with the capacity chosen and for each item
	 * we get the topic+message and activate callback on it
	 * @param agent
	 * @param cap
	 */
	public ParallelAgent(Agent agent,int cap) {
		super();
		this.agent = agent;
		q = new ArrayBlockingQueue<>(cap);
		t = new Thread (()->{
			while (!q.isEmpty()||!stop) {//while queue has contaiers (topic and message to publish)
				//take them and activate callback
				try {
					TMcontainer tm = q.take();
					agent.callback(tm.topic,tm.m);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
				}
				
			}
			
		});
		t.start();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return agent.getName();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		agent.reset();
		
	}

	/**
	 * bundle up the topic and message to the queue
	 * @param topic
	 * @param msg
	 */
	@Override
	public void callback(String topic, Message msg) {
		// TODO Auto-generated method stub
		try {
			q.put(new TMcontainer(topic,msg));//put in q the wrapped topic and message
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		t.interrupt();
		stop = true;
	}
    
}