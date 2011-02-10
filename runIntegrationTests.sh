#! /bin/bash
cd ../integrationTestMutation
OUT_FILE=out-integration_test.txt
/scratch/schuler/java/apache-ant-1.7.1/bin/ant integrationTest | tee ${OUT_FILE}
RESULT="$(grep "BUILD SUCCESSFUL" ${OUT_FILE})"
if [ -n "${RESULT}" ]; then
	echo "Test passed"
else
	echo "TEST FAILED" #For results see: ${OUT_FILE} 
fi