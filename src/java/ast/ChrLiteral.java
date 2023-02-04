package ast;
import java.util.ArrayList;
import java.util.List;
public final class ChrLiteral extends Expr{
  public final char v;
  public ChrLiteral(char v){
	this.v=v;
  }
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
}
