#!/bin/sh
MYANT="ant"
if [[ `uname -a | grep -c Darwin` -eq 1 ]]; then
   export JAVA_HOME=/Library/Java/Home/
   MYANT='/scratch/schuler/java/apache-ant-1.7.1/bin/ant'

else
   export JAVA_HOME=/opt/sun-jdk-1.6.0.20/
   MYANT='/scratch/schuler/bin/apache-ant-1.7.1/bin/ant'
fi
OUTPUTFILE=mutation-files/output-runMutations-${2}.txt
BACKOUTPUTFILE=mutation-files/back-output-runMutations-${2}.txt
if [ -e $OUTPUTFILE ]
then
	mv $OUTPUTFILE ${BACKOUTPUTFILE}
fi
NICE_VAL='-10'
TO_DELETE=( ./-1.6 ./-1.5 ./1.4 ./-classpath ./-g ./-d ./-proceedOnError ./-outjar  ./-proceedOnError  ./-showWeaveInfo ./-target ./-XnotReweavable)
NICE_VAL='-10'
X=0
while  ! grep -q ALL_RESULTS ${OUTPUTFILE}
do
        for i in ${TO_DELETE[@]}
        do
                if [ -e  $i ]
                then
                        echo "Deleting ${i} "
                        rm -r ${i}
                fi
        done
        cp Client.javaBACK ../org.aspectj.ajdt.core/testdata/src1/binary/client/Client.java

        DATE=`date`
        echo $DATE
        if [ $X -gt 0 ]
        then
                echo "Task ${2} not completed - Starting again ${X}"
        else
                echo "Starting Task ${2} "
        fi

	nice ${NICE_VAL} $MYANT -f javalanche.xml runMutations -Dsplit.name=asserts-disabled-75-1569 ${3} -Dmutation.file=${1}  2>&1 >> $OUTPUTFILE
        X=$(($X+1))
        sleep 1
done
