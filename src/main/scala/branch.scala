package FiveStage
import chisel3._
import chisel3.util._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

import chisel3.experimental._

import branchType._

class Branch extends MultiIOModule {

  val io = IO(
    new Bundle {
      val PC         = Input(UInt())
      val imm        = Input(UInt())
      val branchType = Input(UInt())
      val op1        = Input(UInt())
      val op2        = Input(UInt())


      val branchAddr = Output(UInt())
      val branch     = Output(UInt())
    }
  )


  //calculate branch addr
  io.branchAddr := io.PC + io.imm

 
  //Create alu operations map
  val branchMap = Array(
    //Key,       Value
    beq      -> (io.op1 === io.op2),
    neq      -> (io.op1 =/= io.op2),
    gte      -> (io.op1.asSInt >= io.op2.asSInt),
    lt       -> (io.op1.asSInt < io.op2.asSInt),
    gteu     -> (io.op1 >= io.op2),
    ltu      -> (io.op1.asSInt < io.op2.asSInt),
//    jump     -> (io.op1 << io.op2(4,0)),
    DC       -> (io.op1 - io.op2),

  )

  io.branch := MuxLookup(io.branchType, 0.U(1.W), branchMap)



}

