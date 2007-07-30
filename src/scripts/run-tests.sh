#! /bin/sh
cd /Users/schuler/tmp/mutationTest
mvn assembly:assembly
cd /scratch/schuler/aspectJ/run-all-junit-tests/
../lib/ant/bin/ant test
