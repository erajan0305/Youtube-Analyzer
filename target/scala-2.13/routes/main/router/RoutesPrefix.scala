// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/kishanbhimani/IntellijProjects/Youtube-Analyzer/conf/routes
// @DATE:Sat Oct 24 14:38:56 EDT 2020


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
