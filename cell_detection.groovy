/**
 * This script provides a general template for cell detection using StarDist in QuPath.
 * This example assumes you have fluorescence image, which has a channel called 'DAPI' 
 * showing nuclei.
 * 
 * If you use this in published work, please remember to cite *both*:
 *  - the original StarDist paper (https://doi.org/10.48550/arXiv.1806.03535)
 *  - the original QuPath paper (https://doi.org/10.1038/s41598-017-17204-5)
 *  
 * There are lots of options to customize the detection - this script shows some 
 * of the main ones. Check out other scripts and the QuPath docs for more info.
 */

import qupath.ext.stardist.StarDist2D
import qupath.lib.scripting.QP

// IMPORTANT! Replace this with the path to your StarDist model
// that takes a single channel as input (e.g. dsb2018_heavy_augment.pb)
// You can find some at https://github.com/qupath/models
// (Check credit & reuse info before downloading)
def modelPath = "Path\to\model\dsb2018_heavy_augment.pb"

// Customize how the StarDist detection should be applied
// Here some reasonable default options are specified
def stardist = StarDist2D
    .builder(modelPath)
    .channels(0)            // Extract channel called 'DAPI'
    .normalizePercentiles(1, 99) // Percentile normalization
    .threshold(0.5)              // Probability (detection) threshold
    .pixelSize(0.0863*2)              // Resolution for detection; used 2x downsampling for smoother boundaries
    //.cellExpansion()            // Expand nuclei to approximate cell boundaries
    .measureShape()              // Add shape measurements to remove small objects as shown below
    //.measureIntensity()          // Add cell measurements (in all compartments)
    .build()
	
// Define which objects will be used as the 'parents' for detection
def pathObjects = QP.getAnnotationObjects().findAll {it.getPathClass() == getPathClass('Tissue')} //direct cell detection into tissue outline

// Run detection for the selected objects
def imageData = QP.getCurrentImageData()
if (pathObjects.isEmpty()) {
    QP.getLogger().error("No parent objects are selected!")
    return
}
stardist.detectObjects(imageData, pathObjects)
stardist.close() // This can help clean up & regain memory
println('StarDist done!')

//remove tiny objects
def min_area = 7.0 //in µm^2
def nuc_measurement = 'Nucleus: Area µm^2'
def small_nuc = getDetectionObjects().findAll {measurement(it, nuc_measurement) <= min_area}
removeObjects(small_nuc,true)

//add cell expansion and get measurements
import qupath.lib.analysis.features.ObjectMeasurements
def serverOriginal = getCurrentServer()
double paramExpansion = 2
double nucScale = -1
img_resolution=getCurrentImageData().getServer().getPixelCalibration().getAveragedPixelSizeMicrons() //Get the current image's resolution
def detections = getDetectionObjects()
def cellBoundaries = [:]
detections.each {it ->
    def roi = it.getROI()
    def geom = roi.getGeometry()
    def boundary = CellTools.estimateCellBoundary(geom,paramExpansion/img_resolution,nucScale)
    cellBoundaries.put(it,boundary)
}
def cells = CellTools.detectionsToCells(cellBoundaries)
clearCellMeasurements()
ObjectMeasurements.addShapeMeasurements(cells, serverOriginal.getPixelCalibration())
cells.each(p -> ObjectMeasurements.addIntensityMeasurements(serverOriginal, p, 1.0, 
                            ObjectMeasurements.Measurements.values() as List,
                            ObjectMeasurements.Compartments.values() as List))
removeObjects(detections, true) //removes the previous nuclei only detections 
addObjects(cells) // adds in the new cell objects

//add haralick features for pERK channel and YAP channel separately because the grey value ranges are different
selectCells();
runPlugin('qupath.lib.algorithms.IntensityFeaturesPlugin', '{"pixelSizeMicrons":0.0863,"region":"ROI","tileSizeMicrons":25.0,"channel1":false,"channel2":true,"channel3":false,"doMean":false,"doStdDev":false,"doMinMax":false,"doMedian":false,"doHaralick":true,"haralickMin":0.0,"haralickMax":7500.0,"haralickDistance":1,"haralickBins":32}')
runPlugin('qupath.lib.algorithms.IntensityFeaturesPlugin', '{"pixelSizeMicrons":0.0863,"region":"ROI","tileSizeMicrons":25.0,"channel1":false,"channel2":false,"channel3":true,"doMean":false,"doStdDev":false,"doMinMax":false,"doMedian":false,"doHaralick":true,"haralickMin":0.0,"haralickMax":700.0,"haralickDistance":1,"haralickBins":32}')

