# OIC-222 TMA pERK, YAP, ki67 triple stain analysis

Images analyzed with [QuPath v6.0](https://github.com/qupath/qupath/releases/tag/v0.6.0), [Fiji/ImageJ (v1.54p)](https://imagej.net/software/fiji/downloads) and Python v3.11.

Warpy QuPath extension v4.0 and Fiji (ImageJ v1.54p) with bigdataviewer-biop-tools-0.16.0 used for registering individual TMA images collected from the Zeiss Axio Observer 7 with Apotome to the whole slide TMA image from the Zeiss AxioScan.

The whole slide TMA image was preprocessed to reorient through horizontal flip using a [python script](Image_precrocessing.ipynb). Python3.11 env requirements [here](requirements.txt).

## How to use custom scripts and models

**Important note regarding the scripts and models in this repository: The scripts and models here in were created as a custom solution for the image analysis goal of quantifying the samples on a TMA triple stained for three markers of interest, the trained models and scripts should be used as a reference for replicating a similar analysis on your own data. Do not expect these scripts or models to work without alteration on similar but different datasets.**

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

Creating a zarr array and/or a pyramidal ome.tiff is not necessary for using Warpy, we did this to apply a 