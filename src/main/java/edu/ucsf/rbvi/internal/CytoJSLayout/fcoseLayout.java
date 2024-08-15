package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.Task;
import org.cytoscape.io.write.CyWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class fcoseLayout extends AbstractLayoutAlgorithm {
    private final CyNetworkViewWriterFactory writeCyJs;
    private final String syblarsUrl;

    public fcoseLayout(UndoSupport undo, CyNetworkViewWriterFactory writeNetwork, String syblarsUrl) {
        super("fcoseLayout", "fCoSE Layout", undo);
        this.writeCyJs = writeNetwork;
        this.syblarsUrl = syblarsUrl;
    }

    public TaskIterator createTaskIterator(CyNetworkView networkView, Object context,
                                           Set<View<CyNode>> nodesToLayOut,
                                           String attrName) {
        final fcoseLayoutContext myContext = (fcoseLayoutContext) context;
        final CyNetworkView myView = networkView;
        final CyNetworkViewWriterFactory writeCyJs = this.writeCyJs;
        final ApiHelper apiHelper = new ApiHelper(syblarsUrl);

        Task task = new AbstractLayoutTask(
                toString(), networkView, nodesToLayOut, attrName, undoSupport
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

                // Open Output stream
                CyWriter jsonWriter = writeCyJs.createWriter(
                        outputString,
                        myView
                );
                try {
                    jsonWriter.run(taskMonitor);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // API Call
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

                Map<String, Double> nodeToWidth = new HashMap<>();
                Map<String, Double> nodeToHeight = new HashMap<>();

                for (final View<CyNode> nodeView : nodesToLayOut) {
                    String nodeId = nodeView.getModel().getSUID().toString();

                    double nodeWidth = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
                    double nodeHeight = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);

                    nodeToWidth.put(nodeId, nodeWidth);
                    nodeToHeight.put(nodeId, nodeHeight);

                    System.out.println("Node ID:" + nodeId + " Width:" + nodeWidth + " Height:" + nodeHeight);
                }

                String newElements = null;
                try {
                    JSONObject elementsJson = new JSONObject(elements);
                    JSONArray nodesArray = elementsJson.getJSONArray("nodes");

                    for (int i = 0; i < nodesArray.length(); i++) {
                        JSONObject node = (JSONObject) nodesArray.get(i);
                        JSONObject data = (JSONObject) node.get("data");
                        String nodeSUID = data.get("SUID").toString();
                        data.put("width", nodeToWidth.get(nodeSUID));
                        data.put("height", nodeToHeight.get(nodeSUID));
                    }

                    newElements = elementsJson.toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("New Elements: " + newElements);
                dataToSend = newElements;

                JSONObject jsonOptionsObject = new JSONObject();
                try {
                    JSONObject layoutOptions = new JSONObject();
                    layoutOptions.put("name", "fcose");
                    layoutOptions.put("randomize", myContext.randomize);
                    layoutOptions.put("padding", myContext.padding);
                    layoutOptions.put("nodeDimensionsIncludeLabels", myContext.nodeDimensionsIncludeLabels);
                    layoutOptions.put("uniformNodeDimensions", myContext.uniformNodeDimensions);
                    layoutOptions.put("packComponents", myContext.packComponents);
                    layoutOptions.put("nodeRepulsion", myContext.nodeRepulsion);
                    layoutOptions.put("idealEdgeLength", myContext.idealEdgeLength);
                    layoutOptions.put("edgeElasticity", myContext.edgeElasticity);
                    layoutOptions.put("nestingFactor", myContext.nestingFactor);
                    layoutOptions.put("numIter", myContext.numIter);
                    layoutOptions.put("tile", myContext.tile);
                    layoutOptions.put("tilingPaddingVertical", myContext.tilingPaddingVertical);
                    layoutOptions.put("tilingPaddingHorizontal", myContext.tilingPaddingHorizontal);
                    layoutOptions.put("gravity", myContext.gravity);
                    layoutOptions.put("gravityRange", myContext.gravityRange);
                    layoutOptions.put("gravityCompound", myContext.gravityCompound);
                    layoutOptions.put("gravityRangeCompound", myContext.gravityRangeCompound);
                    layoutOptions.put("initialEnergyOnIncremental", myContext.initialEnergyOnIncremental);

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
                Map<String,JSONObject> nodeSizes = new HashMap<String, JSONObject>();

                try {
                    JSONObject layoutFromResponse = apiHelper.postToSyblars(payload);

                    Iterator<String> nodes = layoutFromResponse.keys();
                    while(nodes.hasNext()) {
                        String node = nodes.next();
                        JSONObject value = layoutFromResponse.getJSONObject(node);
                        JSONObject position = value.getJSONObject("position");
                        JSONObject sizes = value.getJSONObject("data");
                        try {
                            nodePositions.put(node, position);
                            nodeSizes.put(node, sizes);
                        } catch (Exception e) {
                            System.out.println("Exception: " + e.getMessage());
                        }
                        System.out.println("Node: " + node + " Position: " + position);
                        System.out.println("Node: " + node + " Position: " + sizes);
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }

                final VisualProperty<Double> xLoc = BasicVisualLexicon.NODE_X_LOCATION;
                final VisualProperty<Double> yLoc = BasicVisualLexicon.NODE_Y_LOCATION;
                final VisualProperty<Double> height = BasicVisualLexicon.NODE_HEIGHT;
                final VisualProperty<Double> width = BasicVisualLexicon.NODE_WIDTH;

                for (final View<CyNode> nodeView : nodesToLayOut) {
                    String nodeId = nodeView.getModel().getSUID().toString();
                    System.out.println("Node ID: " + nodeId);
                    JSONObject position = nodePositions.get(nodeId);
                    JSONObject sizes = nodeSizes.get(nodeId);

                    if(position != null) {
                        try {
                            nodeView.setVisualProperty(xLoc, position.getDouble("x"));
                            nodeView.setVisualProperty(yLoc, position.getDouble("y"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if(sizes != null) {
                        try {
                            nodeView.setVisualProperty(height, sizes.getDouble("height"));
                            nodeView.setVisualProperty(width, sizes.getDouble("width"));
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
        return new fcoseLayoutContext();
    }
}
