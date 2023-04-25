package ast;
import java.util.ArrayList;
import java.util.List;
public final class ClassType implements Type{
  public final String name;
  public ClassTypeDecl decl;
  public ClassType(String name){
	this.name=name;
  }
  public int size(){//todo will need to figure out sizing for codegen
	return decl==null?0:decl.size;
  }
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
  public boolean equals(Type t2){
	return switch(t2){
	case ClassType b->
	  b.name.equals(this.name);
	case default->
	  false;
	};
  }
}
