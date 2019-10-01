package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

import ImmFormat._

class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      val instruction          = Input(new Instruction)
      val registerWriteAddress = Input(UInt())
      val registerWriteData    = Input(UInt())
      val registerWriteEnable  = Input(Bool())

      val controlSignals       = Output(new ControlSignals)
      val branchType           = Output(UInt(3.W))
      val op1Select            = Output(UInt(1.W))
      val op2Select            = Output(UInt(1.W))
      val immType              = Output(UInt(3.W))
      val immData              = Output(UInt())
      val ALUop                = Output(UInt(4.W))

      val readData1            = Output(UInt())
      val readData2            = Output(UInt())
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io

  val immData   = Wire(SInt())


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  //////////////////////////////////////////
  // Connect decoder and register signals //
  //////////////////////////////////////////

  //Connect instruction to decoder
  decoder.instruction := io.instruction
  
  //Connect decoded signals to outputs
  io.controlSignals := decoder.controlSignals
  io.branchType     := decoder.branchType
  io.op1Select      := decoder.op1Select
  io.op2Select      := decoder.op2Select
  io.immType        := decoder.immType
  io.ALUop          := decoder.ALUop

  //From instruction
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2

  //From EXbarrier
  registers.io.writeEnable  := io.registerWriteEnable
  registers.io.writeAddress := io.registerWriteAddress
  registers.io.writeData    := io.registerWriteData

  //To IDBarrier
  io.readData1              := registers.io.readData1
  io.readData2              := registers.io.readData2



  ////////////////////////////
  // Immediate value lookup //
  ////////////////////////////
  
  //Create alu operations map
  val ImmOpMap = Array(
    //Key,       Value
    ITYPE -> io.instruction.immediateIType,
    STYPE -> io.instruction.immediateSType,
    BTYPE -> io.instruction.immediateBType,
    UTYPE -> io.instruction.immediateUType,
    JTYPE -> io.instruction.immediateJType,
    SHAMT -> io.instruction.immediateZType,
    DC    -> io.instruction.immediateIType
  )

  //Set immData
  immData := MuxLookup(io.immType, 0.S(32.W), ImmOpMap)

  //Sign extend immdata
  io.immData := Cat(Fill(16, immData(15)), immData(15,0)).asUInt

}
