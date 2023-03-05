package gen;
import ast.*;
import gen.asm.AssemblyProgram;
/**
 * A visitor that produces code for a single function declaration
 */
public class FunCodeGen extends CodeGen{
  public FunCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
  }
  void visit(FunDecl fd){
	//each function produced in own section; necessary for register allocator
	asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
	//todo emit prologue
	//todo emit function body
	StmtCodeGen scd = new StmtCodeGen(asmProg);
	scd.visit(fd.block);
	//todo emit epilogue
  }
}
