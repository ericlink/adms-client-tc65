#!/bin/sh

response="init-loop"

while [ "$response" != "" ]; do

  response=$(zenity  --height 400 --width 300 --list  --text "Pick a TC65 script to run" \
    --column "Function" \
  `ls *sh |grep -v menu.sh`
  );

  if [ "$response" != "" ]
  then
    ./$response | tee -a log/`date +%FT%T`.log
  fi

done
