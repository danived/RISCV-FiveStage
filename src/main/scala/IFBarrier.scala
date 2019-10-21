package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class IFBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
      val inCurrentPC     = Input(UInt(32.W))
      val inInstruction   = Input(new Instruction)

      val outCurrentPC    = Output(UInt(32.W))
      val outInstruction  = Output(new Instruction)
    }
  )

  val currentPCReg   = RegInit(UInt(), 0.U)
  //val InstructionReg = Reg(new Instruction)

  //current PC
  currentPCReg := io.inCurrentPC
  io.outCurrentPC := currentPCReg

  //Instruction
//  InstructionReg := io.inInstruction
  io.outInstruction := io.inInstruction
}
