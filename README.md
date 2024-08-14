# CytoLayout
## Project Overview
The aim of this project is to enhance the layout capabilities of Cytoscape, an open-source software platform renowned for visualizing complex networks and integrating them with attribute data. While Cytoscape offers a plethora of automatic layout algorithms such as hierarchical, circular, force-directed, and CoSE (Compound Spring Embedder), it lacks support for certain advanced layouts available in its sister project, Cytoscape.js. These layouts, including fCoSE, CoLa, and CiSE, offer features and functionalities that are not present in the layouts currently supported by Cytoscape. Therefore, the project's focal point is to bridge this gap by implementing a Cytoscape layout plugin that extends its capabilities to include layouts supported by Cytoscape.js via SyBLaRS, a web-service suitable for this purpose. By incorporating these additional layouts, users will benefit from a broader range of options for organizing and visualizing networks, enabling more sophisticated analyses and insights into complex biological and computational systems.

### Supported Layouts
The supported graph layout algorithms are: 
- [fCoSE](https://github.com/iVis-at-Bilkent/cytoscape.js-fcose/tree/unstable)
- [CoLa](https://github.com/cytoscape/cytoscape.js-cola)
- [CiSE](https://github.com/iVis-at-Bilkent/cytoscape.js-cise/tree/develop)
- [Dagre](https://github.com/cytoscape/cytoscape.js-dagre)
- [Avsdf](https://github.com/iVis-at-Bilkent/cytoscape.js-avsdf)

## Dependencies
### SyBLaRS
- The plugin uses [SyBLaRS](https://github.com/iVis-at-Bilkent/syblars) service to process the network data to output the required layout.
- An active internet connection will be required for the plugin to communicate with the service.
- An alternative approach can be a locally hosted SyBLaRS service.
- TODO: Guide on how to change the SyBLaRS service url.

## How to
### Build
Clone the repository.
```bash
git clone https://github.com/tushar-c23/CytoLayout
```

Change to the cloned directory
```bash
mvn clean install
```

Locate the compiled `target/CytoJSLayout-1.0.jar` file.

Copy the file to `CytoscapeConfiguration/3/apps/installed` location.

Run `Cytoscape`

The plugin layouts will be available to be used in the `layout` menu.
