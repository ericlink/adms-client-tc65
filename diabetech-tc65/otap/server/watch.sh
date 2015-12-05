#!/bin/bash
watch 'sudo tail -n 10000 /var/log/apache2/www.diabetech.net-access_log|grep otap|tail -n 5;tail -n 2  ./notify/notify.log;'


