#! /bin/sh
ps u | grep $1 | awk {"print \$2 "} 
