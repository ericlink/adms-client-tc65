#!/bin/bash

/opt/obexftp-tc65/bin/obexftp -t /dev/ttyUSB0 -U sie  \
-C ota  \
-k GlucoMonStationary.jad \
-k GlucoMonStationary.jar \
;

/opt/obexftp-tc65/bin/obexftp -t /dev/ttyUSB0 -U sie  -l ota


