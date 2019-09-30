package FiveStage // 

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
  val MEMBarrier = Module(new MEMBarrier).io

  val ID  = Module(new InstructionDecode)
  val IF  = Module(new InstructionFetch)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  // val WB  = Module(new Execute) (You may not need this one?)



  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup              := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup          := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup             := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates                := ID.testHarness.testUpdates
  testHarness.memUpdates                := MEM.testHarness.testUpdates
  testHarness.currentPC                 := IF.testHarness.PC


  /**
    TODO: Your code here
    */
  //Branch addr to IF
  IF.io.branchAddr            := EXBarrier.outBranchAddr
  IF.io.controlSignals        := EXBarrier.outControlSignals
  IF.io.branch                := EXBarrier.outBranch
  //Signals to IFBarrier

  IFBarrier.inCurrentPC       := IF.io.PC
  IFBarrier.inInstruction     := IF.io.instruction

  //Decode stage
  ID.io.instruction           := IFBarrier.outInstruction
  ID.io.registerWriteAddress  := MEMBarrier.outRd
  ID.io.registerWriteEnable   := MEMBarrier.outControlSignals.regWrite

  //Signals to IDBarrier
  IDBarrier.inControlSignals  := ID.io.controlSignals
  IDBarrier.inBranchType      := ID.io.branchType
  IDBarrier.inPC              := IFBarrier.outCurrentPC
  IDBarrier.inOp1Select       := ID.io.op1Select
  IDBarrier.inOp2Select       := ID.io.op2Select
  IDBarrier.inImmType         := ID.io.immType
  IDBarrier.inImmData         := ID.io.immData
  IDBarrier.inRd              := IFBarrier.outInstruction.registerRd
  IDBarrier.inALUop           := ID.io.ALUop
  IDBarrier.inReadData1       := ID.io.readData1
  IDBarrier.inReadData2       := ID.io.readData2

  //Execute stage
  //branch
  EX.io.PC                    := IDBarrier.outPC
  EX.io.branchType            := IDBarrier.outBranchType
  EXBarrier.inBranchAddr      := EX.io.branchAddr
  //alu
  EX.io.op1Select             := IDBarrier.outOp1Select
  EX.io.op2Select             := IDBarrier.outOp2Select
  EX.io.regA                  := IDBarrier.outReadData1
  EX.io.regB                  := IDBarrier.outReadData2
  EX.io.immData               := IDBarrier.outImmData
  EX.io.ALUop                 := IDBarrier.outALUop

  //Signals to EXBarrier
  EXBarrier.inALUResult       := EX.io.ALUResult
  EXBarrier.inControlSignals  := IDBarrier.outControlSignals
  EXBarrier.inBranch          := EX.io.branch
  EXBarrier.inRd              := IDBarrier.outRd
  EXBarrier.inRegB            := IDBarrier.outReadData2

  //MEM stage
  MEM.io.dataIn               := EXBarrier.outRegB
  MEM.io.dataAddress          := EXBarrier.outALUResult
  MEM.io.writeEnable          := EXBarrier.outControlSignals.memWrite

  //MEMBarrier
  MEMBarrier.inControlSignals := EXBarrier.outControlSignals
  MEMBarrier.inALUResult      := EXBarrier.outALUResult
  MEMBarrier.inRd             := EXBarrier.outRd
  MEMBarrier.inRegB           := EXBarrier.outRegB
  MEMBarrier.inMEMData        := MEM.io.dataOut


  //WB mux
  when(MEMBarrier.outControlSignals.memToReg){
    ID.io.registerWriteData := MEMBarrier.outMEMData
  }.otherwise{
    ID.io.registerWriteData := MEMBarrier.outALUResult
  }
}
