package ast;
import java.util.ArrayList;
import java.util.List;
public final class ClassDecl extends Decl{
  public ClassType type,parent;
  public final List<VarDecl>vs;
  public final List<FunDecl>fs;
  public int size;//todo sizing
  public ClassDecl(ClassType type,ClassType par,List<VarDecl>vs,List<FunDecl>fs){
	this.type=type;
	this.parent=par;
	this.vs=vs;
	this.fs=fs;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(type);
	if(parent!=null)
	  children.add(parent);
	children.addAll(vs);
	children.addAll(fs);
	return children;
  }
}
