package sem;
import ast.*;
import ast.BinOp.Op;
public class TypeAnalyzer extends BaseSemanticAnalyzer{
  public Type visit(ASTNode node){
	return switch(node){
	case null->{
	  throw new IllegalStateException("Unexpected null value");
	}
	case Program p->{
	  //todo
	  yield BaseType.NONE;
	}
	case Type t->{yield t;}
	case StructTypeDecl std->{
	  //todo
	  yield BaseType.NONE;
	}
	case VarDecl vd->{
	  if(vd.type==BaseType.VOID)
		error("VarDecl void type "+vd);
	  yield BaseType.NONE;
	}
	case FunDecl fd->{yield BaseType.NONE;}
	case IntLiteral i->{
	  i.type=BaseType.INT;
	  yield i.type;
	}
	case StrLiteral s->{
	  s.type=new ArrayType(BaseType.CHAR,s.v.length()+1);
	  yield s.type;
	}
	case ChrLiteral c->{
	  c.type=BaseType.CHAR;
	  yield c.type;
	}
	case VarExpr v->{
	  if(v.vd!=null)
		v.type=v.vd.type;
	  else{
		error("VarExpr unknown type as declaration missing "+v.name);
		v.type=BaseType.UNKNOWN;
	  }
	  yield v.type;
	}
	case FunCallExpr fc->{
	  if(fc.fd==null){
		error("FunCallExpr unknown type as declaration missing "+fc.f);
		fc.type=BaseType.UNKNOWN;
	  }else{
		int n=fc.args.size();
		if(n==fc.fd.params.size()){
		  for(int i=0;i<n;i++)
			if(!visit(fc.args.get(i)).equals(fc.fd.params.get(i).type))
			  error("FunCallExpr argument "+i+" types differ "+fc.f);
		}else
		  error("FunCallExpr number arguments differ "+fc.f+"; expected "+fc.fd.params.size()+" found "+n);
		fc.type=fc.fd.type;
	  }
	  yield fc.type;
	}
	case BinOp bo->{
	  bo.type=switch(bo.op){
	  case NE,EQ->{
		visit(bo.lhs);
		visit(bo.rhs);
		if(bo.lhs.type instanceof StructType||bo.lhs.type instanceof ArrayType||bo.lhs.type==BaseType.VOID||!bo.lhs.type.equals(bo.rhs.type))
			yield BaseType.UNKNOWN;
		yield BaseType.INT;
	  }
	  case default->{
		if(visit(bo.lhs)==BaseType.INT&&visit(bo.rhs)==BaseType.INT)
		  yield BaseType.INT;
		yield BaseType.UNKNOWN;
	  }
	  };
	  yield bo.type;
	}
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
