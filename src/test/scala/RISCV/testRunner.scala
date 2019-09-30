package FiveStage
import org.scalatest.{Matchers, FlatSpec}
import cats._
import cats.implicits._
import fileUtils._

import chisel3.iotesters._
import scala.collection.mutable.LinkedHashMap

import fansi.Str

import Ops._
import Data._
import VM._

import PrintUtils._
import LogParser._

case class TestOptions(
  printIfSuccessful  : Boolean,
  printErrors        : Boolean,
  printParsedProgram : Boolean,
  printVMtrace       : Boolean,
  printVMfinal       : Boolean,
  printMergedTrace   : Boolean,
  nopPadded          : Boolean,
  breakPoints        : List[Int], // Not implemented
  testName           : String
)

case class TestResult(
  regError   : Option[String],
  memError   : Option[String],
  program    : String,
  vmTrace    : String,
  vmFinal    : String,
  sideBySide : String
)

object TestRunner {

  def run(testOptions: TestOptions): Boolean = {

    val testResults = for {
      lines                           <- fileUtils.readTest(testOptions)
      program                         <- FiveStage.Parser.parseProgram(lines, testOptions)
      (binary, (trace, finalVM))      <- program.validate.map(x => (x._1, x._2.run))
      (termitationCause, chiselTrace) <- ChiselTestRunner(
                                           binary.toList.sortBy(_._1.value).map(_._2),
                                           program.settings,
                                           finalVM.pc,
                                           1500)
    } yield {
      val traces = mergeTraces(trace, chiselTrace).map(x => printMergedTraces((x), program))
      say (printBinary(binary))
      val programString = printProgram(program)
      val vmTraceString = printVMtrace(trace, program)
      val vmFinalState = finalVM.regs.show
      val traceString = printLogSideBySide(trace, chiselTrace, program)

      val regError = compareRegs(trace, chiselTrace)
      val memError = compareMem(trace, chiselTrace)

      TestResult(
        regError,
        memError,
        programString,
        vmTraceString,
        vmFinalState.toString,
        traceString)
    }

    testResults.left.foreach{ error =>
      say(s"Test was unable to run due to error: $error")
    }

    testResults.map{ testResults =>
      val successful = List(testResults.regError, testResults.memError).flatten.headOption.map(_ => false).getOrElse(true)
      if(successful)
        say(s"${testOptions.testName} succesful")
      else
        say(s"${testOptions.testName} failed")

      if(testOptions.printIfSuccessful && successful){
        if(testOptions.printParsedProgram) say(testResults.program)
        if(testOptions.printVMtrace)       say(testResults.vmTrace)
        if(testOptions.printVMfinal)       say(testResults.vmFinal)
        if(testOptions.printMergedTrace)   say(testResults.sideBySide)
      }
      else{
        if(testOptions.printErrors){
          say(testResults.regError.map(_.show.toString).getOrElse("no reg errors"))
          say(testResults.memError.map(_.show.toString).getOrElse("no mem errors"))
        }
        if(testOptions.printParsedProgram) say(testResults.program)
        if(testOptions.printVMtrace)       say(testResults.vmTrace)
        if(testOptions.printVMfinal)       say(testResults.vmFinal)
        if(testOptions.printMergedTrace)   say(testResults.sideBySide)
      }
      successful
    }.toOption.getOrElse(false)
  }
}
