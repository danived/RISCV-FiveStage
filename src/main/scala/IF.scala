package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )


  /**
    * TODO: Add input signals for handling events such as jumps

    * TODO: Add output signal for the instruction. 
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val branchAddr     = Input(UInt())
      val controlSignals = Input(new ControlSignals)
      val branch         = Input(UInt())

      val PC             = Output(UInt())
      val instruction    = Output(new Instruction)
    }
  )

  val IMEM        = Module(new IMEM)
  val nextPC      = WireInit(UInt(), 0.U)
  val PC          = RegInit(UInt(32.W), 0.U)

  val instruction = Wire(new Instruction)
  val branch      = WireInit(Bool(), false.B)

  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress


  /**
    * TODO: Your code here.
    * 
    * You should expand on or rewrite the code below.
    */

  IMEM.io.instructionAddress := PC


  //mux for incremented PC or branch addr
  when(io.controlSignals.jump | (io.controlSignals.branch & io.branch === 1.U)){
    //Branch Addr
    PC := io.branchAddr

    //Sent outputs
    io.PC := io.branchAddr
  }.otherwise{
    //Incremented PC
    PC := nextPC
    
    //Sent outputs
    io.PC := PC
  }

  instruction := IMEM.io.instruction.asTypeOf(new Instruction)


  //Incremented PC
  nextPC := PC + 4.U


  io.instruction := instruction

  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    PC := 0.U
    instruction := Instruction.NOP
  }
}
