package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class RegisterBypass extends MultiIOModule {


  val io = IO(
    new Bundle {
      val readAddr     = Input(UInt())

      val writeAddr    = Input(UInt())
      val writeEnable  = Input(Bool())

      val registerData = Input(UInt())
      val writeData    = Input(UInt())

      val outData      = Output(UInt())

    }
  )


  when((io.readAddr === io.writeAddr) & io.writeEnable){
    io.outData := io.writeData
  }.otherwise{
    io.outData := io.registerData
  }

}
