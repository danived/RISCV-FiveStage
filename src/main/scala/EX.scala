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
      val instruction        = Input(new Instruction)
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val PC                 = Input(UInt())
      val branchType         = Input(UInt())
      val op1Select          = Input(UInt())
      val op2Select          = Input(UInt())
      val rs1                = Input(UInt())
      val Rs2                = Input(UInt())
      val immData            = Input(UInt())
      val ALUop              = Input(UInt())
      //Forwarder
      val rdEXB              = Input(UInt())
      val ALUresultEXB       = Input(UInt())
      val rdMEMB             = Input(UInt())
      val ALUresultMEMB      = Input(UInt())


      val ALUResult          = Output(UInt())
      val branchAddr         = Output(UInt())
      val branch             = Output(Bool())
      val freeze             = Output(Bool())
      val Rs1Forwarded       = Output(UInt())
      val Rs2Forwarded       = Output(UInt())
    }
  )

  val ALU          = Module(new ALU).io
  val Branch       = Module(new Branch).io
  val Rs1Forwarder = Module(new Forwarder).io
  val Rs2Forwarder = Module(new Forwarder).io

  val alu_operand1            = Wire(UInt())
  val alu_operand_1_forwarded = Wire(UInt())
  val alu_operand2            = Wire(UInt())
  val alu_operand_2_forwarded = Wire(UInt())
  val alu_result              = Wire(UInt())
  val freeze_rs1              = Wire(Bool())
  val freeze_rs2              = Wire(Bool())

  //////////////////////
  // Branch condition //
  //////////////////////

  Branch.branchType := io.branchType
  Branch.op1        := io.rs1
  Branch.op2        := io.Rs2
  io.branch         := Branch.branch

  ////////////////
  // Forwarders //
  ////////////////
  Rs1Forwarder.regAddr            := io.instruction.registerRs1
  Rs1Forwarder.controlSignalsEXB  := io.controlSignalsEXB
  Rs1Forwarder.controlSignalsMEMB := io.controlSignalsMEMB
  Rs1Forwarder.regData            := io.rs1
  Rs1Forwarder.rdEXB              := io.rdEXB
  Rs1Forwarder.ALUresultEXB       := io.ALUresultEXB
  Rs1Forwarder.rdMEMB             := io.rdMEMB
  Rs1Forwarder.ALUresultMEMB      := io.ALUresultMEMB
  alu_operand_1_forwarded         := Rs1Forwarder.operandData
  freeze_rs1                      := Rs1Forwarder.freeze

  Rs2Forwarder.regAddr            := io.instruction.registerRs2
  Rs2Forwarder.controlSignalsEXB  := io.controlSignalsEXB
  Rs2Forwarder.controlSignalsMEMB := io.controlSignalsMEMB
  Rs2Forwarder.regData            := io.Rs2
  Rs2Forwarder.rdEXB              := io.rdEXB
  Rs2Forwarder.ALUresultEXB       := io.ALUresultEXB
  Rs2Forwarder.rdMEMB             := io.rdMEMB
  Rs2Forwarder.ALUresultMEMB      := io.ALUresultMEMB
  alu_operand_2_forwarded         := Rs2Forwarder.operandData
  freeze_rs2                      := Rs2Forwarder.freeze

  //stall signal to IDBarrier and EXBarrier
  io.freeze := freeze_rs1 | freeze_rs2

  /////////
  // ALU //
  /////////

  //Operand 1 Mux
  when(io.op1Select === Op1Select.PC){
    alu_operand1    := io.PC
  }.otherwise{
    alu_operand1    := alu_operand_1_forwarded
  }

  //Operand 2 Mux
  when(io.op2Select === Op2Select.rs2){
    alu_operand2    := alu_operand_2_forwarded
  }.otherwise{
    alu_operand2    := io.immData
  }

  //output forwarded operands
  io.Rs1Forwarded := alu_operand_1_forwarded
  io.Rs2Forwarded := alu_operand_2_forwarded

  //ALU
  ALU.op1           :=alu_operand1
  ALU.op2           :=alu_operand2
  ALU.ALUop         :=io.ALUop
  alu_result        := ALU.result


  /////////////////
  // BRANCH ADDR //
  /////////////////
  io.branchAddr := alu_result


  /////////////////////////////
  // ALU RESULT / PC + 4 MUX //
  /////////////////////////////
  when(io.branchType === branchType.jump){
    io.ALUResult := io.PC + 4.U
  }.otherwise{
    io.ALUResult := alu_result
  }
}
