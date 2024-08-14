package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;

public class AvsdfLayoutContext implements TunableValidator {

    @Tunable(description="Padding")
    public double padding= 30;// Default value
    @Tunable(description="Node Separation")
    public double nodeSeparation= 60;// Default value

    @Override
    public ValidationState getValidationState(Appendable errMsg) {
        return ValidationState.OK;
    }
}
