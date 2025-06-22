package biu_project.configs;

import biu_project.graph.Agent;
import biu_project.graph.ParallelAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
public class GenericConfig implements Config {

	private String name;
	private List <String> lines = new ArrayList<>();
	private List <Agent> agents = new ArrayList<>();
	Throwable a = new Throwable();

	/**
	 * this method takes a .conf file and generates for it a new graph object
	 */
	@Override
	public void create(){
		// TODO Auto-generated method stub
		File f = new File(name); //loading the file
		Scanner myReader;
		try {
			myReader = new Scanner(f);
			while (myReader.hasNextLine()) {//adding all the file to a List
		        String data = myReader.nextLine();
		        lines.add(data.trim());
		      }
		      myReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(lines.size()%3==0) { //check if the length is divided by 3
			for(int i=0;i<lines.size();i+=3) {
				String agentName = lines.get(i);//agent kind
				String [] subs = lines.get(i+1).split(",");//get the subs
				String [] pubs = lines.get(i+2).split(",");//get the pubs
				
				try {
					Class<?> someAgent = Class.forName(agentName); //getting the agent type
					try {
						Constructor <?> cons = someAgent.getConstructor(String[].class, String[].class);//getting the constructor of the agent
						try {
							Agent parallel = new ParallelAgent((Agent) cons.newInstance(subs,pubs),10); //creating the agent
							agents.add(parallel);
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	    for(Agent a: this.agents){
			System.out.println(a+ " agenttt");
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		for(Agent a:agents) {
			a.close();
		}
	}

	public void setConfFile(String name) {
		// TODO Auto-generated method stub
		this.name = name;
		
	}


}
