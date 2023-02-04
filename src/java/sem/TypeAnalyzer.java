package sem;
import ast.*;
public class TypeAnalyzer extends BaseSemanticAnalyzer{
  public Type visit(ASTNode node){
	return switch(node){
	case null->{
	  throw new IllegalStateException("Unexpected null value");
	}
	case Program p->{yield BaseType.NONE;}
	case Type t->{
	  yield t;
	}
	case StructTypeDecl std->{yield BaseType.NONE;}
	case VarDecl vd->{yield BaseType.NONE;}
	case FunDecl fd->{yield BaseType.NONE;}
	case IntLiteral i->{yield BaseType.NONE;}
	case StrLiteral s->{yield BaseType.NONE;}
	case ChrLiteral c->{yield BaseType.NONE;}
	case VarExpr v->{yield BaseType.NONE;}
	case FunCallExpr fc->{yield BaseType.NONE;}
	case BinOp bo->{yield BaseType.NONE;}
	case ArrayAccessExpr ra->{yield BaseType.NONE;}
	case FieldAccessExpr fa->{yield BaseType.NONE;}
	case ValueAtExpr va->{yield BaseType.NONE;}
	case AddressOfExpr ao->{yield BaseType.NONE;}
	case SizeOfExpr so->{yield BaseType.NONE;}
	case TypecastExpr tc->{yield BaseType.NONE;}
	case Assign as->{yield BaseType.NONE;}
	case ExprStmt es->{yield BaseType.NONE;}
	case While w->{yield BaseType.NONE;}
	case If ie->{yield BaseType.NONE;}
	case Return r->{yield BaseType.NONE;}
	case Block b->{
	  for(ASTNode c:b.children())
		visit(b);
	  yield BaseType.NONE;
	}
	};
  }
}
