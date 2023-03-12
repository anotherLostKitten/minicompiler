package gen;
import ast.*;
import gen.asm.*;
public class ProgramCodeGen extends CodeGen{
  private final AssemblyProgram.Section dataSection;
  public ProgramCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
	this.dataSection=asmProg.newSection(AssemblyProgram.Section.Type.DATA);
  }
  void generate(Program p){
	MemAllocCodeGen allocator=new MemAllocCodeGen(asmProg);
	allocator.visit(p);
	if(p.main!=null){//to start in main function
	  AssemblyProgram.Section fs=asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
	  fs.emit("initter");
	  fs.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-p.main.co);
	  fs.emit(OpCode.SW,Register.Arch.zero,Register.Arch.sp,4);
	  fs.emit(OpCode.JAL,p.main.in);
	  if(p.main.type==BaseType.CHAR||p.main.type==BaseType.VOID)
		fs.emit(OpCode.LB,Register.Arch.a0,Register.Arch.sp,4);
	  else
		fs.emit(OpCode.LW,Register.Arch.a0,Register.Arch.sp,4);
	  fs.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,p.main.co);
	  fs.emit(OpCode.LI,Register.Arch.v0,17);
	  fs.emit(OpCode.SYSCALL);
	}
	for(Decl d:p.decls){
	  switch(d){
	  case FunDecl fd->{
		if(!fd.sc){
		  FunCodeGen fcg=new FunCodeGen(asmProg);
		  fcg.visit(fd);
		}
	  }
	  default->{}
	  };
	}
  }
}
