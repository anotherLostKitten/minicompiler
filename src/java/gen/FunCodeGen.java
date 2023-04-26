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
	AssemblyProgram.Section fs=asmProg.newSection(AssemblyProgram.Section.Type.TEXT);//each function produced in own section; necessary for register allocator
	fs.emit("function: "+fd.name);
	fs.emit(fd.in);
	fs.emit(OpCode.SW,Register.Arch.fp,Register.Arch.sp,-4);
	fs.emit(OpCode.ADD,Register.Arch.fp,Register.Arch.sp,Register.Arch.zero);
	fs.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,fd.size);
	fs.emit(OpCode.PUSH_REGISTERS);
	StmtCodeGen scd=new StmtCodeGen(asmProg,fd);
	scd.visit(fd.block);
	fs.emit("forced return "+fd.name);
	fs.emit(fd.out);
	fs.emit(OpCode.POP_REGISTERS);
	fs.emit(OpCode.ADD,Register.Arch.sp,Register.Arch.fp,Register.Arch.zero);
	fs.emit(OpCode.LW,Register.Arch.fp,Register.Arch.fp,-4);
	fs.emit(OpCode.JR,Register.Arch.ra);//so this should always return now even if no return statement
  }
}
