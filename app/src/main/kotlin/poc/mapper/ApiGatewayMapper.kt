package poc.mapper

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import poc.expections.MethodNotAllowed
import poc.model.Entity
import poc.serializer.EntitySerializable

class ApiGatewayMapper(
        private val jsonMapper: Json
) {

    companion object {
        private const val ROOT = "/entity"
        private const val FIND_ALL_PATH = "$ROOT/all"
    }

    fun toFindEntityById(request: APIGatewayProxyRequestEvent): String {
        return request.pathParameters["id"] ?: throw IllegalArgumentException("id is required")
    }

    fun toDeleteEntityById(request: APIGatewayProxyRequestEvent): String {
        return request.pathParameters["id"] ?: throw IllegalArgumentException("id is required")
    }

    fun validateFindAll(request: APIGatewayProxyRequestEvent) {
        if(request.path != FIND_ALL_PATH) throw throw IllegalArgumentException("invalid path")
    }

    fun toTransientEntity(request: APIGatewayProxyRequestEvent): Entity {
        val transientEntity: EntitySerializable = jsonMapper.decodeFromString(request.body ?: throw IllegalArgumentException("body is required"))
        return transientEntity.to()
    }

    fun ok(entity: Entity): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(jsonMapper.encodeToString(EntitySerializable.from(entity)))
    }

    fun ok(entities: List<Entity>): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(jsonMapper.encodeToString(entities.map { EntitySerializable.from(it) }))
    }

    fun ok(body: String): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(body)
    }
}