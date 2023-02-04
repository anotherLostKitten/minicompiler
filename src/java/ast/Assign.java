package ast;
import java.util.ArrayList;
import java.util.List;
public final class Assign extends Expr{
  public final Expr lhs,rhs;
  public Assign(Expr lhs,Expr rhs){
	this.lhs=lhs;
	this.rhs=rhs;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(lhs);
	children.add(rhs);
	return children;
  }
}
