package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class IFBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      val inCurrentPC     = Input(UInt())
      val inInstruction   = Input(new Instruction)

      val outCurrentPC    = Output(UInt())
      val outInstruction  = Output(new Instruction)
    }
  )


  val currentPCReg   = RegInit(UInt(), 0.U)
  val InstructionReg = RegInit(UInt(), 0.U)


  currentPCReg := io.inCurrentPC
  io.outCurrentPC := currentPCReg

  io.outInstruction := io.inInstruction
}
