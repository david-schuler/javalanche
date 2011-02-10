#! /bin/bash
VERSION=`grep "<version>" pom.xml | head -1 | sed -E 's/.*0/0/' | sed -E 's/<.*//'`
DIST=javalanche-$VERSION
cd ${DIST}/examples/triangle
ant test
OUT1=out-mutationTest.txt
OUT2=out-analyzeResults.txt
cp ../../javalanche.xml .
ant -f javalanche.xml -Djavalanche=../../  startHsql
ant -f javalanche.xml -Djavalanche=../../  schemaexport
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  mutationTest | tee ${OUT1}
ant -f javalanche.xml -Djavalanche=../../  startHsql
ant -f javalanche.xml -Djavalanche=../../  schemaexport
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  testTask1
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  testTask2
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  testTask3
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  scanProject
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  scan
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  createTasks
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  runMutations -Dmutation.file=./mutation-files/mutation-task-triangle-01.txt
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  analyzeResults   | tee ${OUT2}
#TODO test for same output in mutationTest task and single tasks
ant -f javalanche.xml -Dprefix=triangle -Dcp=target/classes/:./lib/junit.jar -Dtests=triangle.tests.TriangleTestSuite -Djavalanche=../../  cleanJavalanche
ant -f javalanche.xml -Djavalanche=../../  schemaexport
ant clean
rm javalanche.xml

SAME="Same output for both runs"
GREP1_1="$(grep "Mutation score:" ${OUT1})"
GREP1_2="$(grep "Mutation score:" ${OUT2})"
if [ "$GREP1_1" == "$GREP1_2" ]
then
	echo  ${SAME}
else
	echo  Error: Mutation score not equal. ${GREP1_2} - ${GREP1_1}
fi

GREP2_1="$(grep "Covered mutations" ${OUT1})"
GREP2_2="$(grep "Covered mutations" ${OUT2})"
if [ "$GREP2_1" == "$GREP2_2" ]
then
	echo  ${SAME}
else
	echo  Error: Covered mutations not equal. ${GREP2_2} - ${GREP2_1}
fi


