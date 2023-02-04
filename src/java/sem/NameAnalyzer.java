package sem;
import ast.*;
public class NameAnalyzer extends BaseSemanticAnalyzer{
  public void visit(ASTNode node){
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{}
	case BaseType t->{}
	case PointerType t->{}
	case StructType t->{}
	case ArrayType t->{}
	case StructTypeDecl std->{}
	case VarDecl vd->{}
	case FunDecl fd->{}
	case IntLiteral i->{}
	case StrLiteral s->{}
	case ChrLiteral c->{}
	case VarExpr v->{}
	case FunCallExpr fc->{}
	case BinOp bo->{}
	case ArrayAccessExpr ra->{}
	case FieldAccessExpr fa->{}
	case ValueAtExpr va->{}
	case AddressOfExpr ao->{}
	case SizeOfExpr so->{}
	case TypecastExpr tc->{}
	case Assign as->{}
	case ExprStmt es->{}
	case While w->{}
	case If ie->{}
	case Return r->{}
	case Block b->{}
	};
  }
}
