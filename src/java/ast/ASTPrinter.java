package ast;
import java.io.PrintWriter;
public class ASTPrinter{
  private final PrintWriter w;
  private int t;
  public ASTPrinter(PrintWriter w){
	this.w=w;
	this.t=0;
  }
  private void t(){
	w.print("\n");
	for(int i=0;i<t;i++)
	  w.print("  ");
  }
  public void visit(ASTNode node){
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{
	  w.print("Program(");
	  t++;
	  String d="";
	  for(Decl std:p.decls){
		w.print(d);
		t();
		d=",";
		visit(std);
	  }
	  t--;
	  t();
	  w.print(")");
	  w.flush();
	}
	case BaseType t->
	  w.print(t);
	case PointerType t->{
	  w.print("PointerType(");
	  visit(t.type);
	  w.print(")");
	}
	case StructType t->
	  w.print("StructType("+t.name+")");
	case ClassType t->
	  w.print("ClassType("+t.name+")");
	case ArrayType t->{
	  w.print("ArrayType(");
	  visit(t.type);
	  w.print(","+t.num+")");
	}
	case StructTypeDecl std->{
	  w.print("StructTypeDecl(");
	  visit(std.type);
	  t++;
	  for(VarDecl vd:std.vs){
		w.print(",");
		t();
		visit(vd);
	  }
	  t--;
	  t();
	  w.print(")");
	}
	case ClassDecl cd->{
	  w.print("ClassDecl(");
	  visit(cd.type);
	  if(cd.parent!=null){
		w.print(",");
		visit(cd.parent);
	  }
	  t++;
	  for(VarDecl vd:cd.vs){
		w.print(",");
		t();
		visit(vd);
	  }
	  for(FunDecl fd:cd.fs){
		w.print(",");
		t();
		visit(fd);
	  }
	  t--;
	  t();
	  w.print(")");
	}
	case VarDecl vd->{
	  w.print("VarDecl(");
	  visit(vd.type);
	  w.print(","+vd.name+")");
	}
	case FunDecl fd->{
	  w.print("FunDecl(");
	  visit(fd.type);
	  w.print(","+fd.name+",");
	  for(VarDecl vd:fd.params){
		visit(vd);
		w.print(",");
	  }
	  visit(fd.block);
	  w.print(")");
	}
	case IntLiteral i->
	  w.print("IntLiteral("+i.v+")");
	case StrLiteral s->
	  w.print("StrLiteral("+s.v+")");
	case ChrLiteral c->
	  w.print("ChrLiteral("+c.v+")");
	case VarExpr v->
	  w.print("VarExpr("+v.name+")");
	case FunCallExpr fc->{
	  w.print("FunCallExpr("+fc.f);
	  for(Expr r:fc.args){
		w.print(",");
		visit(r);
	  }
	  w.print(")");
	}
	case ClassFunCallExpr cfc->{
	  w.print("ClassFunCallExpr(");
	  visit(cfc.object);
	  w.print(",");
	  visit(cfc.call);
	  w.print(")");
	}
	case BinOp bo->{
	  w.print("BinOp(");
	  visit(bo.lhs);
	  w.print(","+bo.op+",");
	  visit(bo.rhs);
	  w.print(")");
	}
	case ArrayAccessExpr ra->{
	  w.print("ArrayAccessExpr(");
	  visit(ra.arr);
	  w.print(",");
	  visit(ra.ind);
	  w.print(")");
	}
	case FieldAccessExpr fa->{
	  w.print("FieldAccessExpr(");
	  visit(fa.struct);
	  w.print(","+fa.field+")");
	}
	case ValueAtExpr va->{
	  w.print("ValueAtExpr(");
	  visit(va.e);
	  w.print(")");
	}
	case AddressOfExpr ao->{
	  w.print("AddressOfExpr(");
	  visit(ao.e);
	  w.print(")");
	}
	case SizeOfExpr so->{
	  w.print("SizeOfExpr(");
	  visit(so.t);
	  w.print(")");
	}
	case TypecastExpr tc->{
	  w.print("TypecastExpr(");
	  visit(tc.t);
	  w.print(",");
	  visit(tc.e);
	  w.print(")");
	}
	case ClassInstantiationExpr cie->{
	  w.print("ClassInstantiationExpr(");
	  visit(cie.t);
	  w.print(")");
	}
	case Assign as->{
	  w.print("Assign(");
	  visit(as.lhs);
	  w.print(",");
	  visit(as.rhs);
	  w.print(")");
	}
	case ExprStmt es->{
	  w.print("ExprStmt(");
	  visit(es.e);
	  w.print(")");
	}
	case While wh->{
	  w.print("While(");
	  visit(wh.c);
	  w.print(",");
	  t++;
	  t();
	  visit(wh.y);
	  t--;
	  t();
	  w.print(")");
	}
	case If ie->{
	  w.print("If(");
	  visit(ie.c);
	  w.print(",");
	  t++;
	  t();
	  visit(ie.y);
	  if(ie.n!=null){
		w.print(",");
		t();
		visit(ie.n);
	  }
	  t--;
	  t();
	  w.print(")");
	}
	case Return r->{
	  w.print("Return(");
	  if(r.e!=null)
		visit(r.e);
	  w.print(")");
	}
	case Block b->{
	  w.print("Block(");
	  String d="";
	  t++;
	  for(ASTNode n:b.children()){
		w.print(d);
		t();
		d=",";
		visit(n);
	  }
	  t--;
	  t();
	  w.print(")");
	}
	}
  }
}
