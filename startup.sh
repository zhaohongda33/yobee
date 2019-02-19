echo off
echo ------------------------------------------------------------------------
echo ------------------------------------------------------------------------
echo ----export path = $JAVA_HOME/bin
echo ----export classpath = $JAVA_HOME/jre/lib
echo ----export java -Xms256m -Xmx512m -Xss512K
echo ------------------------------------------------------------------------
echo ------------------------------------------------------------------------
nohup java -agentlib:jdwp=transport=dt_socket,server=y,address=8099,suspend=n -jar ccby-yobee.jar -Xms256m -Xmx512m -Xss512K  1>log.out 2>err.log &