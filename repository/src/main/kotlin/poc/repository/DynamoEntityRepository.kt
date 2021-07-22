package poc.repository

import org.slf4j.LoggerFactory
import poc.model.*
import poc.support.ExtensionFunctions.asSAttribute
import poc.support.ExtensionFunctions.retrieveLong
import poc.support.ExtensionFunctions.retrieveS
import poc.support.ExtensionFunctions.toSAttributeValue
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Clock

internal class DynamoEntityRepository(
        private val dynamoDbClient: DynamoDbClient,
        private val table: String,
        private val clock: Clock
): EntityRepository {

    companion object {
        private val log = LoggerFactory.getLogger(EntityRepository::class.java)
        private const val pk = "ENTITY"
    }

    override fun delete(id: String): DeleteResponse {
        log.trace("about to delete item with id {}", id)
        return dynamoDbClient.deleteItem { it
                .tableName(table)
                .key(id.toKeyMap())
        }.let {
            DeleteResponse(
                    it.sdkHttpResponse().toDatabaseResponse()
            )
        }
    }

    override fun findBy(id: String): FindByIdResponse {
        log.trace("about to find item with id {}", id)
        return dynamoDbClient.getItem { it
                .tableName(table)
                .key(id.toKeyMap())
        }.let { response ->
            response.sdkHttpResponse() to response.takeIf { it.hasItem() }?.item()?.toEntity()
        }.let {
            FindByIdResponse(
                    entity = it.second,
                    databaseResponse = it.first.toDatabaseResponse()
            )
        }
    }

    override fun findAll(): FindAllResponse {
        return dynamoDbClient.query { it
                .tableName(table)
                .keyConditionExpression("#id = :index")
                .expressionAttributeNames(mapOf(
                        "#id" to DynamoDbField.PK.param
                ))
                .expressionAttributeValues(
                        mapOf(":index" to pk.asSAttribute { p -> p })
                )
                .limit(1000)
        }.let {  response ->
            response.sdkHttpResponse() to (response.takeIf { it.hasItems() }?.let {
                it.items().map { e -> e.toEntity() }
            } ?: emptyList())
        }.let {
            FindAllResponse(
                    entities = it.second,
                    databaseResponse = it.first.toDatabaseResponse()
            )
        }
    }

    override fun save(transientEntity: Entity): SaveResponse {
        log.trace("about to save item {}", transientEntity)
        val entity = transientEntity.withTimestamp(clock.instant())
        log.trace("updated item with timestamp {}", entity)
        return (entity to dynamoDbClient.putItem { it
                .tableName(table)
                .item(mapOf(
                        *pk.toSAttributeValue(DynamoDbField.PK),
                        *entity.id.toSAttributeValue(DynamoDbField.ID),
                        *entity.value.toSAttributeValue(DynamoDbField.VALUE),
                        *entity.updatedAt.toSAttributeValue(DynamoDbField.UPDATED_AT)
                ))
        }).let {
            SaveResponse(
                    entity = it.first,
                    databaseResponse = it.second.sdkHttpResponse().toDatabaseResponse()
            )
        }
    }

    private fun String.toKeyMap() = mapOf(
            *pk.toSAttributeValue(DynamoDbField.PK),
            *this.toSAttributeValue(DynamoDbField.ID)
    )

    private fun Map<String, AttributeValue>.toEntity(): Entity {
        return Entity(
                id = this[DynamoDbField.ID].retrieveS(),
                value = this[DynamoDbField.VALUE].retrieveS(),
        ).withTimestamp(this[DynamoDbField.UPDATED_AT].retrieveLong())
    }

    private fun SdkHttpResponse.toDatabaseResponse(): DatabaseResponse = DatabaseResponse(
            this.isSuccessful,
            this.statusCode()
    )

    private operator fun Map<String, AttributeValue>.get(field: DynamoDbField): AttributeValue? = this[field.param]
}