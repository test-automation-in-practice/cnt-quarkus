package cnt.elasticsearch

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.JsonObject

data class ElasticSearchIndex(
    val settings: Settings,
    // it is recommended to define the mapping for the data structure
    val mappings: Mappings
) {
    fun toJson(): String {
        return JsonObject.mapFrom(this).toString()
    }
}

data class Settings(
    // Index size is a common cause of Elasticsearch crashes. Since there is no limit to how many documents you can store on each index,
    // an index may take up an amount of disk space that exceeds the limits of the hosting server. As soon as an index approaches this limit,
    // indexing will begin to fail. One way to counter this problem is to split up indices horizontally into pieces called shards .
    // This allows you to distribute operations across shards and nodes to improve performance.
    @JsonProperty("number_of_shards") val numberOfShards: Int,
    // Default is 1000. As I use explicit mapping in this example I can surely define a mapping limit
    // just to be sure to not run into any troubles like mapping explosion
    // Source: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html#mapping-limit-settings
    @JsonProperty("index.mapping.total_fields.limit") val totalMappingFieldsLimit: Int = 1000,
    val analysis: Analysis
)

data class Analysis(
    @JsonProperty("char_filter") val charFilter: Map<String, CharFilter>,
    val filter: Map<String, Filter>,
    val analyzer: Map<String, Analyzer>
)

data class CharFilter(
    val type: String,
    val mappings: List<String>
)

data class Filter(
    val type: String
)

data class Analyzer(
    val type: String,
    @JsonProperty("char_filter") val charFilter: List<String>,
    val tokenizer: String,
    val filter: List<String>
)

data class Mappings(
    val properties: Map<String, Property>
)

data class Property(
    val type: String,
    val analyzer: String
)
