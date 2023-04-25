package ast;
import java.util.ArrayList;
import java.util.List;
public final class ClassInstantiationExpr extends Expr{
  public final ClassType t;
  public ClassInstantiationExpr(ClassType t){
	this.t=t;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(t);
	return children;
  }
}
