package Songbook.utils

import java.io._

object ShellCommand {
  private def runInOpt(dir: Option[File], command: String*)(out: String => Unit): (Boolean,String) = {
    val pb = new java.lang.ProcessBuilder(command: _*) // use .inheritIO() for debugging
    pb.redirectErrorStream(true)
    dir.foreach { d => pb.directory(d.toJava) }
    val proc = try {
       pb.start()
    } catch {case e: java.io.IOException =>
       throw new Error("error while trying to run external process")
    }

    // read all output immediately to make sure proc does not deadlock if buffer size is limited
    val output = new StringBuilder
    val outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream))
    var line: Option[String] = None
    while ( {
      line = Option(outputReader.readLine)
      line.isDefined
    }) {
      output.append(line.get + "\n")
      out(line.get)
    }

    proc.waitFor
    outputReader.close()

    val ev = proc.exitValue
    (ev == 0,output.result)
  }

  /**
   * @param command the command to run
   * @return output+error message if not successful
   */
  def run(command: String*) = runInOpt(None, command: _*){_ => ()}

  /**
   * like run
   * @param dir the directory in which to run the command
   */
  def runIn(dir: File, command: String*) = runInOpt(Some(dir), command: _*){_=>()}

  /**
   * like runIn but prints output
   */
  def runAndPrint(dir: Option[File], command: String*) = {
     dir.foreach {d => println("running in "+d)}
     println(command.mkString(" "))
     runInOpt(dir, command: _*){s => println(s)}
  }
}
