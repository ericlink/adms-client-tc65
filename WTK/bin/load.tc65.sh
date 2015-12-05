#!/bin/bash

/opt/obexftp-tc65/bin/obexftp -t /dev/ttyUSB0 -U sie  \
-C ota  \
-k GlucoMonStationary.jad \
-k GlucoMonStationary.jar \
-p ota/GlucoMonStationary.jad \
-p ota/GlucoMonStationary.jar \
;


sleep 2

/opt/obexftp-tc65/bin/obexftp -t /dev/ttyUSB0 -U sie  -l 
