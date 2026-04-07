# OIC-222 TMA pERK, YAP, ki67 triple stain analysis

Images analyzed with [QuPath v6.0](https://github.com/qupath/qupath/releases/tag/v0.6.0), [Fiji/ImageJ (v1.54p)](https://imagej.net/software/fiji/downloads) and Python v3.11.

Warpy QuPath extension v4.0 and Fiji (ImageJ v1.54p) with bigdataviewer-biop-tools-0.16.0 used for registering individual TMA images collected from the Zeiss Axio Observer 7 with Apotome to the whole slide TMA image from the Zeiss AxioScan.

The whole slide TMA image was preprocessed to reorient through horizontal flip using a [python script](Image_precrocessing.ipynb). Python3.11 env requirements [here](requirements.txt).

## How to use custom scripts and models

**Important note regarding the scripts and models in this repository: The scripts and models here in were created as a custom solution for the image analysis goal of quantifying the samples on a TMA triple stained for three markers of interest, the trained models and scripts should be used as a reference for replicating a similar analysis on your own data. Do not expect these scripts or models to work without alteration on similar but different datasets.**

### Using the python script for preprocessing

Create a python environment with your favorite python env tool and install the packages in [requirements.txt](requirements.txt).

```bash
#with conda
conda create -n envname python=3.11
conda activate envname
pip install -r path\to\requirements.txt
```

```bash
#with python venv (be sure to have the correct version of python available)
python3.11 -m venv envname
source envname/bin/activate
pip install -r path\to\requirements.txt
```

Use the [Image_preprocessing.ipynb](Image_precrocessing.ipynb) notebook in your favorite GUI coding software, like VS code or Jupyter, to use the script. Be sure to select the correct python env or jupyter kernel before running.

If using jupyter kernel, in the active python env:

```bash
pip install ipykernel #if not installed in env
python -m ipykernel install --user --name=envname #--name= will be what is displayed in the dropdown menu for kernel selection
```

Creating a zarr array and/or a pyramidal ome.tiff is not necessary for using Warpy, we did this to apply a horizontal flip to the WSI of the TMA to simply image registration. Zarr arrays were not compatible with Warpy at the time of creating this pipeline. The zarr was therefore converted to a pyramidal ome.tiff through QuPath. An ome.tiff could be generated instead of a zarr for a more streamlined pipeline.

### Using the model files in a new QuPath project

After creating a QuPath project, the whole [object_classifiers folder](object_classifiers) can be copied into the classifier folder of the QuPath project. Note that unless the same type of data was used, this models are unlikely to work on a different data set.

To train a similar model, create similar detections using the [cell_detection script](cell_detection.groovy) on a training image (follow recommended instructions in QuPath's official documentation) and then train a random forest classifier through QuPath's Train Object Classifier with the following settings:

```groovy
max depth = 25 //default setting
min sample = 50
max trees = 100
normalize = mean and variance
selected classes = true //use relevant classes
selected measurements = true //use relevant classes
```

With the points detection tool, place points in cells that are examples of positive and negative cells for a given marker. Then iterate through previewing results and adding more examples until desired performance is achieved. Test on data the classifier has not seen yet to validate performance. Repeat this process for additional markers of interest and then create a composite classifier of all models with QuPath's Create Composite Classifier.

### Registering images with Warpy

Install both the [Warpy extension for QuPath](<https://github.com/BIOP/qupath-extension-warpy>) and the PTBIOP plugin site for ImageJ (<https://biop.epfl.ch/Fiji-Update/>). Additional instructions on setting up Warpy to run in Fiji/ImageJ and using the tool can be found here: <https://imagej.net/plugins/bdv/warpy/warpy>

Once all images are registered, the objects on one image can be transferred to another image it has been registered to. Either use the Warpy transfer objects template script provided with the Warpy extension or refer to [Warpy_Transfer](Warpy_Transfer.groovy) on setting up a loop to transfer all possible objects to the open image.
