package busTrackerApi.config

import org.koin.core.qualifier.named

const val AuthScope = "auth"
const val ResetPasswordScope = "resetPassword"
const val RegisterScope = "register"
val ResetPasswordSignerQualifier = named("Email")
val AuthSignerQualifier = named("Auth")
val RegisterSignerQualifier = named("Register")