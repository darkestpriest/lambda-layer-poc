package poc.model

interface RepositoryResponse {
    val databaseResponse: DatabaseResponse
}

data class SaveResponse(
        val entity: Entity,
        override val databaseResponse: DatabaseResponse
): RepositoryResponse

data class FindByIdResponse(
        val entity: Entity?,
        override val databaseResponse: DatabaseResponse
): RepositoryResponse

data class FindAllResponse(
        val entities: List<Entity>,
        override val databaseResponse: DatabaseResponse
): RepositoryResponse

data class DeleteResponse(
        override val databaseResponse: DatabaseResponse
): RepositoryResponse {
    val success: Boolean by lazy { databaseResponse.isSuccessful }
}

data class DatabaseResponse(
        val isSuccessful: Boolean,
        val statusCode: Int? = null,
        val message: String? = null
)