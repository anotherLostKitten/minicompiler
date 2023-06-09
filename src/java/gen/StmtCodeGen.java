package gen;
import ast.*;
import gen.asm.*;
public class StmtCodeGen extends CodeGen{
  private final FunDecl encapsf;
  public StmtCodeGen(AssemblyProgram asmProg,FunDecl encapsf){
	this.asmProg=asmProg;
	this.encapsf=encapsf;
  }
  void visit(Stmt z){
	AssemblyProgram.Section ts=asmProg.getCurrentSection();
	switch(z){
	case Block b->{
	  //need not handle varDecl (memory allocator takes care of them)
	  for(Stmt is:b.stmts)
		visit(is);
	}
	case While w->{
	  ts.emit("while");
	  Label c=Label.create("while_cond"),e=Label.create("while_end");
	  ts.emit(c);
	  Register v=(new ExprCodeGen(asmProg,encapsf)).visit(w.c);
	  ts.emit(OpCode.BEQZ,v,e);
	  ts.emit("then");
	  visit(w.y);
	  ts.emit(OpCode.B,c);
	  ts.emit("elihw");
	  ts.emit(e);
	}
	case If i->{
	  ts.emit("if");
	  Register v=(new ExprCodeGen(asmProg,encapsf)).visit(i.c);
	  if(i.n==null){
		Label n=Label.create("if_no");
		ts.emit(OpCode.BEQZ,v,n);
		ts.emit("then");
		visit(i.y);
		ts.emit("fi");
		ts.emit(n);
	  }else{
		Label n=Label.create("if_no"),e=Label.create("if_end");
		ts.emit(OpCode.BEQZ,v,n);
		ts.emit("then");
		visit(i.y);
		ts.emit(OpCode.B,e);
		ts.emit("else");
		ts.emit(n);
		visit(i.n);
		ts.emit("fi");
		ts.emit(e);
	  }
	}
	case Return r->{
	  ts.emit("return");
	  if(r.e!=null){
		Register v=new ExprCodeGen(asmProg,encapsf).visit(r.e);
		if(r.e.type instanceof StructType s){
		  Register cp=Register.Virtual.create();
		  for(int i=0;i<s.size();i+=4){
			ts.emit(OpCode.LW,cp,v,i);
			ts.emit(OpCode.SW,cp,Register.Arch.fp,i+4);
		  }
		}else if(r.e.type instanceof PointerType||r.e.type instanceof ClassType||r.e.type==BaseType.INT)
		  ts.emit(OpCode.SW,v,Register.Arch.fp,4);
		else if(r.e.type==BaseType.CHAR||r.e.type==BaseType.VOID)
		  ts.emit(OpCode.SB,v,Register.Arch.fp,4);
		else
		  throw new IllegalStateException("Unreachable return type");
	  }
	  ts.emit(OpCode.B,r.d.out);
	}
	case ExprStmt e->{
	  ts.emit("exprstmt");
	  new ExprCodeGen(asmProg,encapsf).visit(e.e);
	}
	}
  }
}
