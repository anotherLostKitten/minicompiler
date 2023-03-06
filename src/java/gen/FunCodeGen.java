package gen;
import ast.*;
import gen.asm.*;
/**
 * A visitor that produces code for a single function declaration
 */
public class FunCodeGen extends CodeGen{
  public FunCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
  }
  void visit(FunDecl fd){
	//each function produced in own section; necessary for register allocator
	AssemblyProgram.Section fs=asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
	fs.emit("function: "+fd.name);
	fs.emit(fd.in);
	//todo? prologue
	fs.emit(OpCode.SW,Register.Arch.fp,Register.Arch.sp,-4);
	fs.emit(OpCode.ADD,Register.Arch.fp,Register.Arch.sp,Register.Arch.zero);
	fs.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,fd.size);
	fs.emit(OpCode.PUSH_REGISTERS);
	//todo? other stuff for body
	StmtCodeGen scd=new StmtCodeGen(asmProg);
	scd.visit(fd.block);
	//todo? epilogue
	fs.emit(fd.out);
	fs.emit(OpCode.POP_REGISTERS);
	fs.emit(OpCode.LW,Register.Arch.fp,Register.Arch.fp,-4);
	fs.emit(OpCode.ADD,Register.Arch.sp,Register.Arch.fp,Register.Arch.zero);
	fs.emit(OpCode.JR,Register.Arch.ra);//todo? so this should always return now
  }
}
