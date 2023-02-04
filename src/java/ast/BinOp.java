package ast;
import java.util.ArrayList;
import java.util.List;
public final class BinOp extends Expr{
  public enum Op{
	ADD,SUB,MUL,DIV,MOD,GT,LT,GE,LE,NE,EQ,OR,AND
  }
  public final Expr lhs,rhs;
  public final Op op;
  public BinOp(Expr lhs,Expr rhs,Op op){
	this.lhs=lhs;
	this.rhs=rhs;
	this.op=op;
  }
  public List<ASTNode>children(){
	List<ASTNode>children=new ArrayList<ASTNode>();
	children.add(lhs);
	children.add(rhs);
	return children;
  }
}
