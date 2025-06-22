package biu_project.graph;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;



public class TopicManagerSingleton {

    public static class TopicManager{
    	ConcurrentHashMap <String,Topic> map;
    	public static final TopicManager instance = new TopicManager();
    	
  
    	private TopicManager() {
    		this.map = new ConcurrentHashMap<>();
    	}

		/**
		 * the method either gets the topic of the given parameter or creates a new one if non exist
		 * @param s topic name
		 * @return
		 */
    	public Topic getTopic(String s) {
    	    return map.computeIfAbsent(Objects.requireNonNull(s), Topic::new);
    	}//return as requred, if absent then create a new topic for it, else return the topic from map

		/**
		 * @return the collection of topics
		 */
    	public Collection<Topic> getTopics(){
    		return map.values();
    	}
    	public void clear() {
    		map.clear();
    	}
    	
    }

    public static TopicManager get(){
    	return TopicManager.instance;
    }

}
