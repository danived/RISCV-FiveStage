package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

import Op2Select._

class Execute extends MultiIOModule {

  val io = IO(
    new Bundle {
      val op2Select    = Input(UInt())
      val regA         = Input(UInt())
      val regB         = Input(UInt())
      val immData      = Input(UInt())
      val ALUop        = Input(UInt())

      val ALUResult    = Output(UInt())
    }
  )

  val ALU = Module(new ALU).io

  val operand2 = Wire(UInt())


  //Operand 2 Mux
  when(io.op2Select === Op2Select.rs2){
    operand2 := io.regB
  }.otherwise{
    operand2 := io.immData
  }

  //ALU
  ALU.op1      :=io.regA
  ALU.op2      :=operand2
  ALU.ALUop    :=io.ALUop
  io.ALUResult :=ALU.result
//  0.U          :=ALU.zero
  printf("ALU result:%d", ALU.result)
}
