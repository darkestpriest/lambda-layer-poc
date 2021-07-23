package poc.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import poc.mapper.ApiGatewayMapper
import poc.model.Entity
import poc.service.EntityService

class FindAllRequestHandler(
        private val mapper: ApiGatewayMapper,
        private val service: EntityService
): RequestHandlerTemplate<Unit, List<Entity>>() {

    override val path: String by lazy { "/entity/all" }
    override val method: String by lazy { "GET" }

    override fun apply(entity: Unit): List<Entity> {
        return service.findAll()
    }

    override fun parseTo(request: APIGatewayProxyRequestEvent) {
        mapper.validateFindAll(request)
    }

    override fun parseFrom(response: List<Entity>): APIGatewayProxyResponseEvent {
        return mapper.ok(response)
    }
}