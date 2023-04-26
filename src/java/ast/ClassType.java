package ast;
import java.util.ArrayList;
import java.util.List;
public final class ClassType implements Type{
  public final String name;
  public ClassDecl decl;
  public ClassType(String name){
	this.name=name;
  }
  public int size(){//todo? should just be pointer; get decl if you actually want size
	return 4;
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
