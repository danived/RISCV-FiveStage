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

  val forward             = Wire(Bool())
  val forwardSelect       = Wire(UInt())
  val freezeReg           = RegInit(Bool(), false.B)
  val ALUresultBonus      = RegInit(UInt(), 0.U)
  val rdBonus             = RegInit(UInt(), 0.U)
  val controlSignalsBONUS = Reg(new ControlSignals)



  //Bonus write back register
  ALUresultBonus := io.ALUresultMEMB
  rdBonus := io.rdMEMB
  controlSignalsBONUS := io.controlSignalsMEMB


  when((io.regAddr =/= 0.U) & (io.regAddr === io.rdEXB) & io.controlSignalsEXB.regWrite){
    //Freeze and forward
    when(io.controlSignalsEXB.memToReg){
      io.freeze     := true.B

      forward       := true.B
      forwardSelect := ForwardSelect.EX

      //normal forward
    }.otherwise{
      io.freeze     := false.B
      forward       := true.B
      forwardSelect := ForwardSelect.EX

    }
  }.elsewhen((io.regAddr =/= 0.U) & (io.regAddr === io.rdMEMB) & io.controlSignalsMEMB.regWrite){
    io.freeze       := false.B

    forward         := true.B
    forwardSelect   := ForwardSelect.MEM

  }.elsewhen((io.regAddr =/= 0.U) & (io.regAddr === rdBonus) & controlSignalsBONUS.regWrite){
    io.freeze       := false.B

    forward         := true.B
    forwardSelect   := ForwardSelect.BONUS

  }
    .otherwise{
    io.freeze     := false.B
    forward       := false.B
    forwardSelect := ForwardSelect.DC
  }


  //Bonus write back
  //destinatio og verdi og regwrite


  //assign correct data to operandData
  when(forward){
    //if forwarding, send correct forward data to operand
    when(forwardSelect === ForwardSelect.EX){
      io.operandData := io.ALUresultEXB
    }.elsewhen(forwardSelect === ForwardSelect.BONUS){
      io.operandData := ALUresultBonus
    }.otherwise{
      io.operandData := io.ALUresultMEMB
    }

  }.otherwise{
    //if not forwarding, send regdata to operand
    io.operandData := io.regData
  }
}
