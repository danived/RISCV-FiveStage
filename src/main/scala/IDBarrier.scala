package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class IDBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      //Input to registes - decoder signals
      val inInstruction     = Input(new Instruction)
      val inControlSignals  = Input(new ControlSignals)
      val inPC              = Input(UInt())
      val inBranchType      = Input(UInt(3.W))
      val inOp1Select       = Input(UInt(1.W))
      val inOp2Select       = Input(UInt(1.W))
      val inImmData         = Input(UInt())
      val inRd              = Input(UInt())
      val inALUop           = Input(UInt(4.W))

      //Output from register - decoder signals
      val outInstruction    = Output(new Instruction)
      val outControlSignals = Output(new ControlSignals)
      val outPC             = Output(UInt())
      val outBranchType     = Output(UInt(3.W))
      val outOp1Select      = Output(UInt(1.W))
      val outOp2Select      = Output(UInt(1.W))
      val outImmData        = Output(UInt())
      val outRd             = Output(UInt())
      val outALUop          = Output(UInt(4.W))

      //Input to register - registers signals
      val inReadData1       = Input(UInt())
      val inReadData2       = Input(UInt())

      //Output from register - registers signals
      val outReadData1      = Output(UInt())
      val outReadData2      = Output(UInt())
    }
  )

  //Decoder signal registers
  val instructionReg        = Reg(new Instruction)
  val controlSignalsReg     = Reg(new ControlSignals)
  val branchTypeReg         = RegInit(UInt(), 0.U)
  val PCReg                 = RegInit(UInt(), 0.U)
  val op1SelectReg          = RegInit(UInt(), 0.U)
  val op2SelectReg          = RegInit(UInt(), 0.U)
  val immDataReg            = RegInit(UInt(), 0.U)
  val rdReg                 = RegInit(UInt(), 0.U)
  val ALUopReg              = RegInit(UInt(), 0.U)
  //Register signal registers
  val readData1Reg          = RegInit(UInt(), 0.U)
  val readData2Reg          = RegInit(UInt(), 0.U)

  //Decoder signals registers
  instructionReg       := io.inInstruction
  io.outInstruction    := instructionReg

  controlSignalsReg    := io.inControlSignals
  io.outControlSignals := controlSignalsReg

  branchTypeReg        := io.inBranchType
  io.outBranchType     := branchTypeReg

  PCReg                := io.inPC
  io.outPC             := PCReg

  op1SelectReg         := io.inOp1Select
  io.outOp1Select      := op1SelectReg

  op2SelectReg         := io.inOp2Select
  io.outOp2Select      := op2SelectReg

  immDataReg           := io.inImmData
  io.outImmData        := immDataReg

  rdReg                := io.inRd
  io.outRd             := rdReg

  ALUopReg             := io.inALUop
  io.outALUop          := ALUopReg


  //Register signals registers
  readData1Reg         := io.inReadData1
  io.outReadData1      := readData1Reg

  readData2Reg         := io.inReadData2
  io.outReadData2      := readData2Reg
}
