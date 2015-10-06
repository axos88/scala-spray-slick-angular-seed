Spray Slick Angular Seed project
============================

Based off https://github.com/jacobus/s4

Sample project showing off how to integrate scala spray and slick for the backend, and serve off an angular frontend for it.

to use this currently you will need to run:
  - npm
  - bower
  - manually edit the requirejs config file (TODO: describe how)

TODO:
  - write this readme
  - clean up the code
  - write tests
  - Integrate bower run with sbt
  - Remove necessity to edit requirejs manually(!!)
  - Pipeline:
    - pipeline for javascript uglification
    - pipeline for css
    - pipeline for html (replace javascript, css links with generated files)
    - serve the frontend from the generated files
  - move to angular 2 when ready
  - move to slick 3.1 when ready


