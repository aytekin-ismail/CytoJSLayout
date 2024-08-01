package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class CoLaLayoutContext implements TunableValidator {

    @Tunable(description="Node Dimensions Include Labels")
    public boolean nodeDimensionsIncludeLabels= true;// Default value
    @Tunable(description = "Avoid Overlap")
    public boolean avoidOverlap = true; //Default Value
    @Tunable(description = "Handle Disconnected")
    public boolean handleDisconnected = true; //Default Value

    @Tunable(description="Padding")
    public int padding= 30;// Default value
    @Tunable(description="Convergence Threshold")
    public double convergenceThreshold= 0.01;// Default value
    @Tunable(description="Node Spacing")
    public int nodeSpacing= 10;// Default value
    @Tunable(description="Edge Length")
    public int edgeLength= 50;// Default value
    @Tunable(description="Edge SymDiff Length")
    public int edgeSymDiffLength= 0;// Default value
    @Tunable(description="Edge Jaccard Length")
    public int edgeJaccardLength= 0;// Default value
    @Tunable(description="Unconstraint Iterations")
    public int unconstrIter= 10;// Default value
    @Tunable(description="User Constraint Iterations")
    public int userConstIter= 15;// Default value
    @Tunable(description="All Constraint Iterations")
    public int allConstIter= 20;// Default value


    @Override
    public ValidationState getValidationState(Appendable errMsg) {
        return ValidationState.OK;
    }
}
