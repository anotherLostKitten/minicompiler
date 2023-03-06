package ast;
import java.util.ArrayList;
import java.util.List;
public final class PointerType implements Type{
  public final Type type;
  public PointerType(Type type){
	this.type=type;
  }
  public int size(){
	return 4;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	return children;
  }
  public boolean equals(Type t2){
	return switch(t2){
	case PointerType b->
	  b.type.equals(this.type);
	case default->
	  false;
	};
  }
}
