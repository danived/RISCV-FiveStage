package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental._


class EXBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      val inBranchAddr      = Input(UInt(32.W))
      val inBranch          = Input(UInt(1.W))
      val inControlSignals  = Input(new ControlSignals)
      val inRd              = Input(UInt(5.W))
      val inRs2             = Input(UInt(5.W))
      val inALUResult       = Input(UInt(32.W))

      val freeze            = Input(Bool())

      val outBranchAddr     = Output(UInt())
      val outBranch         = Output(UInt())
      val outALUResult      = Output(UInt())
      val outControlSignals = Output(new ControlSignals)
      val outRd             = Output(UInt())
      val outRs2            = Output(UInt())
    }
  )

  val branchAddr        = RegEnable(io.inBranchAddr, 0.U, !io.freeze)
  val branch            = RegEnable(io.inBranch, 0.U, !io.freeze)
  val ALUResultReg      = RegEnable(io.inALUResult, 0.U, !io.freeze)
  val controlSignalsReg = RegEnable(io.inControlSignals, !io.freeze)
  val rdReg             = RegEnable(io.inRd, 0.U, !io.freeze)
  val rs2Reg            = RegEnable(io.inRs2, 0.U, !io.freeze)

  io.outBranchAddr     := branchAddr

  io.outBranch         := branch

  //control singals register
  io.outControlSignals := controlSignalsReg

  //immediate data register
  io.outRd             := rdReg

  //reg B register
  io.outRs2           := rs2Reg

  //ALU result register
  io.outALUResult      := ALUResultReg
}

