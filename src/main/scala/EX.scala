package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

import Op1Select._
import Op2Select._
import branchType._

class Execute extends MultiIOModule {

  val io = IO(
    new Bundle {
      val PC           = Input(UInt())
      val branchType   = Input(UInt())
      val op1Select    = Input(UInt())
      val op2Select    = Input(UInt())
      val regA         = Input(UInt())
      val regB         = Input(UInt())
      val immData      = Input(UInt())
      val ALUop        = Input(UInt())

      val ALUResult    = Output(UInt())
      val branchAddr   = Output(UInt())
      val branch       = Output(UInt())
    }
  )

  val ALU    = Module(new ALU).io
  val Branch = Module(new Branch).io

  val alu_operand1 = Wire(UInt())
  val alu_operand2 = Wire(UInt())

  


  //adder for calculating the branc PC
//  Branch.PC         := io.PC
//  Branch.imm        := io.immData
  Branch.branchType := io.branchType
  Branch.op1        := io.regA
  Branch.op2        := io.regB
//  io.branchAddr     := Branch.branchAddr
  io.branch         := Branch.branch


  /////////
  // ALU //
  /////////

  //Operand 1 Mux
  when(io.op1Select === Op1Select.PC){
    alu_operand1    := io.PC
  }.otherwise{
    alu_operand1    := io.regA
  }

  //Operand 2 Mux
  when(io.op2Select === Op2Select.rs2){
    alu_operand2    := io.regB
  }.otherwise{
    alu_operand2    := io.immData
  }

  //ALU
  ALU.op1           :=alu_operand1
  ALU.op2           :=alu_operand2
  ALU.ALUop         :=io.ALUop
//  io.ALUResult      :=ALU.result
  //  0.U           :=ALU.zero


  /////////////////
  // BRANCH ADDR //
  /////////////////
  io.branchAddr := ALU.result


  /////////////////////////////
  // ALU RESULT / PC + 4 MUX //
  /////////////////////////////
  when(io.branchType === branchType.jump){
    io.ALUResult := io.PC + 4.U
  }.otherwise{
    io.ALUResult := ALU.result
  }

}
