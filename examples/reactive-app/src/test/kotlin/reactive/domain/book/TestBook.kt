package reactive.domain.book

import reactive.adapter.BookEntity
import kotlin.random.Random

object TestBook {

    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')
    private val numberPool: List<Number> = (1..9).toList()

    fun get(): Book {
        return Book(
            title = (1..10).map { Random.nextInt(charPool.size) }.map { charPool[it] }.joinToString(""),
            isbn = (1..10).map { Random.nextInt(numberPool.size) }.map { numberPool[it] }.joinToString(""),
            id = (1..2).map { Random.nextInt(numberPool.size) }.map { numberPool[it] }.joinToString("").toLong(),
        )
    }

    fun getEntity(): BookEntity {
        return BookEntity(
            title = (1..10).map { Random.nextInt(charPool.size) }.map { charPool[it] }.joinToString(""),
            isbn = (1..10).map { Random.nextInt(numberPool.size) }.map { numberPool[it] }.joinToString(""),
            id = (1..2).map { Random.nextInt(numberPool.size) }.map { numberPool[it] }.joinToString("").toLong(),
        )
    }
}
