package ast;
import java.util.ArrayList;
import java.util.List;
public final class ClassFunCallExpr extends Expr{
  public final Expr object;
  public final FunCallExpr call;
  public ClassFunCallExpr(Expr object,FunCallExpr call){
	this.object=object;
	this.call=call;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(object);
	children.add(call);
	return children;
  }
}
