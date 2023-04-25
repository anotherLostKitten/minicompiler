package ast;
import java.io.PrintWriter;
public class DotPrinter{
  private final PrintWriter w;
  private int nc=0;
  public DotPrinter(PrintWriter w){
	this.w=w;
	this.nc=0;
  }
  private void printv(int n,String l){
	w.println("N_"+n+" [label=\""+l+"\"];");
  }
  private void printe(int n,int c){
	w.println("N_"+n+" ->  N_"+c+";");
  }
  public int visit(ASTNode node){
	int n=nc++;
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{
	  w.println("digraph ast {");
	  for(Decl fd:p.decls)
		visit(fd);
	  w.println("}");
	  w.flush();
	}
	case BaseType t->
	  printv(n,t.toString());
	case PointerType t->{
	  printv(n,"PointerType");
	  printe(n,visit(t.type));
	}
	case StructType t->
	  printv(n,"StructType("+t.name+")");
	case ClassType t->
	  printv(n,"ClassType("+t.name+")");
	case ArrayType t->{
	  printv(n,"ArrayType["+t.num+"]");
	  printe(n,visit(t.type));
	}
	case StructTypeDecl std->{
	  printv(n,"StructTypeDecl");
	  printe(n,visit(std.type));
	  for(VarDecl vd:std.vs)
		printe(n,visit(vd));
	}
	case ClassDecl cd->{
	  printv(n,"ClassDecl");
	  printe(n,visit(cd.type));
	  if(cd.parent!=null)
		printe(n,visit(cd.parent));
	  for(VarDecl vd:cd.vs)
		printe(n,visit(vd));
	  for(FunDecl fd:cd.fs)
		printe(n,visit(fd));
	}
	case VarDecl vd->{
	  printv(n,"VarDecl("+vd.name+")");
	  printe(n,visit(vd.type));
	}
	case FunDecl fd->{
	  printv(n,"FunDecl("+fd.name+")");
	  printe(n,visit(fd.type));
	  for(VarDecl vd:fd.params)
		printe(n,visit(vd));
	  printe(n,visit(fd.block));
	}
	case IntLiteral i->
	  printv(n,"IntLiteral("+i.v+")");
	case StrLiteral s->
	  printv(n,"StrLiteral("+s.v+")");
	case ChrLiteral c->
	  printv(n,"ChrLiteral("+c.v+")");
	case VarExpr v->
	  printv(n,"VarExpr("+v.name+")");
	case FunCallExpr fc->{
	  printv(n,"FunCallExpr("+fc.f+")");
	  for(Expr r:fc.args)
		printe(n,visit(r));
	}
	case ClassFunCallExpr cfc->{
	  printv(n,"ClassFunCallExpr");
	  printe(n,visit(cfc.object));
	  printe(n,visit(cfc.call));
	}
	case BinOp bo->{
	  printv(n,"BinOp");
	  printe(n,visit(bo.lhs));
	  int bon=nc++;
	  printv(bon,bo.op.toString());
	  printe(n,bon);
	  printe(n,visit(bo.rhs));
	}
	case ArrayAccessExpr ra->{
	  printv(n,"ArrayAccessExpr");
	  printe(n,visit(ra.arr));
	  printe(n,visit(ra.ind));
	}
	case FieldAccessExpr fa->{
	  printv(n,"FieldAccessExpr("+fa.field+")");
	  printe(n,visit(fa.struct));
	}
	case ValueAtExpr va->{
	  printv(n,"ValueAtExpr");
	  printe(n,visit(va.e));
	}
	case AddressOfExpr ao->{
	  printv(n,"AddressOfExpr");
	  printe(n,visit(ao.e));
	}
	case SizeOfExpr so->{
	  printv(n,"SizeOfExpr");
	  printe(n,visit(so.t));
	}
	case TypecastExpr tc->{
	  printv(n,"TypecastExpr");
	  printe(n,visit(tc.t));
	  printe(n,visit(tc.e));
	}
	case ClassInstantiationExpr cie->{
	  printv(n,"ClassInstantiationExpr");
	  printe(n,visit(cie.t));
	}
	case Assign as->{
	  printv(n,"Assign");
	  printe(n,visit(as.lhs));
	  printe(n,visit(as.rhs));
	}
	case ExprStmt es->{
	  printv(n,"ExprStmt");
	  printe(n,visit(es.e));
	}
	case While w->{
	  printv(n,"While");
	  printe(n,visit(w.c));
	  printe(n,visit(w.y));
	}
	case If ie->{
	  printv(n,"If");
	  printe(n,visit(ie.c));
	  printe(n,visit(ie.y));
	  if(ie.n!=null)
		printe(n,visit(ie.n));
	}
	case Return r->{
	  printv(n,"Return");
	  if(r.e!=null)
		printe(n,visit(r.e));
	}
	case Block b->{
	  printv(n,"Block");
	  for(ASTNode bs:b.children())
		printe(n,visit(bs));
	}
	}
	return n;
  }
}
