package Songbook

import Songbook.utils._

object Build {
  case class Band(file : File) {
    val id = file.stripExtension.name

    private val headline = File.read(file).split("\n").headOption match {
      case Some(s) if s.trim.startsWith("%") => s.trim.drop(1).trim
    }

    val (name, todo) =
      if (headline.startsWith("%"))
        (headline.drop(1).trim, true)
      else (headline, false)

    def latex = if (todo) "\\todoband{" + name + "}{" + id + "}" else "\\band{" + name + "}{" + id + "}"

  }
  def main(args: Array[String]): Unit = {
    def run(s : String*) = ShellCommand.runIn(File("."),s:_*)

    val tf = File.read(File("./songbook.template"))
    val bandfiles = File("./bands/").children.filter(_.getExtension contains "tex")
    val bands = bandfiles.map(Band).sortBy(_.id)
    ShellCommand.run("touch", "main.tex")
    val (main,build) = ((File(".") / "main").addExtension("tex"),(File(".") / "build").addExtension("sh"))

    File.write(main,tf.replace("%%BANDS%%",
        bands.map(_.latex).mkString("\n")
    ))

    run("pdflatex","main")
    run("texlua","songidx.lua","cbmain.sxd", "cbmain.sbx")
    run("texlua","songidx.lua","cbbands.sxd","cbbands.sbx")
    bands.foreach(b => run("texlua","songidx.lua", "bands/" + b.id + ".sxd", "bands/" + b.id + ".sbx"))
    run("pdflatex","main")
  }

}
