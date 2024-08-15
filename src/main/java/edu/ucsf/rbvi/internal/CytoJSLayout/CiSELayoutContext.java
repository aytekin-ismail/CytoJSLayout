package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class CiSELayoutContext implements TunableValidator {

    @Tunable(description="Node Dimensions Include Labels")
    public boolean nodeDimensionsIncludeLabels= true;// Default value
    @Tunable(description = "Pack Components")
    public boolean packComponents = true; //Default Value
    @Tunable(description = "Allow Nodes Inside Circle")
    public boolean allowNodesInsideCircle = false; //Default Value

    @Tunable(description="Padding")
    public double padding= 30;// Default value
    @Tunable(description="Node Separation")
    public double nodeSeparation= 12.5;// Default value
    @Tunable(description="Inter-Cluster Edge Length Coeff.")
    public double idealInterClusterEdgeLengthCoefficient= 1.4;// Default value
    @Tunable(description="Max Ratio Of Nodes Inside Circle")
    public double maxRatioOfNodesInsideCircle= 0.1;// Default value
    @Tunable(description="Spring Coefficient")
    public double springCoeff= 0.45;// Default value
    @Tunable(description="Node Repulsion")
    public int nodeRepulsion= 4500;// Default value
    @Tunable(description="Gravity")
    public double gravity= 0.25;// Default value
    @Tunable(description="Gravity Range")
    public double gravityRange= 3.8;// Default value

    @Override
    public ValidationState getValidationState(Appendable errMsg) {
        return ValidationState.OK;
    }
}
