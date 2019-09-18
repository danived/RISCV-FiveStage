package FiveStage

import chisel3._
import chisel3.util._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule

import chisel3.experimental._

import ALUOps._

class ALU extends MultiIOModule {
  val io = IO(
    new Bundle {
      val op1     = Input(UInt())
      val op2     = Input(UInt())
      val ALUop   = Input(UInt())

      val result  = Output(UInt())
//      val zero    = Output(Bool())
    }
  )



  //Create alu operations map
  val ALUopMap = Array(
    //Key,       Value
    ADD      -> (io.op1 + io.op2),
    SUB      -> (io.op1 - io.op2),
    OR       -> (io.op1 | io.op2),
    XOR      -> (io.op1 ^ io.op2),
    SLT      -> (io.op1 - io.op2),
    SLL      -> (io.op1 - io.op2),
    SLTU     -> (io.op1 - io.op2),
    SRL      -> (io.op1 - io.op2),
    SRA      -> (io.op1 - io.op2),
    SLTU     -> (io.op1 - io.op2),
    COPY_A   -> (io.op1),
    COPY_B   -> (io.op2),
    DC       -> (io.op1 - io.op2),

  )

  io.result := MuxLookup(io.ALUop, 0.U(32.W), ALUopMap)

}
