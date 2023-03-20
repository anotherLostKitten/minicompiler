package ast;
import gen.asm.Label;
import gen.asm.Register;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public final class VarDecl extends Decl{
  public boolean g,r;
  public int o,s;
  public Label l;
  public Register vr;
  public VarDecl(Type type,String name){
	this.type=type;
	this.name=name;
	this.r=true;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(this.type);
	return children;
  }
}
