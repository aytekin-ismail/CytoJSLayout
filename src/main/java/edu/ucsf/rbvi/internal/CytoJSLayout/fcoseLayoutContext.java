package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class fcoseLayoutContext implements TunableValidator {

    @Tunable(description="Randomize")
    public boolean randomize = true; // Default value
    @Tunable(description="Node Dimensions Include Labels")
    public boolean nodeDimensionsIncludeLabels= true;// Default value
    @Tunable(description="Uniform Node Dimensions")
    public boolean uniformNodeDimensions= false;// Default value
    @Tunable(description="Pack Components")
    public boolean packComponents= true;// Default value
    @Tunable(description="Tile")
    public boolean tile= true;// Default value

    @Tunable(description="Padding")
    public int padding= 30;// Default value
    @Tunable(description="Node Repulsion")
    public int nodeRepulsion= 4500;// Default value
    @Tunable(description="Ideal Edge Length")
    public int idealEdgeLength= 50;// Default value
    @Tunable(description="Edge Elasticity")
    public double edgeElasticity= 0.45;// Default value
    @Tunable(description="Nesting Factor")
    public double nestingFactor= 0.1;// Default value
    @Tunable(description="Number of Iterations")
    public int numIter= 2500;// Default value
    @Tunable(description="Tiling Padding Vertical")
    public int tilingPaddingVertical= 10;// Default value
    @Tunable(description="Tiling Padding Horizontal")
    public int tilingPaddingHorizontal= 10;// Default value
    @Tunable(description="Gravity")
    public double gravity= 0.25;// Default value
    @Tunable(description="Gravity Range")
    public double gravityRange= 3.8;// Default value
    @Tunable(description="Gravity Compound")
    public double gravityCompound= 1;// Default value
    @Tunable(description="Gravity Range Compound")
    public double gravityRangeCompound= 1.5;// Default value
    @Tunable(description="Initial Energy On Incremental")
    public double initialEnergyOnIncremental= 0.3;// Default value

    public ValidationState getValidationState(final Appendable errMsg) {
        return ValidationState.OK;
        // TODO: Implement validation for all parameters
    }
}
