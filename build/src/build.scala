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

  def clear: Unit = {
    println("\033[H\033[2J")
  }
  val max = 40
  def prl(s : String, newline : Boolean = false) = print("\r" + s + (0 to (max-s.length)).toList.map(_ => ' ').mkString("") +
    {if (newline) "\n" else ""})

  def main(args: Array[String]): Unit = {
    def run(s : String*) = ShellCommand.runIn(File("."),s:_*)

    val bandfiles = File("./bands/").children.filter(_.getExtension contains "tex")
    val bands = bandfiles.map(Band).sortBy(_.id)

    val tf = File.read(File("./songbook.template")).replace("%%BANDS%%",
      bands.map(_.latex).mkString("\n")
    )
    ShellCommand.run("touch", "main.tex")
    ShellCommand.run("touch", "mobile.tex")
    val (main,mobile) = (
      (File(".") / "main").addExtension("tex"),
      (File(".") / "mobile").addExtension("tex"))

    File.write(main,tf.replace("%%VERSION%%","\\book"))
    File.write(mobile,tf.replace("%%VERSION%%","\\mobile"))

    clear
    prl("First run: main.pdf")
    run("pdflatex","main")
    prl("First run: mobile.pdf")
    run("pdflatex","mobile")
    prl("First run: Done.",true)
    // run("texlua","songidx.lua","cbmain.sxd", "cbmain.sbx")
    // run("texlua","songidx.lua","cbbands.sxd","cbbands.sbx")
    // bands.foreach(b => run("texlua","songidx.lua", "bands/" + b.id + ".sxd", "bands/" + b.id + ".sbx"))
    prl("Second run: main.pdf")
    run("pdflatex","main")
    prl("Second run: mobile.pdf")
    run("pdflatex","mobile")
    prl("Second run: Done.",true)
  }

}
