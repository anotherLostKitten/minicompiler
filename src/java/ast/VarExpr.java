package ast;
import java.util.ArrayList;
import java.util.List;
public final class VarExpr extends Expr{
  public final String name;
  public VarDecl vd;
  public VarExpr(String name){
	this.name=name;
  }
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
}
