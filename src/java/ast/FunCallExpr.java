package ast;
import java.util.ArrayList;
import java.util.List;
public final class FunCallExpr extends Expr{
  public final String f;
  public final List<Expr>args;
  public FunDecl fd;
  public int o;
  public FunCallExpr(String f,List<Expr>args){
	this.f=f;
	this.args=args;
	this.fd=null;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.addAll(args);
	return children;
  }
}
