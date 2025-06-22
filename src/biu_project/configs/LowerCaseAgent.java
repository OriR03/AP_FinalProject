package biu_project.configs;

import biu_project.graph.Agent;
import biu_project.graph.Message;
import biu_project.graph.TopicManagerSingleton;
import biu_project.graph.TopicManagerSingleton.TopicManager;

import java.util.HashMap;

public class LowerCaseAgent implements Agent {
    private TopicManager tm;
    private String [] subs;
    private String [] pubs;
    private HashMap<String, Message> Messages = new HashMap<>();
    private static int counter=1;
    private String name;

    /**
     * LowerCaseAgent constructor
     *
     * @param subs the arr of subscribers to the topic
     * @param pubs the arr of publishers to the topic
     */
    public LowerCaseAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        this.tm = TopicManagerSingleton.get();

        tm.getTopic(subs[0]).subscribe(this); //subscribing to the agent
        tm.getTopic(pubs[0]).addPublisher(this); // adding the agent as a publisher
        this.name="lowercase agent"+this.counter;
        this.counter++;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void reset() {
        this.callback(subs[0], new Message(""));
    }

    @Override
    public void callback(String topic, Message msg) {
        // TODO Auto-generated method stub
        Messages.put(topic,msg);


        if(Messages.containsKey(subs[0])) {//publishing the msg
            String x = Messages.get(subs[0]).asText.toLowerCase();
            tm.getTopic(pubs[0]).publish(new Message(x));
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(pubs[0]).removePublisher(this);
    }



}
