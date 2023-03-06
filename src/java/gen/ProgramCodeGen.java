package gen;
import ast.*;
import gen.asm.AssemblyProgram;
public class ProgramCodeGen extends CodeGen{
  private final AssemblyProgram.Section dataSection;
  public ProgramCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
	this.dataSection=asmProg.newSection(AssemblyProgram.Section.Type.DATA);
  }
  void generate(Program p){
	//allocate all variables
	MemAllocCodeGen allocator=new MemAllocCodeGen(asmProg);
	allocator.visit(p);
	//generate the code for each function
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
	//todo idk make sure we start in main function somewhere
  }
}
