package ast;
import java.util.ArrayList;
import java.util.List;
public final class ArrayType implements Type{
  public final Type type;
  public final int num;
  public ArrayType(Type type,int num){
	this.type=type;
	this.num=num;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	return children;
  }
}
