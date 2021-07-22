package poc.config

import kotlinx.serialization.json.Json
import poc.config.RepositoryConfiguration.repository
import poc.handler.*
import poc.mapper.ApiGatewayMapper
import poc.service.EntityService

object Configuration {

    private val jsonMapper: Json by lazy {
        Json
    }

    val requestHandlers: Map<String, List<RequestHandlerTemplate<*, *>>> by lazy {
        with(mapper to service) {
            mapOf(
                    "GET" to listOf(FindAllRequestHandler(first, second), FindByIdRequestHandler(first, second)),
                    "DELETE" to listOf(DeleteRequestHandler(first, second)),
                    "POST" to listOf(SaveRequestHandler(first, second))
            )
        }
    }

    private val service: EntityService by lazy { EntityService(repository) }
    private val mapper: ApiGatewayMapper by lazy { ApiGatewayMapper(jsonMapper) }
}