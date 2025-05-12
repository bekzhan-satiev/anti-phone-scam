package kg.digitalshield.navigation

private object Route {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CALL_DETAILED = "call/{id}"
    const val STATISTIC = "statistic"
    const val SEARCH = "search"
}

sealed class Screen(val route: String) {
    data object Login : Screen(Route.LOGIN)
    data object Register : Screen(Route.REGISTER)
    data object Home : Screen(Route.HOME)
    data object CallDetailed : Screen(Route.CALL_DETAILED)
    data object Statistic : Screen(Route.STATISTIC)
    data object Search : Screen(Route.SEARCH)
}