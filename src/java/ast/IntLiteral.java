package ast;
import java.util.ArrayList;
import java.util.List;
public final class IntLiteral extends Expr{
  public final int v;
  public IntLiteral(int v){
	this.v=v;
  }
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
}
