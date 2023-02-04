package sem;
import ast.*;
public class NameAnalyzer extends BaseSemanticAnalyzer{
  public void visit(ASTNode node){
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Block b->{
	  //todo
	}
	case FunDecl fd->{
	  //todo
	}
	case Program p->{
	  //todo
	}
	case VarDecl vd->{
	  //todo
	}
	case VarExpr v->{
	  //todo
	}
	case StructTypeDecl std->{
	  //todo
	}
	case Type t->{
	  //todo
	}
	//todo
	};
  }
}
