package gen;
import ast.*;
import gen.asm.*;
import java.util.HashMap;
import java.util.Map;
public class MemAllocCodeGen extends CodeGen{
  AssemblyProgram.Section ds;
  Map<String,Label>strls;
  public MemAllocCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
	this.strls=new HashMap<String,Label>();
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
	case ClassType t->{}//todo? i think do nothing
	case ArrayType t->{}
	case StructTypeDecl std->{
	  g=2;
	  fpo=0;
	  for(VarDecl v:std.vs)
		visit(v);
	  std.size=fpo;
	  g=0;
	}
	case ClassDecl cd->{//todo? calculate size of declared class
	  g=2;
	  fpo=cd.parent==null?4:cd.parent.decl.size;//4 is vtable address size
	  for(VarDecl v:cd.vs)
		visit(v);
	  cd.size=fpo;
	  int vto=-4;
	  StringBuilder sb=new StringBuilder("word ");
	  for(FunDecl f:cd.vt.values()){
		f.vto=vto+=4;//todo? check sign for vto?
		f.pcpc=cd;
		if(f.in==null)
		  visit(f);
		sb.append(f.in.name+", ");//todo? do i care about comma trailing?
	  }
	  ds.emit(cd.vtl=Label.create("virtual_table_"+cd.type.name));
	  ds.emit(new Directive(sb.toString()));
	}
	case VarDecl vd->{
	  vd.s=(vd.type.size()-1|3)+1;//to pad
	  vd.g=g==0;
	  switch(g){
	  case 0://global
		ds.emit(vd.l=Label.create(vd.name));
		ds.emit(new Directive("space "+vd.s));
		break;
	  case 1://function body
		if(!vd.r){
		  fpo-=vd.s;
		  vd.o=fpo;
		}else
		  vd.vr=Register.Virtual.create();
		break;
	  case 2://function param/struct field
		vd.o=fpo;
		fpo+=vd.s;
	  }
	}
	case FunDecl fd->{
	  fd.in=Label.create("function_"+fd.name);
	  fd.out=Label.create("function_return_"+fd.name);
	  g=2;
	  fd.rvo=fpo=(fd.type.size()-1|3)+5;//return address, return val
	  if(fd.cdl)
		fpo+=4;//offset for implicit class pointer in functions
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
	  s.l=strls.get(s.v);
	  if(s.l==null){
		StringBuilder sb=new StringBuilder("byte ");
		ds.emit(s.l=Label.create("string_ligeral"));
		for(int i=0;i<s.v.length();i++)
		  sb.append((int)s.v.charAt(i)+", ");
		sb.append("0");
		ds.emit(new Directive(sb.toString()));
		ds.emit(new Directive("align 2"));
		strls.put(s.v,s.l);
	  }
	}
	case ChrLiteral c->{}
	case VarExpr v->{}
	case FunCallExpr fc->{
	  for(Expr r:fc.args)
		visit(r);
	  if(fc.type instanceof StructType st)
		fc.o=fpo-=(st.size()-1|3)+1;//just allocate space on the stack for return values of any funciton which returns a struct (lazy solution)
	}
	case ClassFunCallExpr cfc->{//todo? think just need check for str literals?
	  visit(cfc.object);
	  visit(cfc.call);
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
	case ClassInstantiationExpr cie->{}//todo? no static mem i think?
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
	  if(r.e!=null)
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
