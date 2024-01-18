package cnt.elasticsearch

object ElasticSearchConsts {
    const val INDEX_NAME = "library"
    const val INDEX_API = "/$INDEX_NAME"
    const val DOC_API = "$INDEX_NAME/_doc/"
    const val CREATE_API = "$INDEX_NAME/_create/"
    const val SEARCH_API = "$INDEX_NAME/_search/"
    const val ANALYSE_API = "$INDEX_NAME/_analyze/"
}
