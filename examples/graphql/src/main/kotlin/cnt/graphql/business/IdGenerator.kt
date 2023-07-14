package cnt.graphql.business

import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class IdGenerator {
    fun generateId(): UUID = UUID.randomUUID()
}
