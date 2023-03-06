package ast;
import java.util.ArrayList;
import java.util.List;
public final class Return extends Stmt{
  public final Expr e;
  public FunDecl d;
  public Return(Expr e){
	this.e=e;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	if(e!=null)
	  children.add(e);
	return children;
  }
}
