package edu.ucsf.rbvi.internal.CytoJSLayout;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
public class ConcentricLayoutContext {

    @Tunable(description="Padding")
    public double padding = 30; // Default value

    @Tunable(description = "Start Angle")
    public double startAngle= 3 / 2 * Math.PI; // where nodes start in radians

    @Tunable(description = "Clockwise")
    public boolean clockwise = true; // whether the layout should go clockwise (true) or counterclockwise/anticlockwise (false)

    @Tunable(description = "Equal Distance")
    public boolean equidistant= false; // whether levels have an equal radial distance betwen them, may cause bounding box overflow

    @Tunable(description = "Minimum Node Spacing")
    public double minNodeSpacing= 10; // min spacing between outside of nodes (used for radius adjustment)

    @Tunable(description = "Avoid Overlap")
    public boolean avoidOverlap= true; // prevents node overlap, may overflow boundingBox if not enough space

    @Tunable(description = "Node Dimensions Include Labels")
    public boolean nodeDimensionsIncludeLabels= false; // Excludes the label when calculating node bounding boxes for the layout algorithm
}
