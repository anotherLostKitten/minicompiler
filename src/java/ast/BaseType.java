package ast;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
public enum BaseType implements Type{
  INT,CHAR,VOID,UNKNOWN,NONE;
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
  public int size(){
	switch(this){
	case INT:
	  return 4;
	case CHAR:
	case VOID:
	  return 1;
	}
	return 0;
  }
  public boolean equals(Type t2){
	return switch(t2){
	case BaseType b->
	  b==this&&b!=UNKNOWN;
	case default->
	  false;
	};
  }
}
