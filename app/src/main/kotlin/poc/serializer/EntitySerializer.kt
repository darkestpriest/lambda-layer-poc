package poc.serializer

import kotlinx.serialization.Serializable
import poc.model.Entity

@Serializable
data class EntitySerializable(
        val id: String,
        val value: String,
        val updatedAt: Long? = null
) {
    companion object {
        fun from(entity: Entity): EntitySerializable {
            return EntitySerializable(entity.id, entity.value, entity.updatedAt)
        }
    }

    fun to(): Entity {
        return Entity(id, value).apply {
            updatedAt?.let { withTimestamp(it) }
        }
    }
}
