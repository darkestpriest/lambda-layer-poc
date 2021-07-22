package poc.service

import org.slf4j.LoggerFactory
import poc.model.Entity
import poc.model.RepositoryResponse
import poc.repository.EntityRepository

class EntityService(
        private val repository: EntityRepository
) {

    companion object {
        private val log = LoggerFactory.getLogger(EntityService::class.java)
    }

    fun delete(id: String): Boolean {
        return repository.delete(id).also {
            logResponse(it)
        }.success
    }

    fun findAll(): List<Entity> {
        return repository.findAll().also{
            logResponse(it)
        }.entities
    }

    fun findBy(id: String): Entity? {
        return repository.findBy(id).also {
            logResponse(it)
        }.entity
    }

    fun save(entity: Entity): Entity {
        return repository.save(entity).also {
            logResponse(it)
        }.entity
    }

    private fun logResponse(response: RepositoryResponse) {
        log.debug("repository response {}", response)
    }

}