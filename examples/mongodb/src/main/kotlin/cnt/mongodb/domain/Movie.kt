package cnt.mongodb.domain

import io.quarkus.mongodb.panache.common.MongoEntity
import org.bson.types.ObjectId

@MongoEntity(collection = "movies")
data class Movie(
    var id: ObjectId,
    var title: String,
    var publishYear: Int,
    var cast: List<Actor>
)