package sem;
import ast.*;
public class TypeAnalyzer extends BaseSemanticAnalyzer{
  public Type visit(ASTNode node){
	return switch(node){
	case null->{
	  throw new IllegalStateException("Unexpected null value");
	}
	case Block b->{
	  for(ASTNode c:b.children())
		visit(b);
	  yield BaseType.NONE;
	}
	case FunDecl fd->{
	  //todo
	  yield BaseType.NONE;
	}
	case Program p->{
	  //todo
	  yield BaseType.NONE;
	}
	case VarDecl vd->{
	  //todo
	  yield BaseType.NONE;
	}
	case VarExpr v->{
	  //todo
	  yield BaseType.UNKNOWN; // to change
	}
	case StructTypeDecl std->{
	  //todo
	  yield BaseType.UNKNOWN;//to change
	}
	case Type t->{
	  yield t;
	}
	//todo
	};
  }
}
