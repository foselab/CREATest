# CREATest

## Replication package for the paper "Abstract Test Generation for itemis CREATE Statecharts by Code Transformation", submitted to ICST 2025

This repository contains the replication package for the paper "Abstract Test Generation for itemis CREATE Statecharts by Code Transformation", submitted to ICST 2025. The package contains the following artifacts:

* `CREATest`: folder containing the source code of the CREATest tool.
* `dist`: folder containing the compiled version of the CREATest tool (`CREATest-0.0.2.jar`), together with the needed libraries (e.g., EvoSuite)
* `experiments-model`: folder containing the models used in the evaluation of the paper.
    - `from_itemis`: folder containing the models taken from itemis repository
    - `from_stl4iot`: folder containing the models taken from the STL4IoT repository
    - `from_other_repos`: folder containing the models taken from other repositories
    - `SuiteForCluster[X].sctunit`: test suite for cluster [X] used for the evaluation of the CREATest tool.
