package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class Forwarder extends MultiIOModule {

  val io = IO(
    new Bundle {
      val instruction        = Input(new Instruction)
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val rdEXB              = Input(UInt())
      val ALUresultEXB       = Input(UInt())
      val rdMEMB             = Input(UInt())
      val ALUresultMEMB      = Input(UInt())

      val forward            = Output(Bool())
      val forwardSelect      = Output(UInt())

    }
  )

  //check if rs1 or rs2 is rd of EXB or MEMB
  when((io.instruction.registerRs1 === io.rdEXB) & io.controlSignalsEXB.regWrite){
    io.forward := true.B

  }.elsewhen((io.instruction.registerRs2 === io.rdEXB) & io.controlSignalsEXB.regWrite){
    io.forward := true.B

  }.elsewhen((io.instruction.registerRs1 === io.rdMEMB) & io.controlSignalsMEMB.regWrite){
    io.forward := true.B

  }.elsewhen((io.instruction.registerRs2 === io.rdMEMB) & io.controlSignalsMEMB.regWrite){
    io.forward := true.B

  }
  .otherwise{
    io.forward := false.B
  }


}
