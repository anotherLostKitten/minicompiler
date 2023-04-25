package sem;
import ast.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class LvalAnalyzer extends BaseSemanticAnalyzer{
  public boolean visit(ASTNode node){
	return switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{
	  for(Decl d:p.decls)
		visit(d);
	  yield false;
	}
	case Type t->false;
	case StructTypeDecl std->false;
	case ClassDecl cd->{
	  for(FunDecl fd:cd.fs)
		visit(fd);
	  yield false;
	}
	case VarDecl vd->false;
	case FunDecl fd->visit(fd.block);
	case IntLiteral i->false;
	case StrLiteral s->false;
	case ChrLiteral c->false;
	case VarExpr v->true;
	case FunCallExpr fc->{
	  for(Expr r:fc.args)
		visit(r);
	  yield false;
	}
	case ClassFunCallExpr cfc->{
	  visit(cfc.object);
	  visit(cfc.call);
	  yield false;
	}
	case BinOp bo->{
	  visit(bo.lhs);
	  visit(bo.rhs);
	  yield false;
	}
	case ArrayAccessExpr ra->{
	  visit(ra.arr);
	  visit(ra.ind);
	  yield true;
	}
	case FieldAccessExpr fa->
	  visit(fa.struct);
	case ValueAtExpr va->{
	  visit(va.e);
	  yield true;
	}
	case AddressOfExpr ao->{
	  if(!visit(ao.e))
		error("AddressOfExpr lval error");
	  else if(ao.e instanceof VarExpr ve)
		ve.vd.r=false;
	  yield false;
	}
	case SizeOfExpr so->false;
	case TypecastExpr tc->{
	  visit(tc.e);
	  yield false;
	}
	case ClassInstantiationExpr cie->false;
	case Assign as->{
	  if(!visit(as.lhs))
		error("Assign lval error");
	  visit(as.rhs);
	  yield false;
	}
	case ExprStmt es->{
	  visit(es.e);
	  yield false;
	}
	case While w->{
	  visit(w.c);
	  visit(w.y);
	  yield false;
	}
	case If ie->{
	  visit(ie.c);
	  visit(ie.y);
	  if(ie.n!=null)
		visit(ie.n);
	  yield false;
	}
	case Return r->{
	  if(r.e!=null)
		visit(r.e);
	  yield false;
	}
	case Block b->{
	  for(Stmt s:b.stmts)
		visit(s);
	  yield false;
	}
	};
  }
}
