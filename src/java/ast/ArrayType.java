package ast;
import java.util.ArrayList;
import java.util.List;
public final class ArrayType implements Type{
  public final Type type;
  public final int num;
  private int size=-1;
  public ArrayType(Type type,int num){
	this.type=type;
	this.num=num;
  }
  public int size(){
	if(size<0)
	  size=type.size()*num;
	return size;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	return children;
  }
  public boolean equals(Type t2){
	return switch(t2){
	case ArrayType b->
	  b.num==this.num&&b.type.equals(this.type);
	case default->
	  false;
	};
  }
}
