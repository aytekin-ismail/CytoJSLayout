package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CiSELayout extends AbstractLayoutAlgorithm {
    private final CyNetworkViewWriterFactory writeCyJs;

    public CiSELayout(UndoSupport undo, CyNetworkViewWriterFactory writeNetwork) {
        super("CiSELayout", "CiSE Layout", undo);
        this.writeCyJs = writeNetwork;
    }

    public TaskIterator createTaskIterator(
            CyNetworkView networkView,
            Object context,
            Set<View<CyNode>> nodesToLayOut,
            String attrName
    ) {
        final CiSELayoutContext myContext = (CiSELayoutContext) context;
        final CyNetworkView myView = networkView;
        final CyNetworkViewWriterFactory writeCyJs = this.writeCyJs;

        Task task = new AbstractLayoutTask(
                toString(),
                networkView,
                nodesToLayOut,
                attrName,
                undoSupport
        ) {
            @Override
            protected void doLayout(TaskMonitor taskMonitor) {
                OutputStream outputString = new OutputStream() {
                    private StringBuilder string = new StringBuilder();

                    @Override
                    public void write(int b) throws IOException {
                        this.string.append((char) b);
                    }

                    public String toString() {
                        return this.string.toString();
                    }
                };

                CyWriter jsonWriter = writeCyJs.createWriter(outputString, myView);
                try {
                    jsonWriter.run(taskMonitor);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // API Call
                // TODO: Make this a variable
                String url = "http://localhost:3000/json?image=false";
                String dataToSend = outputString.toString();

                // Parse the JSON string
                JSONObject json = null;
                try {
                    json = new JSONObject(dataToSend);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Access the value of a specific key
                String elements = null;
                try {
                    elements = json.getJSONObject("elements").toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Elements: " + elements);
                dataToSend = elements;

                JSONObject jsonOptionsObject = new JSONObject();

                try {
                    JSONObject layoutOptions = new JSONObject();
                    layoutOptions.put("name", "cise");
                    layoutOptions.put("animate", false);
                    layoutOptions.put("nodeDimensionsIncludeLabels", myContext.nodeDimensionsIncludeLabels);
                    layoutOptions.put("packComponents", myContext.packComponents);
                    layoutOptions.put("allowNodesInsideCircle", myContext.allowNodesInsideCircle);

                    layoutOptions.put("padding", myContext.padding);
                    layoutOptions.put("nodeSeparation", myContext.nodeSeparation);
                    layoutOptions.put("idealInterClusterEdgeLengthCoefficient", myContext.idealInterClusterEdgeLengthCoefficient);
                    layoutOptions.put("maxRatioOfNodesInsideCircle", myContext.maxRatioOfNodesInsideCircle);
                    layoutOptions.put("springCoeff", myContext.springCoeff);
                    layoutOptions.put("nodeRepulsion", myContext.nodeRepulsion);
                    layoutOptions.put("gravity", myContext.gravity);
                    layoutOptions.put("gravityRange", myContext.gravityRange);

                    JSONObject imageOptions = new JSONObject();
                    imageOptions.put("format", "png");
                    imageOptions.put("background", "transparent");
                    imageOptions.put("width", 1280);
                    imageOptions.put("height", 720);
                    imageOptions.put("color", "bluescale");

                    jsonOptionsObject.put("layoutOptions", layoutOptions);
                    jsonOptionsObject.put("imageOptions", imageOptions);

                    System.out.println(jsonOptionsObject.toString(4));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                String optionsString = jsonOptionsObject.toString();

                String payload = "[" + dataToSend + "," + optionsString + "]";

                System.out.println("Payload: " + payload + "\n");
                Map<String,JSONObject> nodePositions = new HashMap<String, JSONObject>();

                try {
                    URL obj = new URI(url).toURL();
                    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setDoOutput(true);
                    try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                        os.writeBytes(payload);
                        os.flush();
                    }
                    int responseCode = connection.getResponseCode();
                    StringBuilder response = new StringBuilder();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        try (
                                BufferedReader reader = new BufferedReader(new InputStreamReader(
                                        connection.getInputStream()
                                ))
                        ) {
                            String line;
                            while((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                        }
                        System.out.println("Response: " + response + "\n");
                    }
                    else {
                        System.out.println("POST request failed: " + responseCode);
                    }
                    connection.disconnect();

                    //iterate over response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject layoutFromResponse = jsonResponse.getJSONObject("layout");
                    Iterator<String> nodes = layoutFromResponse.keys();
                    while(nodes.hasNext()) {
                        String node = nodes.next();
                        JSONObject value = layoutFromResponse.getJSONObject(node);
                        JSONObject position = value.getJSONObject("position");
                        try {
                            nodePositions.put(node, position);
                        } catch (Exception e) {
                            System.out.println("Exception: " + e.getMessage());
                        }
                        System.out.println("Node: " + node + " Position: " + position);
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                }

                final VisualProperty<Double> xLoc = BasicVisualLexicon.NODE_X_LOCATION;
                final VisualProperty<Double> yLoc = BasicVisualLexicon.NODE_Y_LOCATION;

                for (final View<CyNode> nodeView : nodesToLayOut) {
                    String nodeId = nodeView.getModel().getSUID().toString();
                    System.out.println("Node ID: " + nodeId);
                    JSONObject position = nodePositions.get(nodeId);
                    if(position != null) {
                        try {
                            nodeView.setVisualProperty(xLoc, position.getDouble("x"));
                            nodeView.setVisualProperty(yLoc, position.getDouble("y"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        };
        return new TaskIterator(task);
    }

    public Object createLayoutContext() {
        return new CoLaLayoutContext();
    }
}
