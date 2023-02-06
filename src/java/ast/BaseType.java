package ast;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
public enum BaseType implements Type{
  INT,CHAR,VOID,UNKNOWN,NONE;
  public List<ASTNode>children(){
	return new ArrayList<ASTNode>();
  }
  public boolean equals(Type t2){
	return switch(t2){
	case BaseType b->{
	  yield b==this&&b!=UNKNOWN&&t2!=UNKNOWN;
	}
	case default->{
	  yield false;
	}
	};
  }
}
