package reactive

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.AssertSubscriber
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.jboss.resteasy.reactive.RestResponse

fun <T> Multi<T>.subscribeAssert(requested: Long = 0): AssertSubscriber<T> {
    return this.subscribe().withSubscriber(AssertSubscriber.create(requested))
}

fun <T> Uni<T>.subscribeAssert(): UniAssertSubscriber<T> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
}

fun <T> Uni<RestResponse<T>>.subscribeRestResponseAssert(): UniAssertSubscriber<RestResponse<T>> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
}
