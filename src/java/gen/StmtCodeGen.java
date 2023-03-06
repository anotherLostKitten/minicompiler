package gen;
import ast.*;
import gen.asm.AssemblyProgram;
public class StmtCodeGen extends CodeGen{
  public StmtCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
  }
  void visit(Stmt s){
	switch(s){
	case Block b->{
	  //need not handle varDecl (memory allocator takes care of them)
	  for(Stmt is:b.stmts)
		visit(is);
	}
	//todo, obviously
	case While w->{}
	case If i->{}
	case Return r->{}
	case ExprStmt e->
	  (new ExprCodeGen(asmProg)).visit(e.e);
	}
  }
}
