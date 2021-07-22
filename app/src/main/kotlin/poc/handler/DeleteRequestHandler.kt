package poc.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import poc.mapper.ApiGatewayMapper
import poc.model.Entity
import poc.service.EntityService

class DeleteRequestHandler(
        private val mapper: ApiGatewayMapper,
        private val service: EntityService
): RequestHandlerTemplate<String, Boolean>() {

    override val path: String by lazy { "entity" }

    override val method: String by lazy { "DELETE" }

    override fun apply(entity: String): Boolean {
        return service.delete(entity)
    }

    override fun parseTo(request: APIGatewayProxyRequestEvent): String {
        return mapper.toDeleteEntityById(request)
    }

    override fun parseFrom(response: Boolean): APIGatewayProxyResponseEvent {
        return mapper.ok(response)
    }
}