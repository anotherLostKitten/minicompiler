package ast;
import java.util.ArrayList;
import java.util.List;
public final class StructTypeDecl extends Decl{
  public StructType type;
  public final List<VarDecl>vs;
  public StructTypeDecl(StructType type,List<VarDecl>vs){
	this.type=type;
	this.vs=vs;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	children.addAll(vs);
	return children;
  }
}
