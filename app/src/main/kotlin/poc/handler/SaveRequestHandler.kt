package poc.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import poc.mapper.ApiGatewayMapper
import poc.model.Entity
import poc.service.EntityService

class SaveRequestHandler(
        private val mapper: ApiGatewayMapper,
        private val service: EntityService
): RequestHandlerTemplate<Entity, Entity>() {

    override val path: String by lazy { "entity" }

    override val method: String by lazy { "POST" }

    override fun apply(entity: Entity): Entity {
        return service.save(entity)
    }

    override fun parseTo(request: APIGatewayProxyRequestEvent): Entity {
        return mapper.toTransientEntity(request)
    }

    override fun parseFrom(response: Entity): APIGatewayProxyResponseEvent {
        return mapper.ok(response)
    }

}