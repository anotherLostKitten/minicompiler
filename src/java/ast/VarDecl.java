package ast;
import gen.asm.Label;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public final class VarDecl extends Decl{
  public boolean g;
  public int o,s;
  public Label l;
  public VarDecl(Type type,String name){
	this.type=type;
	this.name=name;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(this.type);
	return children;
  }
}
