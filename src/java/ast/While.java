package ast;
import java.util.ArrayList;
import java.util.List;
public final class While extends Stmt{
  public final Expr c;
  public final Stmt y;
  public While(Expr c,Stmt y){
	this.c=c;
	this.y=y;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(c);
	children.add(y);
	return children;
  }
}
