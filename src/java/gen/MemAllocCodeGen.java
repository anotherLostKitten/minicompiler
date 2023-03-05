package gen;
import ast.ASTNode;
import gen.asm.AssemblyProgram;
//allocator for global & local variable decls
public class MemAllocCodeGen extends CodeGen{
  public MemAllocCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
  }
  boolean global=true;
  int fpOffset=0;
  void visit(ASTNode n){
	//todo
  }
}
