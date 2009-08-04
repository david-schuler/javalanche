#! /bin/sh
mvn -Dmaven.test.skip=true   assembly:assembly
DIST=javalanche-0.1
mkdir ${DIST}
cp -r target/javalanche-mutation-0.1-dist.dir/ ${DIST}/

cp mutationTest.xml ${DIST}/
cp import.xml ${DIST}/
mkdir -p ${DIST}/src/main/resources/
cp src/main/resources/mutation-build.xml ${DIST}/src/main/resources/
cp src/main/resources/hibernate.cfg.xml ${DIST}/src/main/resources/
cp ../adabu2-check-invariants/src/main/resources/invariant-build.xml  ${DIST}/src/main/resources/

mkdir -p ${DIST}/examples/triangle
cp ../Triangle/build.xml ${DIST}/examples/triangle/
cp -r ../Triangle/src ${DIST}/examples/triangle/

TAR=javalanche-0.1-bin.tar.gz
tar -cvzf ${TAR} javalanche-0.1
cp ${TAR} src/site/builds/

