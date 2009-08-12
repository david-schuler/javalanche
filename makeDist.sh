#! /bin/sh
mvn -Dmaven.test.skip=true   assembly:assembly
VERSION=0.2
DIST=javalanche-$VERSION
mkdir ${DIST}
cp -r target/javalanche-mutation-${VERSION}-dist.dir/ ${DIST}/

cp javalanche.xml ${DIST}/
cp import.xml ${DIST}/
mkdir -p ${DIST}/src/main/resources/
cp src/main/resources/mutation-build.xml ${DIST}/src/main/resources/
cp src/main/resources/hibernate.cfg.xml ${DIST}/src/main/resources/
cp src/main/resources/log4j.properties ${DIST}/src/main/resources/
cp src/main/resources/coverage-include.xml ${DIST}/src/main/resources/
cp ../adabu2-check-invariants/src/main/resources/invariant-build.xml  ${DIST}/src/main/resources/

mkdir -p ${DIST}/examples/triangle
cp ../Triangle/build.xml ${DIST}/examples/triangle/ 
cp -r ../Triangle/src ${DIST}/examples/triangle/
cp -r ../Triangle/lib ${DIST}/examples/triangle/

mkdir -p ${DIST}/examples/invariantExample
cp ../InvariantExample/build.xml ${DIST}/examples/invariantExample/
cp -r ../InvariantExample/src ${DIST}/examples/invariantExample/

rm ${DIST}/javalanche-mutation-${VERSION}.jar

if [  $1 ]; then
 if [ $1 == "tgz"  ]
	then
		echo "Generating tgz"
		TAR=javalanche-${VERSION}-bin.tar.gz
		tar -cvzf ${TAR} javalanche-${VERSION}
		cp ${TAR} src/site/builds/
	fi
fi


