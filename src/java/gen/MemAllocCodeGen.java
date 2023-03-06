package gen;
import ast.*;
import gen.asm.*;
//allocator for global & local variable decls
public class MemAllocCodeGen extends CodeGen{
  AssemblyProgram.Section ds;
  public MemAllocCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
  }
  int g=0;//0:global, 1:function body, 2:struct/function params
  int fpo=0;
  void visit(ASTNode n){
	switch(n){
	case Program p->{
	  ds=asmProg.getCurrentSection();
	  for(Decl d:p.decls)
		visit(d);
	}
	case BaseType t->{}
	case PointerType t->{}
	case StructType t->{}
	case ArrayType t->{}
	case StructTypeDecl std->{
	  g=2;
	  fpo=0;
	  for(VarDecl v:std.vs)
		visit(v);
	  std.size=fpo;
	  g=0;
	}
	case VarDecl vd->{
	  int s=(vd.type.size()-1|3)+1;//to pad
	  vd.g=g==0;
	  switch(g){
	  case 0:
		ds.emit(vd.l=Label.create(vd.name));
		ds.emit(new Directive("space "+s));
		break;
	  case 1:
		fpo-=s;
		vd.o=fpo;
		break;
	  case 2:
		vd.o=fpo;
		fpo+=s;
	  }
	}
	case FunDecl fd->{
	  fd.in=Label.create("function_"+fd.name);
	  fd.out=Label.create("function_return_"+fd.name);
	  g=2;
	  fpo=(fd.type.size()-1|3)+5;//return address, return val
	  for(VarDecl v:fd.params)
		visit(v);
	  fd.co=fpo;
	  g=1;
	  fpo=-4;//skip frame pointer
	  visit(fd.block);
	  fd.size=fpo;
	  g=0;
	}
	case IntLiteral i->{}
	case StrLiteral s->{
	  StringBuilder sb=new StringBuilder("byte ");
	  ds.emit(s.l=Label.create("string_ligeral"));
	  for(int i=0;i<s.v.length();i++)
		sb.append((int)s.v.charAt(i)+", ");
	  sb.append("0");
	  ds.emit(new Directive(sb.toString()));
	  ds.emit(new Directive("align 2"));
	}
	case ChrLiteral c->{}
	case VarExpr v->{}
	case FunCallExpr fc->{
	  if(fc.type instanceof StructType st){
		fc.o=fpo;
		fpo+=(st.size()-1|3)+1;
	  }
	  for(Expr r:fc.args)
		visit(r);
	}
	case BinOp bo->{
	  visit(bo.rhs);
	  visit(bo.lhs);
	}
	case ArrayAccessExpr ra->{
	  visit(ra.arr);
	  visit(ra.ind);
	}
	case FieldAccessExpr fa->
	  visit(fa.struct);
	case ValueAtExpr va->
	  visit(va.e);
	case AddressOfExpr ao->
	  visit(ao.e);
	case SizeOfExpr so->{}
	case TypecastExpr tc->
	  visit(tc.e);
	case Assign as->{
	  visit(as.lhs);
	  visit(as.rhs);
	}
	case ExprStmt es->
	  visit(es.e);//literally just for string literals xd
	case While w->{
	  visit(w.c);
	  visit(w.y);
	}
	case If ie->{
	  visit(ie.c);
	  visit(ie.y);
	  if(ie.n!=null)
		visit(ie.n);
	}
	case Return r->{
	  visit(r.e);
	}
	case Block b->{
	  for(VarDecl v:b.vds)
		visit(v);
	  for(Stmt s:b.stmts)
		visit(s);
	}
	};
  }
}
