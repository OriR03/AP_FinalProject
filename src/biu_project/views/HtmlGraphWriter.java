package biu_project.views;

import biu_project.configs.Graph;
import biu_project.configs.Node;
import biu_project.servlets.ConfLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class HtmlGraphWriter extends Observable {
    /**
     * this method creates the graph, while getting the template for it from graph_gui_template
     * it replaces the the keys left in the template with the current parameters
     * @param g the graph to display
     * @param params parameters like color of the nodes
     */
    public void writeGraph(Graph g, Map<String, String> params) throws IOException {

        Map<String, String> symbolsMap = new HashMap<>();//agent to symbol map
        symbolsMap.put("inc agent", "+1");
        symbolsMap.put("plus agent", "+");
        symbolsMap.put("mul agent", "X");
        symbolsMap.put("square agent", "^2");
        symbolsMap.put("concat agent","||");
        symbolsMap.put("reverse agent","↺");
        symbolsMap.put("lowercase agent","↓");
        symbolsMap.put("uppercase agent","↑");

        String template = new String(Files.readAllBytes(Paths.get("src/graph_display/graph_gui_template.html")));

        boolean flag = false;
        String nodes="";
        String edges="";

        for (Node node : g) {
            String nodeId = toID(node.getName());
            String label = node.getName().substring(1);

            String type;
            if(node.getName().startsWith("A")) {
                type = "agent";
            }else{
                type = "topic";
            }

            if (type.equals("agent")) {
                label = label.replaceAll("\\d", "");
                if (params.get("displayMode").equals("symbols")) {
                    flag = true;
                    label = symbolsMap.get(label);
                }
            }
            nodes+=String.format("{ data: { id: '%s', label: '%s', type: '%s' } },\n", nodeId, label, type);//add all nodes to array
        }

        for (Node node : g) {
            String sourceId = toID(node.getName());
            for (Node target : node.getEdges()) {
                String targetId = toID(target.getName());
                edges+=String.format("{ data: { id: '%s-%s', source: '%s', target: '%s' } },\n", sourceId, targetId, sourceId, targetId);//add all edges to array
            }
        }
        //replace each key with the current parameter
        template = template.replace("{__nodes__}", nodes);
        template = template.replace("{__edges__}", edges);
        template = template.replace("{__agentColor__}", params.get("agent_color"));
        template = template.replace("{__topicColor__}", params.get("topic_color"));

        template = template.replace("{__textSymbols__}", flag ? "'font-size': 40, 'width': 80, 'height': 80" : "'width': 'mapData(label.length, 1, 10, 20, 90)', 'height': 'mapData(label.length, 1, 10, 20, 90)'");
        template=template.replace("{__textSymbolsTopic__}","'width': 'mapData(label.length, 1, 10, 20, 90)'");
        template=template.replace("{__layoutName__}",params.get("layoutName"));

        setChanged();
        notifyObservers(template);//notify the model layer that the html is ready to be served to client
    }

    /**
     * @param name gets the name of the agent, and puts _ instead of spaces, to get the valid 
     *             form in the arrays of the graph html file.
     * @return
     */
    private static String toID(String name) {
        return name.trim().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_]", "");
    }


}
