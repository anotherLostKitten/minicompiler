package ast;
import java.util.ArrayList;
import java.util.List;
public final class TypecastExpr extends Expr{
  public final Type t;
  public final Expr e;
  public TypecastExpr(Type t,Expr e){
	this.t=t;
	this.e=e;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(t);
	children.add(e);
	return children;
  }
}
