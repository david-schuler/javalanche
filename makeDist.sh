#! /bin/sh
if [  $1 ]; then
 if [ $1 == "tgz"  ]
  then
 	mvn clean
	mvn site
 fi
fi
mvn -Dmaven.test.skip=true   assembly:assembly
VERSION=0.3.2
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


mkdir -p ${DIST}/examples/triangleJunit4
cp ../TriangleJunit4/build.xml ${DIST}/examples/triangleJunit4/ 
cp -r ../TriangleJunit4/src ${DIST}/examples/triangleJunit4/
cp -r ../TriangleJunit4/lib ${DIST}/examples/triangleJunit4/
rm ${DIST}/examples/triangleJunit4/src/triangle/TESTJUNIT4.java

mkdir -p ${DIST}/examples/invariantExample
cp ../InvariantExample/build.xml ${DIST}/examples/invariantExample/
cp -r ../InvariantExample/src ${DIST}/examples/invariantExample/

rm ${DIST}/javalanche-mutation-${VERSION}.jar
rm ${DIST}/lib/junit-4.1.jar
#rm ${DIST}/lib/daikon-local.jar
if [  $1 ]; then
 if [ $1 == "tgz"  ]
	then
		echo "Generating tgz"
		TAR=javalanche-${VERSION}-bin.tar.gz
		tar -cvzf ${TAR} javalanche-${VERSION}
		cp ${TAR} src/site/builds/
		
		SRCDIR=target/javalanche-src/
		mkdir ${SRCDIR}
		cp -r src ${SRCDIR}
		cp pom.xml ${SRCDIR}
		cp mavenAnt.xml ${SRCDIR}
		rm  ${SRCDIR}/src/dist*
		rm -rf ${SRCDIR}/src/attic
		rm -rf ${SRCDIR}/src/site
		rm -rf ${SRCDIR}/src/main/doc
		find ${SRCDIR} -name ".svn" | xargs rm -rf 
		cd target
		tar -cvzf javalanche-${VERSION}-src.tar.gz  javalanche-src/     
		cp ${TAR} src/site/resources/builds/
		cd ..
		rsync -r target/site/ ~/Sites/st_chair/javalanche/
	fi
fi


