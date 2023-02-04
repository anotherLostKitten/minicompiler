package ast;
import java.util.ArrayList;
import java.util.List;
public final class PointerType implements Type{
  public final Type type;
  public PointerType(Type type){
	this.type=type;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	return children;
  }
}
