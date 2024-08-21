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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CiSELayout extends AbstractLayoutAlgorithm {
    private final CyNetworkViewWriterFactory writeCyJs;
    private final String syblarsUrl;

    public CiSELayout(UndoSupport undo, CyNetworkViewWriterFactory writeNetwork, String syblarsUrl) {
        super("CiSELayout", "CiSE Layout", undo);
        this.writeCyJs = writeNetwork;
        this.syblarsUrl = syblarsUrl;
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
        final ApiHelper apiHelper = new ApiHelper(syblarsUrl);

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
                    System.err.println("Exception: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                // Access the value of a specific key
                JSONObject elements = null;
                try {
                    JSONObject json = new JSONObject(outputString.toString());
                    elements = json.getJSONObject("elements");
                } catch (JSONException e) {
                    System.err.println("Exception: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                // Store node height and width to be used later
                Map<String, Double> nodeToWidth = new HashMap<>();
                Map<String, Double> nodeToHeight = new HashMap<>();
                for (final View<CyNode> nodeView : nodesToLayOut) {
                    String nodeId = nodeView.getModel().getSUID().toString();

                    double nodeWidth = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
                    double nodeHeight = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);

                    nodeToWidth.put(nodeId, nodeWidth);
                    nodeToHeight.put(nodeId, nodeHeight);
                }

                // Add the node height and width to the elements data
                try {
                    JSONArray nodesArray = elements.getJSONArray("nodes");

                    for (int i = 0; i < nodesArray.length(); i++) {
                        JSONObject node = (JSONObject) nodesArray.get(i);
                        JSONObject data = (JSONObject) node.get("data");
                        String nodeSUID = data.get("SUID").toString();
                        data.put("width", nodeToWidth.get(nodeSUID));
                        data.put("height", nodeToHeight.get(nodeSUID));
                    }
                } catch (JSONException e) {
                    System.err.println("Exception: " + e.getMessage());
                    throw new RuntimeException(e);
                }

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
                } catch (JSONException e) {
                    System.err.println("Exception: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                String dataToSend = elements.toString();
                String optionsString = jsonOptionsObject.toString();

                String payload = "[" + dataToSend + "," + optionsString + "]";

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
                    JSONObject position = nodePositions.get(nodeId);
                    JSONObject sizes = nodeSizes.get(nodeId);

                    // Set new position of the nodes
                    if(position != null) {
                        try {
                            nodeView.setVisualProperty(xLoc, position.getDouble("x"));
                            nodeView.setVisualProperty(yLoc, position.getDouble("y"));
                        } catch (JSONException e) {
                            System.err.println("Exception: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }

                    // Set new sizes of the nodes
                    if(sizes != null) {
                        try {
                            nodeView.setVisualProperty(height, sizes.getDouble("height"));
                            nodeView.setVisualProperty(width, sizes.getDouble("width"));
                        } catch (JSONException e) {
                            System.err.println("Exception: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        return new TaskIterator(task);
    }

    public Object createLayoutContext() {
        return new CiSELayoutContext();
    }
}
