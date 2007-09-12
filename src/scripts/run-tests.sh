#! /bin/sh
task="mytest"
if [ $1 ]; then
	task=$1
fi
cd /Users/schuler/workspace2/mutationTest
mvn assembly:assembly
cd /scratch/schuler/aspectJ/run-all-junit-tests/
../lib/ant/bin/ant $task
