package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import java.util.Collection;
import java.util.Properties;

import static org.cytoscape.work.ServiceProperties.*;

/**
 * {@code CyActivator} is a class that is a starting point for OSGi bundles.
 * 
 * A quick overview of OSGi: The common currency of OSGi is the <i>service</i>.
 * A service is merely a Java interface, along with objects that implement the
 * interface. OSGi establishes a system of <i>bundles</i>. Most bundles import
 * services. Some bundles export services. Some do both. When a bundle exports a
 * service, it provides an implementation to the service's interface. Bundles
 * import a service by asking OSGi for an implementation. The implementation is
 * provided by some other bundle.
 * 
 * When OSGi starts your bundle, it will invoke {@CyActivator}'s
 * {@code start} method. So, the {@code start} method is where
 * you put in all your code that sets up your app. This is where you import and
 * export services.
 * 
 * Your bundle's {@code Bundle-Activator} manifest entry has a fully-qualified
 * path to this class. It's not necessary to inherit from
 * {@code AbstractCyActivator}. However, we provide this class as a convenience
 * to make it easier to work with OSGi.
 *
 * Note: AbstractCyActivator already provides its own {@code stop} method, which
 * {@code unget}s any services we fetch using getService().
 */
public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}
	/**
	 * This is the {@code start} method, which sets up your app. The
	 * {@code BundleContext} object allows you to communicate with the OSGi
	 * environment. You use {@code BundleContext} to import services or ask OSGi
	 * about the status of some service.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		final CyServiceRegistrar serviceRegistrar = getService(context, CyServiceRegistrar.class);
		final UndoSupport undoSupport = getService(context, UndoSupport.class);
		final String syblarsURL = "http://localhost:3000/json?image=false";
		//final String syblarsURL = "https://syblars.cs.bilkent.edu.tr/json?image=false";

		String filter = "("+ID+"=cytoscapejsNetworkWriterFactory)";
		CyNetworkViewWriterFactory writeCyJs = serviceRegistrar.getService(CyNetworkViewWriterFactory.class, filter);

		{
			final fcoseLayout fcoseLayout = new fcoseLayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, fcoseLayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, fcoseLayout, CyLayoutAlgorithm.class, props);
		}
		{
			final CoLaLayout coLaLayout = new CoLaLayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, coLaLayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, coLaLayout, CyLayoutAlgorithm.class, props);
		}
		{
			final CiSELayout ciSELayout = new CiSELayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, ciSELayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, ciSELayout, CyLayoutAlgorithm.class, props);
		}
		{
			final DagreLayout dagreLayout = new DagreLayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, dagreLayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, dagreLayout, CyLayoutAlgorithm.class, props);
		}
		{
			final AvsdfLayout avsdfLayout = new AvsdfLayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, avsdfLayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, avsdfLayout, CyLayoutAlgorithm.class, props);
		}
		{
			final ConcentricLayout concentricLayout = new ConcentricLayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, concentricLayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, concentricLayout, CyLayoutAlgorithm.class, props);
		}
		{
			final BreadthFirstLayout breadthFirstLayout = new BreadthFirstLayout(undoSupport, writeCyJs, syblarsURL);
			final Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(PREFERRED_MENU, "Layout");
			props.setProperty(TITLE, breadthFirstLayout.toString());
			props.setProperty(MENU_GRAVITY, "10.1");
			registerService(context, breadthFirstLayout, CyLayoutAlgorithm.class, props);
		}

	}
}
