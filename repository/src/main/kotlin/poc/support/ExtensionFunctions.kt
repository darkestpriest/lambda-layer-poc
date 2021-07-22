package poc.support

import poc.model.DynamoDbField
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

internal object ExtensionFunctions {

    fun AttributeValue?.retrieveLong(): Long = this?.s()?.toLong()!!
    fun AttributeValue?.retrieveS(): String = this?.s()!!
    fun Any?.toSAttributeValue(field: DynamoDbField) =
            (if (this != null) arrayOf(field.param to this.asSAttribute { it.toString() }) else arrayOf())

    fun <T> T.asSAttribute(transform: (T) -> String): AttributeValue = AttributeValue.builder().s(transform.invoke(this)).build()
}