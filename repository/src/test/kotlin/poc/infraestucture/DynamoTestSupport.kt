package poc.infraestucture

import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import poc.model.DynamoDbField
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

object DynamoTestSupport {

    val startedClient: DynamoDbClient by lazy {
        with(container to client) {
            createTable()
            this.second
        }
    }

    private val container: LocalStackContainer by lazy {
        LocalStackContainer(
                DockerImageName.parse("localstack/localstack:0.12.6")
        )
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .apply { start() }
    }

    private val client: DynamoDbClient by lazy {
        DynamoDbClient
                .builder()
                .endpointOverride(container.getEndpointOverride(LocalStackContainer.Service.DYNAMODB)  )
                .credentialsProvider( StaticCredentialsProvider.create(AwsBasicCredentials.create(container.accessKey, container.secretKey)))
                .region(Region.of(container.region))
                .build()
    }

    const val table = "table"

    private fun createTable() {
        if(isTableCreated()) return
        val createTableRequest = CreateTableRequest
                .builder()
                .tableName(table)
                .attributeDefinitions(
                        AttributeDefinition
                                .builder()
                                .attributeName(DynamoDbField.PK.param)
                                .attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition
                                .builder()
                                .attributeName(DynamoDbField.ID.param)
                                .attributeType(ScalarAttributeType.S).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(100).writeCapacityUnits(100).build())
                .keySchema(
                        KeySchemaElement.builder().attributeName(DynamoDbField.PK.param).keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName(DynamoDbField.ID.param).keyType(KeyType.RANGE).build()
                )
                .build()
        client.createTable(createTableRequest)
        waitForTableToBeCreated()
    }

    private fun isTableCreated(): Boolean {
        return client.listTables().tableNames().contains(table)
    }

    private fun waitForTableToBeCreated() {
        (1..3).find {
            val status = client.describeTable(DescribeTableRequest.builder().tableName(table).build()).table().tableStatus()
            val isReady = status == TableStatus.ACTIVE
            if (!isReady) Thread.sleep(500)
            isReady
        } ?: throw IllegalStateException("table $table is not ready yet")
    }
}