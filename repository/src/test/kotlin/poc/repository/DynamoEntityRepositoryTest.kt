package poc.repository

import com.nhaarman.mockitokotlin2.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import poc.infraestucture.DynamoTestSupport.startedClient
import poc.infraestucture.DynamoTestSupport.table
import poc.model.Entity
import poc.support.TestSupport.randomString
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Clock
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class DynamoEntityRepositoryTest {

    companion object {
        private val dynamoDbClient: DynamoDbClient = startedClient
        private const val tableName: String = table
    }

    @Mock
    private lateinit var clock: Clock

    private lateinit var sut: DynamoEntityRepository

    @Before
    fun setup() {
        sut = DynamoEntityRepository(
                dynamoDbClient, tableName, clock
        )
    }

    @After
    fun cleanUp() {
        sut.findAll().entities.map {
            sut.delete(it.id)
        }
    }

    @Test
    fun `retrieve entity by id`() {
        val expected = savedEntity()
        /** control **/ savedEntity()

        assertEquals(expected, sut.findBy(expected.id).entity)
    }

    @Test
    fun `retrieve all entities`() {
        val expected = 0.rangeTo(5).map {
            savedEntity()
        }.sortedBy { it.id }
        val actual = sut.findAll().entities.sortedBy { it.id }

        assertEquals(expected, actual)
    }

    @Test
    fun `delete entity by id`() {
        val expected = savedEntity()
        /** control **/ savedEntity()

        assertTrue(sut.delete(expected.id).success)
    }

    @Test
    fun `deleted entity is not found anymore`() {
        val toDelete = savedEntity()
        /** control **/ savedEntity()

        sut.delete(toDelete.id)

        assertNull(sut.findBy(toDelete.id).entity)
    }

    private fun savedEntity(): Entity {
        val transientEntity = Entity(
                id = randomString(),
                value = randomString()
        )

        whenever(clock.instant())
                .thenReturn(Instant.now())

        return sut.save(transientEntity).entity
    }
}