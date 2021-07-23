package poc.config

import poc.config.RepositoryConfiguration.getProperty
import poc.repository.DynamoEntityRepository
import poc.repository.EntityRepository
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.lang.UnsupportedOperationException
import java.net.URI
import java.time.Clock

object RepositoryConfiguration {

    private const val profileEnvVar = "PROFILE"
    private const val tableNameEnVar = "TABLE_NAME"

    val repository: EntityRepository by lazy {
        DynamoEntityRepository(
                dynamoDbClient = DynamoClientBuilder(getProperty(profileEnvVar)).build(),
                table = getProperty(tableNameEnVar),
                clock = Clock.systemUTC()
        )
    }

    fun getProperty(key: String): String = System.getProperty(key, System.getenv(key))?.apply {
        if (this.isBlank()) throw IllegalArgumentException("property $key cannot be empty")
    } ?: throw IllegalArgumentException("property $key is required")
}

class DynamoClientBuilder(
        private val profile: String
) {
    companion object {
        private const val local = "LOCAL"
        private const val live = "LIVE"
        private const val dynamoHostEnvVar = "LOCAL_DYNAMO_HOST"
    }

    fun build(): DynamoDbClient =
            when(profile) {
                live -> live()
                local -> local()
                else -> throw UnsupportedOperationException("$profile is not supported")
            }

    private fun live() =
            builder()
                    .endpointOverride(URI.create("https://dynamodb.us-west-2.amazonaws.com"))
                    .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                    .credentialsProvider(credentials())
                    .build()

    private fun local() =
            builder()
                    .credentialsProvider(object: AwsCredentialsProvider {
                        override fun resolveCredentials(): AwsCredentials {
                            return object : AwsCredentials {
                                override fun accessKeyId(): String {
                                    return "foo"
                                }
                                override fun secretAccessKey(): String {
                                    return "foo"
                                }
                            }
                        }
                    }).region(Region.US_WEST_2)
                    .endpointOverride(URI.create(getProperty(dynamoHostEnvVar))).build()

    private fun builder() =
            DynamoDbClient.builder()
                    .region(Region.US_WEST_2)
                    .httpClient(http())

    private fun credentials() = EnvironmentVariableCredentialsProvider.create()

    private fun http() = UrlConnectionHttpClient.builder().build()
}