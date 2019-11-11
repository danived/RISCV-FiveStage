package FiveStage

import chisel3._
import chisel3.util._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class IFBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      val inCurrentPC     = Input(UInt(32.W))
      val inInstruction   = Input(new Instruction)
      val freeze          = Input(Bool())

      val outCurrentPC    = Output(UInt(32.W))
      val outInstruction  = Output(new Instruction)
    }
  )

  val currentPCReg   = RegEnable(io.inCurrentPC, 0.U, !io.freeze)
  val prevPC         = WireInit(UInt(), 0.U)
  //val InstructionReg = Reg(new Instruction)

  //PC
  io.outCurrentPC := currentPCReg

  //Instruction
  io.outInstruction := io.inInstruction

}
