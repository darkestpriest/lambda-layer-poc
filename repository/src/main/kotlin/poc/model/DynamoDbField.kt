package poc.model

enum class DynamoDbField {
    PK, ID, VALUE, UPDATED_AT;

    val param: String by lazy { this.name.lowercase() }
}