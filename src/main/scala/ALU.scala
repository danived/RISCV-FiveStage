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

      val result  = Output(UInt(32.W))
    }
  )

  val ALU_SLT = Wire(UInt())
  val ALU_SLTU = Wire(UInt())


  //ALU_SLT operation
  when (io.op1.asSInt < io.op2.asSInt){
    ALU_SLT := 1.U
  }.otherwise{
    ALU_SLT := 0.U
  }

  //ALU_SLTU operation
  when (io.op1 < io.op2){
    ALU_SLTU:= 1.U
  }.otherwise{
    ALU_SLTU:= 0.U
  }

 
  //Create alu operations map
  val ALUopMap = Array(
    //Key,       Value
    ADD      -> (io.op1 + io.op2),
    SUB      -> (io.op1 - io.op2),
    AND      -> (io.op1 & io.op2),
    OR       -> (io.op1 | io.op2),
    XOR      -> (io.op1 ^ io.op2),
    SLT      -> ALU_SLT,
    SLL      -> (io.op1 << io.op2(4,0)),
    SLTU     -> ALU_SLTU,
    SRL      -> (io.op1 >> io.op2(4,0)),
    SRA      -> (io.op1.asSInt >> io.op2(4,0)).asUInt,
    INC_4    -> (io.op1 + 4.U),
    COPY_B   -> (io.op2),
    DC       -> (io.op1 - io.op2),

  )

  //set output signal based on the result of the expression in the muxlookup table
  io.result := MuxLookup(io.ALUop, 0.U(32.W), ALUopMap)

}
