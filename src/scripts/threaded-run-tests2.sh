#! /bin/sh
prop=""
prop2=""
if [ $1 ]; then
	prop=$1
fi
if [ $2 ]; then
	prop2=$2
fi
echo $prop
echo $prop2
cd /scratch/schuler/aspectj/run-all-junit-tests/
../lib/ant/bin/ant -v mytest $prop $prop2


