package poc.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import poc.expections.NotFound
import poc.mapper.ApiGatewayMapper
import poc.model.Entity
import poc.service.EntityService

class FindByIdRequestHandler (
        private val mapper: ApiGatewayMapper,
        private val service: EntityService
): RequestHandlerTemplate<String, Entity>(){
    override val path: String by lazy { "entity" }
    override val method: String by lazy { "GET" }

    override fun apply(entity: String): Entity {
        return service.findBy(entity) ?: throw NotFound(entity)
    }

    override fun parseTo(request: APIGatewayProxyRequestEvent): String {
        return mapper.toFindEntityById(request)
    }

    override fun parseFrom(response: Entity): APIGatewayProxyResponseEvent {
        return mapper.ok(response)
    }
}