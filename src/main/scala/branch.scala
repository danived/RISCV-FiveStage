package FiveStage
import chisel3._
import chisel3.util._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

import chisel3.experimental._

import branchType._

class BranchConditionCheck extends MultiIOModule {

  val io = IO(
    new Bundle {
      val branchType         = Input(UInt())
      val op1                = Input(UInt())
      val op2                = Input(UInt())

      val branchConditionMet = Output(UInt())
    }
  )
 
  //Branch if expressions are true
  //Branch lookup
  val branchMap = Array(
    //Key,       Value
    beq      -> (io.op1 === io.op2),
    neq      -> (io.op1 =/= io.op2),
    gte      -> (io.op1.asSInt >= io.op2.asSInt),
    lt       -> (io.op1.asSInt < io.op2.asSInt),
    gteu     -> (io.op1 >= io.op2),
    ltu      -> (io.op1.asSInt < io.op2.asSInt),
    jump     -> 1.U,
    DC       -> 0.U,

  )

  io.branchConditionMet := MuxLookup(io.branchType, 0.U(1.W), branchMap)
}

