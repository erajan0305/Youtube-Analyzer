// @GENERATOR:play-routes-compiler
// @SOURCE:/home/akash/Rajan/SOEN 6441/youtube-analyzer/conf/routes
// @DATE:Sat Oct 24 10:57:34 MDT 2020


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
