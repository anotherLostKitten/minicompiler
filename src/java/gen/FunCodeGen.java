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
	fs.emit("function "+fd.name);
	//todo emit prologue
	//todo emit function body
	StmtCodeGen scd=new StmtCodeGen(asmProg);
	scd.visit(fd.block);
	//todo emit epilogue
  }
}
