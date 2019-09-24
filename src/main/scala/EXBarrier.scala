package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class EXBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      val inBranchAddr      = Input(UInt())
      val inBranch          = Input(UInt())
      val inControlSignals  = Input(new ControlSignals)
      val inRd              = Input(UInt())
      val inRegB            = Input(UInt())
      val inALUResult       = Input(UInt())

      val outBranchAddr     = Output(UInt())
      val outBranch         = Output(UInt())
      val outALUResult      = Output(UInt())
      val outControlSignals = Output(new ControlSignals)
      val outRd             = Output(UInt())
      val outRegB           = Output(UInt())
    }
  )

  val branchAddr        = RegInit(UInt(), 0.U)
  val branch            = RegInit(UInt(), 0.U)
  val ALUResultReg      = RegInit(UInt(), 0.U)
  val controlSignalsReg = Reg(new ControlSignals)
  val rdReg             = RegInit(UInt(), 0.U)
  val regBReg           = RegInit(UInt(), 0.U)

  branchAddr           := io.inBranchAddr
  io.outBranchAddr     := branchAddr

  branch               := io.inBranch
  io.outBranch         := branch

  //control singals register
  controlSignalsReg    := io.inControlSignals
  io.outControlSignals := controlSignalsReg

  //immediate data register
  rdReg                := io.inRd
  io.outRd             := rdReg

  //reg B register
  regBReg              := io.inRegB
  io.outRegB           := regBReg

  //ALU result register
  ALUResultReg         := io.inALUResult
  io.outALUResult      := ALUResultReg
}
