#! /bin/sh
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
tar -cvzf javalanche-src.tgz  javalanche-src/

