package reactive

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.jboss.resteasy.reactive.RestResponse

fun <T> Uni<T>.subscribeAssert(): UniAssertSubscriber<T> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
}

fun <T> Uni<RestResponse<T>>.subscribeRestResponseAssert(): UniAssertSubscriber<RestResponse<T>> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
}
