package ast;
import java.util.ArrayList;
import java.util.List;
public final class StructType implements Type{
  public final String name;
  public StructTypeDecl decl;
  public StructType(String name){
	this.name=name;
  }
  public int size(){
	return decl==null?0:decl.size;
  }
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
  public boolean equals(Type t2){
	return switch(t2){
	case StructType b->
	  b.name.equals(this.name);
	case default->
	  false;
	};
  }
}
