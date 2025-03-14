# CREATest

## Replication package for the paper "Abstract Test Generation for itemis CREATE Statecharts by Code Transformation", submitted to ICTSS 2025

This repository contains the replication package for the paper "Abstract Test Generation for itemis CREATE Statecharts by Code Transformation", submitted to ICTSS 2025. The package contains the following artifacts:

* `CREATest`: folder containing the source code of the CREATest tool.
* `dist`: folder containing the compiled version of the CREATest tool (`CREATest-0.0.3.jar`), together with the needed libraries (e.g., EvoSuite)
* `experiments`: folder containing all the scripts and models used for the ecxperimental evaluation of the tool.
* `experiments-model`: folder containing the models used in the evaluation of the paper.
    - `from_itemis`: folder containing the models taken from itemis repository
    - `from_stl4iot`: folder containing the models taken from the STL4IoT repository
    - `from_other_repos`: folder containing the models taken from other repositories
    - `SuiteForCluster[X].sctunit`: test suite for cluster [X] used for the evaluation of the CREATest tool.

## How to use the CREATest tool

### Requirements

The tool has the following requirements:

* A machine with a Windows operating system installed and at least 21MB of
free hard disk space.
* Java Development Kit (JDK) version 9 installed on the machine. The
JDK is available for the download at https://www.oracle.com/it/java/technologies/javase/javase9-archive-downloads.html.
* A working Eclipse-based installation of itemis CREATE (both standalone and Eclipse plug-in are supported). The supported versions of itemis CREATE are 5.2.x (the correct functioning of the tool is not guaranteed with different versions of itemis CREATE). itemis CREATE is available under license.
No feature provided by the professional license is required to use the tool, so the standard license is sufficient. The installation of itemis CREATE comes with an evaluation license that is valid for 30 days. Licenses are available at https://www.itemis.com/en/products/itemis-create/licenses/. 
itemis CREATE can be downloaded after filling out the form available at https://info.itemis.com/products/itemis-create/download/.
* A directory named `libs` containing the dependencies as JAR files. The `libs` directory must be located in the same directory of `Createst-0.0.3.jar` and its content is available in the `dist/libs.zip` file available in this replication package. The required dependencies are:
    - ANTLR Runtime version 3.3,
    - Apache Commons CLI version 1.6.0,
    - EvoSuite version 1.2.0,
    - JUnit version 4.13.2,
    - JavaParser Core version 3.25.6,
    - StringTemplate version 4.0.2,
    - ZeroTurnaround ZIP version 1.17.

### CLI

The CREATest tool can be used by the user via a command line interface (CLI).
The following options are required:

* `-s <arg>`, `--sccPath <arg>`: the absolute path to the `scc.bat` file contained in the itemis CREATE installation, used for Java code generation from CREATE statecharts.
* `-y <arg>`, `--yscPath <arg>`: the absolute path of the CREATE statechart file. It must have .ysc extension.

The following options are optional:

* `-h`, `--help`: print the help message, regardless of other options.
* `-b <arg>`, `--evoSearchBudget <arg>`: the EvoSuite search budget, expressed in seconds. It must be a positive integer.
* `-g`, `--genArtifacts`: generate a .zip containing all the artifacts produced during the process.
* `-e`, `--runExperiments`: execute an extra time the generation process skipping the Java simplification phase. For experimental purposes.

If an input option provided by the user is incorrect (e.g. the specified source file does not exist) or one of the required options is missing, a message describing the error is displayed.

#### Usage

Once all the requirements are met, the CREATest tool can be used. It is sufficient to run the CREATest JAR with the appropriate options. At the end of the run, a .sctunit file containing the SCTUnit test class will be created in the folder containing the JAR.

If the `-g` option is used, a .zip file is also created. After decompressing the .zip file, the resulting directory can be opened as a workspace with itemis CREATE. The workspace contains a project with all the produced artefacts. In itemis CREATE, the CREATE statechart can be opened and the SCTUnit test class can be executed. To fix the compilation errors in the project, it is necessary to add the EvoSuite 1.2.0 jar as a dependency of the project. The JUnit test class must be runned with JUnit 4 as test runner and Java JDK 9 as runtime JRE.

### Input Statechart Limitations

It is not recomended to use characters that are not the underscore or alphanumeric characters in the statechart definition section or in states and regions. It is also discuraged the use of Java and SCTUnit keywords. Especially in the namespace, the use of such characters or keywords may lead to the failure of the tool.
