package ast;
import java.util.ArrayList;
import java.util.List;
public final class FieldAccessExpr extends Expr{
  public final Expr struct;
  public final String field;
  public FieldAccessExpr(Expr struct,String field){
	this.struct=struct;
	this.field=field;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(struct);
	return children;
  }
}
