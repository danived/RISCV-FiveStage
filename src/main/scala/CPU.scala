package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFBarrier  = Module(new IFBarrier).io
  val IDBarrier  = Module(new IDBarrier).io
  val EXBarrier  = Module(new EXBarrier).io
  // val MEMBarrier = Module(new MEMBarrier).io

  val ID  = Module(new InstructionDecode)
  val IF  = Module(new InstructionFetch)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  // val WB  = Module(new Execute) (You may not need this one?)



  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC


  /**
    TODO: Your code here
    */
  //Signals to IFBarrier
  IFBarrier.inCurrentPC          := IF.io.PC
  IFBarrier.inInstruction        := IF.io.instruction

  //Decode stage
  ID.io.instruction              := IFBarrier.outInstruction
  ID.io.registerWriteAddress     := 0.U
  ID.io.registerWriteData        := 0.U
  ID.io.registerWriteEnable      := 0.U

  //Signals to IDBarrier
  IDBarrier.inControlSignals     := ID.io.controlSignals
  IDBarrier.inBranchType         := ID.io.branchType
  IDBarrier.inOp1Select          := ID.io.op1Select
  IDBarrier.inOp2Select          := ID.io.op2Select
  IDBarrier.inImmType            := ID.io.immType
  IDBarrier.inALUop              := ID.io.ALUop
  IDBarrier.inReadData1          := ID.io.readData1
  IDBarrier.inReadData2          := ID.io.readData2
  //immediate values would also go here

  //Execute stage
  EX.io.op2Select                := IDBarrier.outOp2Select
  EX.io.regA                     := IDBarrier.outReadData1
  EX.io.regB                     := IDBarrier.outReadData2
  EX.io.ALUop                    := IDBarrier.outALUop
  //Signals to EXBarrier
  EXBarrier.inALUResult          := EX.io.ALUResult

}
