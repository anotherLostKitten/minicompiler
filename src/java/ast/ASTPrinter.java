package ast;
import java.io.PrintWriter;
public class ASTPrinter{
  private final PrintWriter writer;
  private int t;
  public ASTPrinter(PrintWriter writer){
	this.writer=writer;
	this.t=0;
  }
  private void t(){
	writer.print("\n");
	for(int i=0;i<t;i++)
	  writer.print("  ");
  }
  public void visit(ASTNode node){
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{
	  writer.print("Program(");
	  t++;
	  String d="";
	  for(StructTypeDecl std:p.structTypeDecls){
		writer.print(d);
		t();
		d=",";
		visit(std);
	  }
	  for(VarDecl vd:p.varDecls){
		writer.print(d);
		t();
		d=",";
		visit(vd);
	  }
	  for(FunDecl fd:p.funDecls){
		writer.print(d);
		t();
		d=",";
		visit(fd);
	  }
	  t--;
	  t();
	  writer.print(")");
	  writer.flush();
	}
	case BaseType t->
	  writer.print(t);
	case PointerType t->{
	  writer.print("PointerType(");
	  visit(t.type);
	  writer.print(")");
	}
	case StructType t->
	  writer.print("PointerType("+t.name+")");
	case ArrayType t->{
	  writer.print("ArrayType(");
	  visit(t.type);
	  writer.print(t.num);
	  writer.print(")");
	}
	case StructTypeDecl std->{
	  writer.print("StructTypeDecl(");
	  visit(std.type);
	  t++;
	  for(VarDecl vd:std.vs){
		writer.print(",");
		t();
		visit(vd);
	  }
	  t--;
	  t();
	  writer.print(")");
	}
	case VarDecl vd->{
	  writer.print("VarDecl(");
	  visit(vd.type);
	  writer.print(","+vd.name+")");
	}
	case FunDecl fd->{
	  writer.print("FunDecl(");
	  visit(fd.type);
	  writer.print(","+fd.name+",");
	  for(VarDecl vd:fd.params){
		visit(vd);
		writer.print(",");
	  }
	  visit(fd.block);
	  writer.print(")");
	}
	case IntLiteral i->
	  writer.print("IntLiteral("+i.v+")");
	case StrLiteral s->
	  writer.print("StrLiteral("+s.v+")");
	case ChrLiteral c->
	  writer.print("ChrLiteral("+c.v+")");
	case VarExpr v->
	  writer.print("VarExpr("+v.name+")");
	case FunCallExpr fc->{
	  writer.print("FunCallExpr("+fc.f);
	  for(Expr r:fc.args){
		writer.print(",");
		visit(r);
	  }
	  writer.print(")");
	}
	case BinOp bo->{
	  writer.print("BinOp(");
	  visit(bo.lhs);
	  writer.print(","+bo.op+",");
	  visit(bo.rhs);
	  writer.print(")");
	}
	case ArrayAccessExpr ra->{
	  writer.print("ArrayAccessExpr(");
	  visit(ra.arr);
	  writer.print(",");
	  visit(ra.ind);
	  writer.print(")");
	}
	case FieldAccessExpr fa->{
	  writer.print("FieldAccessExpr(");
	  visit(fa.struct);
	  writer.print(","+fa.field+")");
	}
	case ValueAtExpr va->{
	  writer.print("ValueAtExpr(");
	  visit(va.e);
	  writer.print(")");
	}
	case AddressOfExpr ao->{
	  writer.print("AddressOfExpr(");
	  visit(ao.e);
	  writer.print(")");
	}
	case SizeOfExpr so->{
	  writer.print("SizeOfExpr(");
	  visit(so.t);
	  writer.print(")");
	}
	case TypecastExpr tc->{
	  writer.print("TypecastExpr(");
	  visit(tc.t);
	  writer.print(",");
	  visit(tc.e);
	  writer.print(")");
	}
	case Assign as->{
	  writer.print("Assign(");
	  visit(as.lhs);
	  writer.print(",");
	  visit(as.rhs);
	  writer.print(")");
	}
	case ExprStmt es->{
	  writer.print("ExprStmt(");
	  visit(es.e);
	  writer.print(")");
	}
	case While w->{
	  writer.print("While(");
	  visit(w.c);
	  writer.print(",");
	  t++;
	  t();
	  visit(w.y);
	  t--;
	  t();
	  writer.print(")");
	}
	case If ie->{
	  writer.print("If(");
	  visit(ie.c);
	  writer.print(",");
	  t++;
	  t();
	  visit(ie.y);
	  if(ie.n!=null){
		writer.print(",");
		t();
		visit(ie.n);
	  }
	  t--;
	  t();
	  writer.print(")");
	}
	case Return r->{
	  writer.print("Return(");
	  if(r.e!=null)
		visit(r.e);
	  writer.print(")");
	}
	case Block b->{
	  writer.print("Block(");
	  String d="";
	  t++;
	  for(ASTNode n:b.children()){
		writer.print(d);
		t();
		d=",";
		visit(n);
	  }
	  t--;
	  t();
	  writer.print(")");
	}
	}
  }
}
