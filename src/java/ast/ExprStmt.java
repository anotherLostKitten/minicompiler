package ast;
import java.util.ArrayList;
import java.util.List;
public final class ExprStmt extends Stmt{
  public final Expr e;
  public ExprStmt(Expr e){
	this.e=e;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(e);
	return children;
  }
}
