#! /bin/sh
task="mytest"
if [ $1 ]; then
	task=$1
fi
cd /scratch/schuler/aspectj/run-all-junit-tests/
../lib/ant/bin/ant  $task
