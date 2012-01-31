#! /bin/sh
VERSION=`grep "<version>" pom.xml | head -1 | sed -E 's/.*0/0/' | sed -E 's/<.*//'`
echo "Building version: ${VERSION}"
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

cp -r target/javalanche-${VERSION}-dist/* ${DIST}/

cp javalanche.xml ${DIST}/
cp import.xml ${DIST}/
mkdir -p ${DIST}/src/main/resources/
cp src/main/resources/javalanche-tasks.xml ${DIST}/src/main/resources/
cp src/main/resources/hibernate.cfg.xml ${DIST}/src/main/resources/
cp src/main/resources/log4j.properties ${DIST}/src/main/resources/
cp src/main/resources/javalanche-add-tasks.xml ${DIST}/src/main/resources/
cp src/main/resources/coverage-include.xml ${DIST}/src/main/resources/
#cp ../adabu2-check-invariants/src/main/resources/invariant-build.xml  ${DIST}/src/main/resources/

# Copy example programs to test the generated version of Javalanche
cp -r examples ${DIST}/

rm ${DIST}/javalanche-${VERSION}.jar

# Not needed as it removes necessary .jars from the DIST/lib
#JARJAR=/scratch/schuler/java/jarjar-1.0.jar
#java -jar ${JARJAR} process jarjar-rules.txt ${DIST}/lib/ds-util-0.3.2.1.jar ${DIST}/lib/ds-util-trans.jar
#rm ${DIST}/lib/ds-util-0.3.2.1.jar
#java -jar ${JARJAR} process jarjar-rules.txt ${DIST}/lib/xstream-1.4.1.jar ${DIST}/lib/xstream-trans.jar
#rm ${DIST}/lib/xstream-1.4.1.jar
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
		#rsync  --verbose  --progress --stats --compress --recursive --times --perms --links    target/site/ kubrick:Sites/st_chair/javalanche/ 
	fi
fi


