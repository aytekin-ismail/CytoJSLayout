package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class MyTaskFactory extends AbstractTaskFactory {
    public MyTaskFactory() {
        super();
    }

    public TaskIterator createTaskIterator() {
        return null;
    }

    public boolean isReady() {
        return true;
    }
}
