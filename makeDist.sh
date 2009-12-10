#! /bin/sh
VERSION=0.3.5
DIST=javalanche-$VERSION

if [  $1 ]; then
 if [ $1 == "tgz"  ]
  then
 	mvn clean
	mvn site
	rm -r ${DIST}
 fi
fi
mvn -Dmaven.test.skip=true   assembly:assembly
mkdir ${DIST}


cp -r target/javalanche-mutation-${VERSION}-dist.dir/ ${DIST}/

cp javalanche.xml ${DIST}/
cp import.xml ${DIST}/
mkdir -p ${DIST}/src/main/resources/
cp src/main/resources/mutation-build.xml ${DIST}/src/main/resources/
cp src/main/resources/hibernate.cfg.xml ${DIST}/src/main/resources/
cp src/main/resources/log4j.properties ${DIST}/src/main/resources/
cp src/main/resources/mutation-add-tasks.xml ${DIST}/src/main/resources/
cp src/main/resources/coverage-include.xml ${DIST}/src/main/resources/
cp ../adabu2-check-invariants/src/main/resources/invariant-build.xml  ${DIST}/src/main/resources/

mkdir -p ${DIST}/examples/triangle
cp ../Triangle/build.xml ${DIST}/examples/triangle/ 
cp -r ../Triangle/src ${DIST}/examples/triangle/
cp -r ../Triangle/lib ${DIST}/examples/triangle/


mkdir -p ${DIST}/examples/triangleJunit4
cp ../TriangleJunit4/build.xml ${DIST}/examples/triangleJunit4/ 
cp -r ../TriangleJunit4/src ${DIST}/examples/triangleJunit4/
cp -r ../TriangleJunit4/lib ${DIST}/examples/triangleJunit4/

mkdir -p ${DIST}/examples/invariantExample
cp ../InvariantExample/build.xml ${DIST}/examples/invariantExample/
cp -r ../InvariantExample/src ${DIST}/examples/invariantExample/

rm ${DIST}/javalanche-mutation-${VERSION}.jar
#rm ${DIST}/lib/daikon-local.jar
if [  $1 ]; then
 if [ $1 == "tgz"  ]
	then
		echo "Generating tgz"
		TAR=javalanche-${VERSION}-bin.tar.gz
		tar -czf ${TAR} javalanche-${VERSION}
		cp ${TAR} src/site/builds/
		cp ${TAR} target/site/builds/
		SRCDIR=target/javalanche-src/
		mkdir ${SRCDIR}
		cp -r src ${SRCDIR}
		cp pom.xml ${SRCDIR}
		cp mavenAnt.xml ${SRCDIR}
		cp src/main/resources/COPYING ${SRCDIR}
		cp src/main/resources/COPYING.LESSER ${SRCDIR}
		rm  ${SRCDIR}/src/dist*
		rm -rf ${SRCDIR}/src/attic
		rm -rf ${SRCDIR}/src/site
		rm -rf ${SRCDIR}/src/main/doc
		rm -rf ${SRCDIR}/src/main/java/de/unisb/cs/st/javalanche/coverage/experiment
		find ${SRCDIR} -name ".svn" | xargs rm -rf 
		find ${SRCDIR} -name "*~" | xargs rm  
		cd target
		TAR_SRC=javalanche-${VERSION}-src.tar.gz
		tar -czf ${TAR_SRC}  javalanche-src/     
		cp ${TAR_SRC} ../src/site/builds/
		cp ${TAR_SRC} site/builds/
		cd ..
		rsync  --verbose  --progress --stats --compress --recursive --times --perms --links    target/site/ kubrick:Sites/st_chair/javalanche/ 
	fi
fi


