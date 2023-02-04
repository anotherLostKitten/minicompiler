package ast;
import java.util.ArrayList;
import java.util.List;
public final class StructTypeDecl extends Decl{
  public StructType type;
  public final List<VarDecl>lms;
  public StructTypeDecl(StructType type,List<VarDecl>lms){
	this.type=type;
	this.lms=lms;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	children.addAll(lms);
	return children;
  }
}
