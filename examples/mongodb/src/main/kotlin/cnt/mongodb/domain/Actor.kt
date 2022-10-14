package cnt.mongodb.domain

import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.types.ObjectId

@MongoEntity
data class Actor(
    var id: ObjectId,
    var name: String,
    var inAs: String
)
