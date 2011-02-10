#! /bin/bash
VERSION=`grep "<version>" pom.xml | head -1 | sed -E 's/.*0/0/' | sed -E 's/<.*//'`
DIST=javalanche-$VERSION
cd ${DIST}/examples/triangleJunit4
JAVALANCHE_ARGS="-Dprefix=triangle -Dcp=target/classes -Djavalanche=../../ -Dtests=triangle.tests.Triangle1Test:triangle.tests.Triangle2Test:triangle.tests.Triangle3Test"
ant test
OUT1=out-mutationTest-1.txt
OUT2=out-mutationTest-2.txt
cp ../../javalanche.xml .
ant -f javalanche.xml $JAVALANCHE_ARGS mutationTest | tee ${OUT1}
sed -iBACK 's/<project name="Javalanche">/& \
    <property name="prefix" value="triangle"\/> \
    <property name="tests" value="triangle.tests.TriangleTestSuite"\/> \
    <property name="javalanche" value="..\/..\/"\/> \
    <property name="cp" value="target\/classes"\/> /' javalanche.xml 
ant -f javalanche.xml mutationTest | tee ${OUT2}
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


