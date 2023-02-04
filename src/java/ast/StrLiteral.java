package ast;
import java.util.ArrayList;
import java.util.List;
public final class StrLiteral extends Expr{
  public final String v;
  public StrLiteral(String v){
	this.v=v;
  }
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
}
