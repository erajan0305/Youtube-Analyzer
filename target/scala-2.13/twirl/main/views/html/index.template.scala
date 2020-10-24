
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import java.lang._
import java.util._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.api.data.Field
import play.data._
import play.core.j.PlayFormsMagicForJava._
import scala.jdk.CollectionConverters._

object index extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply():play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.1*/("""<html>
<head>
    <title>Youtube Analyzer</title>
</head>
<body>
<section id="content">
    <h1>Youtube Analyzer</h1>
    <a href="">Channel Information</a>
    <a href="">Similar Content</a>
    <br/><br/>
    <div class="wrapper doc">
        <input type="text" placeholder="Search..">
        <button onclick="">Search</button>
    </div>
    <div></div>
</section>
</body>
</html>"""))
      }
    }
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2020-10-24T14:38:56.840971
                  SOURCE: /Users/kishanbhimani/IntellijProjects/Youtube-Analyzer/app/views/index.scala.html
                  HASH: 340282e843e616ebe20961f4fc8fdf9b4c17c010
                  MATRIX: 989->0
                  LINES: 32->1
                  -- GENERATED --
              */
          