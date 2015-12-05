#!/bin/bash

cd /tmp
rm logfile.0 
rm logfile.1

/opt/obexftp-tc65/bin/obexftp -t /dev/ttyUSB0 -U sie  -G logfile.1

/opt/obexftp-tc65/bin/obexftp -t /dev/ttyUSB0 -U sie  -G logfile.0

ls -lt logfile.*

touch logfile.0 
touch logfile.1

gvim /tmp/logfile.0 /tmp/logfile.1 



