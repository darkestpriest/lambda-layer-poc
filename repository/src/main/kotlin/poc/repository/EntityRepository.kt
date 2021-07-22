package poc.repository

import poc.model.*

interface EntityRepository {

    fun delete(id: String): DeleteResponse
    fun findBy(id: String): FindByIdResponse
    fun findAll(): FindAllResponse
    fun save(transientEntity: Entity): SaveResponse
}