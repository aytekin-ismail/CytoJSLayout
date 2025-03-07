package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class BreadthFirstLayoutContext implements TunableValidator {

    @Tunable(description="Directed")
    public boolean directed = false;// Default value
    // whether the tree is directed downwards (or edges can point in any direction if false)

    //padding on fit
    @Tunable(description="Padding")
    public double padding= 30;// Default value

    // put depths in concentric circles if true, put depths top down if false
    @Tunable(description="Circle")
    public boolean circle = false;// Default value

    // whether to create an even grid into which the DAG is placed (circle:false only)
    @Tunable(description="Grid")
    public boolean grid = false;// Default value

    // positive spacing factor, larger => more space between nodes (N.B. n/a if causes overlap)
    @Tunable(description="Spacing Factor")
    public double spacingFactor = 1.75;// Default value



    // prevents node overlap, may overflow boundingBox if not enough space
    @Tunable(description="Avoid Overlap")
    public boolean avoidOverlap = true;// Default value

    // Excludes the label when calculating node bounding boxes for the layout algorithm
    @Tunable(description="Node Dimensions Include Labels")
    public boolean nodeDimensionsIncludeLabels = false;// Default value

    public ValidationState getValidationState(final Appendable errMsg) {
        return ValidationState.OK;
    }
}
