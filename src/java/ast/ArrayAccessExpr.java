package ast;
import java.util.ArrayList;
import java.util.List;
public final class ArrayAccessExpr extends Expr{
  public final Expr arr,ind;
  public ArrayAccessExpr(Expr arr,Expr ind){
	this.arr=arr;
	this.ind=ind;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(ind);
	children.add(arr);
	return children;
  }
}
