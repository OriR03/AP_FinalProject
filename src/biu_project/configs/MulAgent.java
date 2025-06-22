package biu_project.configs;

import biu_project.graph.Agent;
import biu_project.graph.Message;
import biu_project.graph.TopicManagerSingleton;
import biu_project.graph.TopicManagerSingleton.TopicManager;

import java.util.HashMap;

public class MulAgent implements Agent {
    private TopicManager tm;
    private String [] subs;
    private String [] pubs;
    private HashMap<String, Message> Messages = new HashMap<>();
    private static int counter=1;
    private String name;
    /**
     * MulAgent constructor
     *
     * @param subs the arr of subscribers to the topic
     * @param pubs the arr of publishers to the topic
     */
    public MulAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        this.tm = TopicManagerSingleton.get();
        if(subs.length>1) {
            tm.getTopic(subs[0]).subscribe(this);//subscribing to the agent
            tm.getTopic(subs[1]).subscribe(this);

        }
        tm.getTopic(pubs[0]).addPublisher(this);// adding the agent as a publisher
        this.name="mul agent"+this.counter;
        this.counter++;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        this.callback(subs[0], new Message(0.0));
        this.callback(subs[1], new Message(0.0));
    }

    @Override
    public void callback(String topic, Message msg) {
        // TODO Auto-generated method stub
        if (!Double.isNaN(msg.asDouble)) {
            Messages.put(topic,msg);
        }

        if(Messages.containsKey(subs[0]) && Messages.containsKey(subs[1])) {//publishing the msg
            Double x = Messages.get(subs[0]).asDouble;
            Double y = Messages.get(subs[1]).asDouble;
            if(!Double.isNaN(x) && !Double.isNaN(y)) {
                tm.getTopic(pubs[0]).publish(new Message(x*y));
            }
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(subs[1]).unsubscribe(this);
        tm.getTopic(pubs[0]).removePublisher(this);
    }



}
