package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class IDBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      //Input to registes - decoder signals
      val inControlSignals  = Input(new ControlSignals)
      val inBranchType      = Input(UInt(3.W))
      val inOp1Select       = Input(UInt(1.W))
      val inOp2Select       = Input(UInt(1.W))
      val inImmType         = Input(UInt(3.W)) // dont need
      val inImmData         = Input(UInt())
      val inRd              = Input(UInt())
      val inALUop           = Input(UInt(4.W))

      //Output from register - decoder signals
      val outControlSignals = Output(new ControlSignals)
      val outBranchType     = Output(UInt(3.W))
      val outOp1Select      = Output(UInt(1.W))
      val outOp2Select      = Output(UInt(1.W))
      val outImmType        = Output(UInt(3.W))
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
  val controlSignalsReg     = Reg(new ControlSignals)
  val branchTypeReg         = RegInit(UInt(), 0.U)
  val op1SelectReg          = RegInit(UInt(), 0.U)
  val op2SelectReg          = RegInit(UInt(), 0.U)
  val immTypeReg            = RegInit(UInt(), 0.U)
  val immDataReg            = RegInit(UInt(), 0.U)
  val rdReg                 = RegInit(UInt(), 0.U)
val ALUopReg                = RegInit(UInt(), 0.U)
  //Register signal registers
  val readData1Reg          = RegInit(UInt(), 0.U)
  val readData2Reg          = RegInit(UInt(), 0.U)

  //Decoder signals registers
  controlSignalsReg    := io.inControlSignals
  io.outControlSignals := controlSignalsReg

  branchTypeReg        := io.inBranchType
  io.outBranchType     := branchTypeReg

  op1SelectReg         := io.inOp1Select
  io.outOp1Select      := op1SelectReg

  op2SelectReg         := io.inOp2Select
  io.outOp2Select      := op2SelectReg

  immTypeReg           := io.inImmType
  io.outImmType        := immTypeReg

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

  printf("Reg A:%d      ", io.inReadData1)
  printf("Reg B:%d\n",     io.inReadData2)
}
