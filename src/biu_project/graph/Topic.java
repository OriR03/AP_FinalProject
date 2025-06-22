package biu_project.graph;

import java.util.ArrayList;



public class Topic {
    public final String name;
    public String msg;
    public ArrayList <Agent> subs = new ArrayList<>();
    public ArrayList <Agent> pubs = new ArrayList<>();

    /**
     * Topic constructor
     * @param name topic name
     */
    public Topic(String name){
        this.name=name;
    }

    /**
     * subscribe to an agent
     * @param a the agent the topic wants to sub
     */
    public void subscribe(Agent a){
    	subs.add(a);
    }

    /**
     *  unsubscribe from an agent
     * @param a the agent the topic wants to unsub
     */
    public void unsubscribe(Agent a){
    	subs.remove(a);
    }

    /**
     * publish a msg to all subs
     * @param m the msg the topic wants to publish
     */
    public void publish(Message m) {
        this.msg = m.asText;
        for (Agent a : new ArrayList<>(subs)) {//iterate over the subs and activate callbacks
            try {
                a.callback(name, m);
            } catch (Exception e) {
            }
        }
    }

    /**
     * add a publisher to the topic
     * @param a the publisher the topic wants to add
     */
    public void addPublisher(Agent a){
    	pubs.add(a);
    }
    /**
     * remove a publisher from the topic
     * @param a the publisher the topic wants to remove
     */
    public void removePublisher(Agent a){
    	pubs.remove(a);
    }


}
