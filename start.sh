#!/bin/bash
function echoAndEval() {
  YELLOW='\033[1;33m'
  NC='\033[0m'
    echo -e "${YELLOW}poc_layer % ${NC}$1"
    eval "$1"
}
databaseContainerName='poc_layer_db'

#build project modules
echo 'building project...'
buildCommand='./gradlew clean build --parallel --build-cache'
echoAndEval "$buildCommand"

#sam build for local testing
echo 'build function for local execution...'
samBuildCommand='sam build'
echoAndEval "$samBuildCommand"

#init dynamo db local
echo 'init dynamo db local'
eval "docker stop $databaseContainerName"
dynamoLocalCommand="docker run -d --name=$databaseContainerName -p 8000:8000 amazon/dynamodb-local -jar DynamoDBLocal.jar -inMemory -sharedDb"
echoAndEval "$dynamoLocalCommand"

#create dynamo db table
echo 'create dynamo db table locally'
dynamoTableCommand='AWS_PROFILE=personal aws dynamodb create-table --table-name entity --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=id,AttributeType=S --billing-mode PAY_PER_REQUEST --key-schema AttributeName=pk,KeyType=HASH AttributeName=id,KeyType=RANGE --endpoint-url http://localhost:8000 --region us-west-2'
echoAndEval "$dynamoTableCommand"

#init local api
echo 'starting function locally'
samFunctionCommand='AWS_PROFILE=personal sam local start-api --env-vars env_vars.json --region us-west-2'
echoAndEval "$samFunctionCommand"