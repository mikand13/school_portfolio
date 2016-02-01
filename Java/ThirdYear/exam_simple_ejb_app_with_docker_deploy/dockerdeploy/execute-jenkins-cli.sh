#!/bin/bash

# Based on https://github.com/Verigreen/jenkins-tomcat-nginx/blob/master/execute-jenkins-cli-commands.sh
# Anders Mikkelsen

echo "CLI: From cli script"

# Wait for Jenkins to load
while ! curl -s --head --request GET http://localhost:7070/ | grep "200 OK" > /dev/null;
do
	echo "CLI: Waiting for jenkins..."
	sleep 10
done

# Wait for Jenkins asset load
echo "CLI: Waiting for jenkins to load assets..."
sleep 10

echo "CLI: Getting jenkins-cli.jar..."

rm jenkins-cli.jar
wget http://localhost:7070/jnlpJars/jenkins-cli.jar

echo "CLI: Importing LMS job"
java -jar jenkins-cli.jar -s http://localhost:7070/ create-job LMS < lms-job.xml

sleep 10
echo "CLI: Finished!"

exit 0