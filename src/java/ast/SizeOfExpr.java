package ast;
import java.util.ArrayList;
import java.util.List;
public final class SizeOfExpr extends Expr{
  public final Type t;
  public int v;
  public SizeOfExpr(Type t){
	this.t=t;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(t);
	return children;
  }
}
