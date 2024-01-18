# Elastic Search

While you can use Elasticsearch as a document store and retrieve documents and their metadata, the real power comes from
being able to easily access the full suite of search capabilities built on the Apache Lucene search engine library.

Elasticsearch provides a simple, coherent REST API for managing your cluster and indexing and searching your data. For
testing purposes, you can use the Elasticsearch client for your language of choice.

# Dictionary

- Index: An index is a collection of documents that share similar characteristics. It is comparable to a database in a
  relational database system. An index is identified by a name and can be configured after it was created.
- Document: A document is the basic unit of data in Elasticsearch. It is stored in JSON format and contains fields that
  represent the actual data. Documents within an index can have different structures.
- Query DSL: Query DSL (Domain Specific Language) is the query language used by Elasticsearch that allows you to
  formulate complex search requests. It supports various types of queries, aggregations, and filters.
- Aggregation: Aggregation is a powerful feature in Elasticsearch that allows you to group and summarize data. It is
  commonly used to extract statistics, sums, averages, and other analytical information from the stored data.
- Search Request: A search request is a query sent to Elasticsearch to find documents from one or more indices. It can
  include
  filters, sorting, and aggregations.
- Pipeline: Pipelines are a way to perform data processing operations before data is written to the index. They can
  include multiple steps, such as extracting, transforming, and loading data (ETL).
- Shard: A shard is a subset of an index. Elasticsearch allows horizontal scaling by splitting an index into multiple
  shards that are distributed across different nodes in the cluster. Each shard is a complete and independent search
  index
- Mapping: Mapping is the process of defining how a document, and the fields it contains, are stored and indexed.
