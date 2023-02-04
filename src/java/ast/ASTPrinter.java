package ast;
import java.io.PrintWriter;
public class ASTPrinter{
  private final PrintWriter writer;
  public ASTPrinter(PrintWriter writer){
	this.writer=writer;
  }
  public void visit(ASTNode node){
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{
	  writer.print("Program(");
	  String delimiter="";
	  for(StructTypeDecl std:p.structTypeDecls){
		writer.print(delimiter);
		delimiter=",";
		visit(std);
	  }
	  for(VarDecl vd:p.varDecls){
		writer.print(delimiter);
		delimiter=",";
		visit(vd);
	  }
	  for(FunDecl fd:p.funDecls){
		writer.print(delimiter);
		delimiter=",";
		visit(fd);
	  }
	  writer.print(")");
	  writer.flush();
	}
	case BaseType t->{
	  //todo
	}
	case PointerType t->{
	}
	case StructType t->{
	}
	case ArrayType t->{
	}
	case StructTypeDecl std->{
	  //todo
	}
	case VarDecl vd->{
	  writer.print("VarDecl(");
	  visit(vd.type);
	  writer.print(","+vd.name);
	  writer.print(")");
	}
	case FunDecl fd->{
	  writer.print("FunDecl(");
	  visit(fd.type);
	  writer.print(","+fd.name+",");
	  for(VarDecl vd:fd.params){
		visit(vd);
		writer.print(",");
	  }
	  visit(fd.block);
	  writer.print(")");
	}
	case IntLiteral i->{}
	case StrLiteral s->{}
	case ChrLiteral c->{}
	case VarExpr v->{
	  writer.print("VarExpr(");
	  writer.print(v.name);
	  writer.print(")");
	}
	case FunCallExpr fc->{}
	case BinOp bo->{}
	case ArrayAccessExpr ra->{}
	case FieldAccessExpr fa->{}
	case ValueAtExpr va->{}
	case AddressOfExpr ao->{}
	case SizeOfExpr so->{}
	case TypecastExpr tc->{}
	case Assign as->{}
	case ExprStmt es->{}
	case While w->{}
	case If ie->{}
	case Return r->{}
	case Block b->{
	  writer.print("Block(");
	  //todo
	  writer.print(")");
	}
	}
  }
}
