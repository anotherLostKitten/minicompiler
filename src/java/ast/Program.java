package ast;
import java.util.ArrayList;
import java.util.List;
public final class Program implements ASTNode{
  public List<Decl>decls;
  public Program(List<Decl>decls){
	this.decls=decls;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.addAll(decls);
	return children;
  }
}
