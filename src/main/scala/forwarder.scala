package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class Forwarder extends MultiIOModule {

  val io = IO(
    new Bundle {
      val regAddr            = Input(UInt())
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val regData            = Input(UInt())
      val rdEXB              = Input(UInt())
      val ALUresultEXB       = Input(UInt())
      val rdMEMB             = Input(UInt())
      val ALUresultMEMB      = Input(UInt())

      val operandData        = Output(UInt())
      val freeze             = Output(Bool())
    }
  )

  val forward       = Wire(Bool())
  val forwardSelect = Wire(UInt())

  //check if data should be forwarded
  when((io.regAddr === io.rdEXB) & io.controlSignalsEXB.regWrite){
    forward := true.B
    forwardSelect := ForwardSelect.EX

  }.elsewhen((io.regAddr === io.rdMEMB) & io.controlSignalsMEMB.regWrite){
    forward := true.B
    forwardSelect := ForwardSelect.MEM
  }
  .otherwise{
    forward       := false.B
    forwardSelect := ForwardSelect.DC
  }


  //assign correct data to operandData
  when(forward){
    //if forwarding, send correct forward data to operand
    when(forwardSelect === ForwardSelect.EX){
      io.operandData := io.ALUresultEXB
    }.otherwise{
      io.operandData := io.ALUresultMEMB
    }

  }.otherwise{
    //if not forwarding, send regdata to operand
    io.operandData := io.regData
  }


  //check if load instruction and we should forward
  //Then we need to stall the pipeline
  when(io.controlSignalsMEMB.memToReg & (forward & (forwardSelect === ForwardSelect.MEM))){
    io.freeze := true.B
  }.otherwise{
    io.freeze := false.B
  }
}
