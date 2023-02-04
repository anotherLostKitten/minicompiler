package ast;
import java.util.ArrayList;
import java.util.List;
public final class ValueAtExpr extends Expr{
  public final Expr e;
  public ValueAtExpr(Expr e){
	this.e=e;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(e);
	return children;
  }
}
