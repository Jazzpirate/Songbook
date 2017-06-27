package Songbook

import Songbook.utils._

object Build {
  case class Band(file : File) {
    val id = file.stripExtension.name
    val name = File.read(file).split("\n").head.drop(1).trim
  }
  def main(args: Array[String]): Unit = {
    def run(s : String*) = ShellCommand.runIn(File("."),s:_*)

    val tf = File.read(File("./songbook.template"))
    val bandfiles = File("./bands/").children.filter(_.getExtension contains "tex")
    val bands = bandfiles.map(Band).sortBy(_.id)
    ShellCommand.run("touch", "main.tex")
    val (main,build) = ((File(".") / "main").addExtension("tex"),(File(".") / "build").addExtension("sh"))

    File.write(main,tf.replace("%%BANDS%%",
        bands.map(b => "\\band{" + b.name + "}{" + b.id + "}").mkString("\n")
    ))

    run("pdflatex","main")
    run("texlua","songidx.lua","cbmain.sxd", "cbmain.sbx")
    run("texlua","songidx.lua","cbbands.sxd","cbbands.sbx")
    bands.foreach(b => run("texlua","songidx.lua", "bands/" + b.id + ".sxd", "bands/" + b.id + ".sbx"))
    run("pdflatex","main")
  }

}
