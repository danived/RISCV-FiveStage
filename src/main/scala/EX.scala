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
      val instruction       = Input(new Instruction)
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val PC                = Input(UInt())
      val branchType        = Input(UInt())
      val op1Select         = Input(UInt())
      val op2Select         = Input(UInt())
      val rs1               = Input(UInt())
      val Rs2               = Input(UInt())
      val immData           = Input(UInt())
      val ALUop             = Input(UInt())
      //Forwarder
      val rdEXB             = Input(UInt())
      val ALUresultEXB      = Input(UInt())
      val rdMEMB            = Input(UInt())
      val ALUresultMEMB     = Input(UInt())


      val ALUResult         = Output(UInt())
      val branchAddr        = Output(UInt())
      val branch            = Output(UInt())
    }
  )

  val ALU    = Module(new ALU).io
  val Branch = Module(new Branch).io

  val alu_operand1 = Wire(UInt())
  val alu_operand2 = Wire(UInt())


  //////////////////////
  // Branch condition //
  //////////////////////

  Branch.branchType := io.branchType
  Branch.op1        := io.rs1
  Branch.op2        := io.Rs2
  io.branch         := Branch.branch


  /////////
  // ALU //
  /////////

  //Operand 1 Mux
  when(io.op1Select === Op1Select.PC){
    alu_operand1    := io.PC
  }.otherwise{
    alu_operand1    := io.rs1
  }

  //Operand 2 Mux
  when(io.op2Select === Op2Select.rs2){
    alu_operand2    := io.Rs2
  }.otherwise{
    alu_operand2    := io.immData
  }

  //ALU
  ALU.op1           :=alu_operand1
  ALU.op2           :=alu_operand2
  ALU.ALUop         :=io.ALUop


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



  //legg til forwarder


}
