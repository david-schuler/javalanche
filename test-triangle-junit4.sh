#! /bin/bash
VERSION=`grep "<version>" pom.xml | head -1 | sed -E 's/.*0/0/' | sed -E 's/<.*//'`
DIST=javalanche-$VERSION

# Set up test example
cd ${DIST}/examples/triangleJunit4
ant test
cp ../../javalanche.xml .

# Variables representing the two test's properties and their output locations
OUT1=out-mutationTest-1.txt
OUT2=out-mutationTest-2.txt
sed -iBACK 's/<project name="Javalanche">/& \
    <property name="prefix" value="triangle"\/> \
    <property name="tests" value="triangle.tests.TriangleTestSuite"\/> \
    <property name="javalanche" value="..\/..\/"\/> \
    <property name="cp" value="target\/classes"\/> /' javalanche.xml
JAVALANCHE_ARGS="-Dprefix=triangle -Dcp=target/classes -Djavalanche=../../ -Dtests=triangle.tests.Triangle1Test:triangle.tests.Triangle2Test:triangle.tests.Triangle3Test"

# Test 1: using javalanche.xml properties with JUnit TestSuite
ant -f javalanche.xml $JAVALANCHE_ARGS mutationTest | tee ${OUT1}

# Test 2: using CLI arguments with JUnit Tests (composes testsuite)
ant -f javalanche.xml mutationTest | tee ${OUT2}

# Clean up test
ant clean
rm javalanche.xml

# Variables required for the comparison of the output
SAME="Successful: Same output for both runs"
GREP1_1="$(grep "Mutation score:" ${OUT1})"
GREP1_2="$(grep "Mutation score:" ${OUT2})"
GREP2_1="$(grep "Covered mutations" ${OUT1})"
GREP2_2="$(grep "Covered mutations" ${OUT2})"

# Output the test results
echo "-------------------------------------"
echo "Testing Mutation Score"
if [ "$GREP1_1" == "$GREP1_2" ]
then
	echo  ${SAME}
else
	echo  Error: Mutation score not equal. ${GREP1_2} - ${GREP1_1}
fi

echo ""
echo "Testing Covered Mutations"
if [ "$GREP2_1" == "$GREP2_2" ]
then
	echo  ${SAME}
else
	echo  Error: Covered mutations not equal. ${GREP2_2} - ${GREP2_1}
fi

# Clean up output files
rm ${OUT1}
rm ${OUT2}
