package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class DagreLayoutContext implements TunableValidator {

    @Tunable(description="Node Dimensions Include Labels")
    public boolean nodeDimensionsIncludeLabels= true;// Default value
    @Tunable(description="Padding")
    public int padding= 30;// Default value
    @Tunable(description="Node Separation")
    public double nodeSep= 50.0;// Default value
    @Tunable(description="Edge Separation")
    public double edgeSep= 10;// Default value
    @Tunable(description="Rank Separation")
    public double rankSep= 50;// Default value
    @Tunable(description="Edge Weight")
    public int edgeWeight= 1;// Default value


    @Override
    public ValidationState getValidationState(Appendable errMsg) {
        return ValidationState.OK;
    }
}
