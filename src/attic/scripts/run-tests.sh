#! /bin/sh
task="mytest"
if [ $1 ]; then
	task=$1
fi
prop=""
if [ $2 ]; then
	prop=$2
fi
cd /scratch/schuler/aspectj/run-all-junit-tests/
../lib/ant/bin/ant  $task $prop
