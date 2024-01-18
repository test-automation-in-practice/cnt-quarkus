package cnt.elasticsearch

import io.vertx.core.json.JsonObject

data class SearchQueryRequest(val query: JsonObject)
