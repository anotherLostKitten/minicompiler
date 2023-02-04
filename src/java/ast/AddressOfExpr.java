package ast;
import java.util.ArrayList;
import java.util.List;
public final class AddressOfExpr extends Expr{
  public final Expr e;
  public AddressOfExpr(Expr e){
	this.e=e;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(e);
	return children;
  }
}
