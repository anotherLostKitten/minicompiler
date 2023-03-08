package ast;
import gen.asm.Label;
import java.util.ArrayList;
import java.util.List;
public final class FunDecl extends Decl{
  public final List<VarDecl>params;
  public final Block block;
  public final boolean sc;
  public int size,co,rvo;
  public Label in,out;
  public FunDecl(Type type,String name,List<VarDecl>params,Block block){
	this.type=type;
	this.name=name;
	this.params=params;
	this.block=block;
	this.sc=false;
  }
  public FunDecl(Type type,String name,List<VarDecl>params,Block block,boolean sc){
	this.type=type;
	this.name=name;
	this.params=params;
	this.block=block;
	this.sc=sc;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.addAll(params);
	children.add(block);
	return children;
  }
}
