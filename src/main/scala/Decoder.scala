package FiveStage
import chisel3._
import chisel3.util.BitPat
import chisel3.util.ListLookup


/**
  * This module is mostly done, but you will have to fill in the blanks in opcodeMap.
  * You may want to add more signals to be decoded in this module depending on your
  * design if you so desire.
  * 
  * In the "classic" 5 stage decoder signals such as op1select and immType
  * are not included, however I have added them to my design, and similarily you might
  * find it useful to add more
 */
class Decoder() extends Module {

  val io = IO(new Bundle {
                val instruction    = Input(new Instruction)

                val controlSignals = Output(new ControlSignals)
                val branchType     = Output(UInt(3.W))
                val op1Select      = Output(UInt(1.W))
                val op2Select      = Output(UInt(1.W))
                val immType        = Output(UInt(3.W))
                val ALUop          = Output(UInt(4.W))
              })

  import lookup._
  import Op1Select._
  import Op2Select._
  import branchType._
  import ImmFormat._

  val N = 0.asUInt(1.W)
  val Y = 1.asUInt(1.W)

  /**
    * In scala we sometimes (ab)use the `->` operator to create tuples. 
    * The reason for this is that it serves as convenient sugar to make maps.
    * 
    * This doesn't matter to you, just fill in the blanks in the style currently
    * used, I just want to demystify some of the scala magic.
    * 
    * `a -> b` == `(a, b)` == `Tuple2(a, b)`
    */
  val opcodeMap: Array[(BitPat, List[UInt])] = Array(

    // signal      memToReg, regWrite, memRead, memWrite, branch,  jump, branchType,    Op1Select, Op2Select, ImmSelect,    ALUOp
    //memory inistructions
    LW     -> List(Y,        Y,        Y,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.ADD),
    SW     -> List(N,        N,        N,       Y,        N,       N,    branchType.DC, rs1,       imm,       STYPE,        ALUOps.ADD),

    //load instrucitons
//    LI     -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.ADD),

    //arithmetic instructions
    ADD    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.ADD),
    ADDI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.ADD),
    SUB    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SUB),
    AND    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.AND),
    ANDI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.AND),
    OR     -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.OR ),
    ORI    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.OR),
    XOR    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.XOR),
    XORI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.XOR),

    //Shift instructions
    SRA    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SRA),
    SRAI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.SRA),
    SRL    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SRL),
    SRLI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.SRL),
    SLL    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SLL),
    SLLI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.SLL),

    //Set less than instructions
    SLT    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SLT),
    SLTI   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.SLT),
    SLTU   -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SLTU),
    SLTIU  -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       ITYPE,        ALUOps.SLTU),

    //jump instructions
    JAL    -> List(N,        Y,        N,       N,        N,       Y,    branchType.jump, PC,      imm,       JTYPE,        ALUOps.ADD),
    JALR   -> List(N,        Y,        N,       N,        N,       Y,    branchType.jump, rs1,     imm,       ITYPE,        ALUOps.ADD),

    //Branch instruction
    BEQ    -> List(N,        N,        N,       N,        Y,       N,    branchType.beq, PC,      imm,       BTYPE,        ALUOps.ADD),
    BNE    -> List(N,        N,        N,       N,        Y,       N,    branchType.neq, PC,      imm,       BTYPE,        ALUOps.ADD),
    BLT    -> List(N,        N,        N,       N,        Y,       N,    branchType.lt,  PC,      imm,       BTYPE,        ALUOps.ADD),
    BGE    -> List(N,        N,        N,       N,        Y,       N,    branchType.gte, PC,      imm,       BTYPE,        ALUOps.ADD),
    BLTU   -> List(N,        N,        N,       N,        Y,       N,    branchType.ltu, PC,      imm,       BTYPE,        ALUOps.ADD),
    BGEU   -> List(N,        N,        N,       N,        Y,       N,    branchType.gteu,PC,      imm,       BTYPE,        ALUOps.ADD),

    //unsure on these instuction
    LUI    -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       UTYPE,        ALUOps.SLTU),
    AUIPC  -> List(N,        Y,        N,       N,        N,       N,    branchType.DC, rs1,       imm,       UTYPE,        ALUOps.SLTU),


    /**
      TODO: Fill in the blanks
      */
  )


  val NOP = List(N, N, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.DC)

  val decodedControlSignals = ListLookup(
    io.instruction.asUInt(),
    NOP,
    opcodeMap)

  io.controlSignals.memToReg   := decodedControlSignals(0)
  io.controlSignals.regWrite   := decodedControlSignals(1)
  io.controlSignals.memRead    := decodedControlSignals(2)
  io.controlSignals.memWrite   := decodedControlSignals(3)
  io.controlSignals.branch     := decodedControlSignals(4)
  io.controlSignals.jump       := decodedControlSignals(5)

  io.branchType := decodedControlSignals(6)
  io.op1Select  := decodedControlSignals(7)
  io.op2Select  := decodedControlSignals(8)
  io.immType    := decodedControlSignals(9)
  io.ALUop      := decodedControlSignals(10)
}
