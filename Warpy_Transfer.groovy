/**
 * Transfer PathObjects to the currently open image from candidate entries that have been registered with Warpy
 *
 * REQUIREMENTS
 * ============
 * You need to:
 *  - have the QuPath Warpy Extension installed (https://github.com/BIOP/qupath-extension-warpy)
 *  - have an opened image which has been registered with Warpy (https://imagej.net/plugins/bdv/warpy/warpy)
 *  - have some objects detected on one of the images, and have the other image currently active in the viewer
 *
 * This script then transfer all objects from the moving to the fixed image (or vice versa)
 *
 * Support for annotations, detections, cells. TMAs are handled separately, see the appropriate script.

 * @author Nicolas Chiaruttini
 * @author Olivier Burri
 *
 */
// Necessary import, requires qupath-extenstion-warpy, see: https://github.com/BIOP/qupath-extension-warpy
// THIS SCRIPT NEEDS TO BE RUN ON THE IMAGE THAT THE OBJECTS SHOULD BE TRANSFERRED TO, WILL ONLY WORK IF THE IMAGES HAVE BEEN REGISTERED WITH THE WARPY PLUGIN IN IMAGEJ

import qupath.ext.warpy.*

// Transfer PathObjects from another image that contains a serialized RealTransform
// result from Warpy

// The current Image Entry that we want to transfer PathObjects to
def targetEntry = getProjectEntry()

// Locate candidate entries can can be transformed into the source entry
def sourceEntries = Warpy.getCandidateSourceEntries( targetEntry )

// Loop through all candidate entries to transfer objects from all images to the WSI of the TMA cores
if(sourceEntries.size() > 1) {
    logger.warn( "Multiple candidate entries found, looping through entries" )
}

sourceEntries.each { source ->
    def transform = Warpy.getRealTransform( source, targetEntry )
    def objectsToTransfer = Warpy.getPathObjectsFromEntry( source )
    def transferredObjects = Warpy.transformPathObjects( objectsToTransfer, transform )
    def downsample = 1
    Warpy.addIntensityMeasurements(transferredObjects, downsample)
    addObjects(transferredObjects)
    }

fireHierarchyUpdate()
println "Warpy anotations and detections transfer script done"

//Classify objects
runObjectClassifier("Composite_Classifier")

//Be sure to check if the hierarchy updated correctly after running. Remember to save the project if not run through "Run for Project" in the script editor