package busTrackerApi.extensions

import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.Qualifier

inline fun <reified T : Any> inject() = lazy { GlobalContext.get().get<T>() }
inline fun <reified T : Any> inject(qualifier: Qualifier) = lazy { GlobalContext.get().get<T>(qualifier) }
