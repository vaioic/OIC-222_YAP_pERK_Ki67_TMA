## OIC-222 YAP pERK Ki67 TMA Analysis

### Key for QuPath TMA Analysis

To organize the cell detections, counts, and measurements by TMA core, QuPath's TMA dearray tool is used to identify the TMA grid and assign core identities. The columns are assign a number and the rows are assigned a letter, thereby assigning each TMA core a unique number-letter ID. See example below:

<img src="/Images/TMA_Array.png">

The tissue inside each TMA location is then outlined and the cells in each tissue are detected and classified as YAP, Ki67, YAP:Ki67, or left blank if it is negative.

The cell counts for the TMA array and the cells were exported as a CSV.

The TMA csv file has the following columns:

- Image: which image the data come from (only the triple stained image was analyzed, should be the same for all entries)
- Object ID: a unique identifier for the tissue outline
- TMA Core: the location in the TMA array
- Missing core: True or False value; if True the TMA tissue was not intact. It is included in the array for consistent naming, but not analyzed.
- Num Detections: total number of cells detected in the tissue
- Num Ki67: number of cells that are ki67 positive
- Num YAP: number of cells that are YAP positive
- Num YAP:Ki67: number of cells that are double positive for YAP and Ki67
- Percent Ki67/YAP/YAP:Ki67: percentage of the cells in each class

The cell measurements are broken into different cell compartments like so:

<img src='/Images/QuPath_Cell_Compartment_Measurements.png'>

The following columns were included in the cell measurement csv:

- Image: which image the data come from (only the triple stained image was analyzed, should be the same for all entries)
- Object ID: a unique identifier for the tissue outline
- Classification: Nothing/blank, Ki67, YAP, or YAP:Ki67
- TMA Core: which TMA core the cell belongs to
- Cell compartment measurements (same compartment and statistical measurements for each marker):
  - Nucleus:**Ki67/YAP/YAP:Ki67**:*Mean/Median/Min/Max/Std.Dev.*
  - Cytoplasm:**Ki67/YAP/YAP:Ki67**:*Mean/Median/Min/Max/Std.Dev.*
  - Cell:**Ki67/YAP/YAP:Ki67**:*Mean/Median/Min/Max/Std.Dev.*
