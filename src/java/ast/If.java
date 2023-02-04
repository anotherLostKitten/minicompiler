package ast;
import java.util.ArrayList;
import java.util.List;
public final class If extends Stmt{
  public final Expr c;
  public final Stmt y,n;
  public If(Expr c,Stmt y,Stmt n){
	this.c=c;
	this.y=y;
	this.n=n;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(c);
	children.add(y);
	if(n!=null)
	  children.add(n);
	return children;
  }
}
