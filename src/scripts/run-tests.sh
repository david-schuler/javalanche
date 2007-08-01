#! /bin/sh
task="mytest"
if [ $1 ]; then
	task=$1
fi
cd /Users/schuler/workspace2/mutationTest2
mvn assembly:assembly
cd /scratch/schuler/aspectJ/run-all-junit-tests/
../lib/ant/bin/ant $task
