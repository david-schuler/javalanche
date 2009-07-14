#! /bin/sh
prop=""
prop2=""
prop3=""
if [ $1 ]; then
	prop=$1
fi
if [ $2 ]; then
	prop2=$2
fi

if [ $3 ]; then
	prop3=$3
fi
echo $prop
echo $prop2
echo $prop3
echo $4
export DISPLAY=:0.0
export CLASSPATH=/scratch/schuler/jtopas/ant-junit.jar:/scratch/schuler/jtopas/source/lib/junit.jar 
#cd $4/run-all-junit-tests/
#ant mytest $prop $prop2 $prop3
cd /scratch/schuler/jtopas/
ant runMutations $*

#../lib/ant/bin/ant -h
#ls -l
#../lib/ant/bin/ant -h
#cd ..
#cd lib/ant/bin/
#./ant -h
#ls -l
# test-no-compile $prop $prop2 $prop3


