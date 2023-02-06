package sem;
import ast.VarDecl;
public class VarSymbol extends Symbol{
  VarDecl vd;
  public VarSymbol(VarDecl vd){
	this.vd=vd;
	this.name=vd.name;
  }
}
