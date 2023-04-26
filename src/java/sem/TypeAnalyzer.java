package sem;
import ast.*;
import ast.BinOp.Op;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
public class TypeAnalyzer extends BaseSemanticAnalyzer{
  private FunDecl frt=null;
  private Map<String,StructTypeDecl>structs;
  private Map<String,ClassDecl>classes;
  public Type visit(ASTNode node){
	return switch(node){
	case null->{
	  throw new IllegalStateException("Unexpected null value");
	}
	case Program p->{
	  structs=new HashMap<String,StructTypeDecl>();
	  classes=new HashMap<String,ClassDecl>();
	  for(Decl d:p.decls){
		visit(d);
		if(d instanceof VarDecl vd)
		  vd.r=false;
	  }
	  yield BaseType.NONE;
	}
	case BaseType t->t;
	case PointerType t->{
	  visit(t.type);
	  yield t;
	}
	case StructType t->{
	  if((t.decl=structs.get(t.name))!=null)
		yield t;
	  error("StructType references undefined struct "+t.name);
	  yield BaseType.UNKNOWN;
	}
	case ClassType t->{
	  if((t.decl=classes.get(t.name))!=null)
		yield t;
	  error("ClassType references undefined class "+t.name);
	  yield BaseType.UNKNOWN;
	}
	case ArrayType t->{
	  visit(t.type);
	  yield t;
	}
	case StructTypeDecl std->{
	  std.type.decl=std;
	  std.vst=new HashMap<String,VarDecl>();
	  for(VarDecl vd:std.vs){
		visit(vd);
		std.vst.put(vd.name,vd);//assumes name analysis passed
	  }
	  if(structs.containsKey(std.type.name))
		error("StructTypeDecl struct name in use");
	  else
		structs.put(std.type.name,std);
	  yield BaseType.NONE;
	}
	case ClassDecl cd->{
	  cd.type.decl=cd;
	  //todo? make sure overriding types match, build vst &c. is correct
	  if(cd.parent==null){
		cd.vst=new LinkedHashMap<String,VarDecl>();
		cd.vt=new LinkedHashMap<String,FunDecl>();
	  }else{
		cd.parent.decl=classes.get(cd.parent.name);
		cd.vst=new LinkedHashMap<String,VarDecl>(cd.parent.decl.vst);
		cd.vt=new LinkedHashMap<String,FunDecl>(cd.parent.decl.vt);
	  }
	  for(VarDecl vd:cd.vs){
		visit(vd);
		cd.vst.put(vd.name,vd);
	  }
	  for(FunDecl fd:cd.fs){
		visit(fd);
		if(cd.vst.containsKey(fd.name))
		  error("ClassDecl function overriding a field "+fd.name);
		FunDecl pfd=cd.vt.get(fd.name);
		if(pfd!=null)
		  if(!pfd.type.equals(fd.type))
			error("ClassDecl function override different return type "+fd.name);
		  else if(pfd.params.size()!=fd.params.size())
			error("ClassDecl function override different number args "+fd.name);
		  else
			for(int i=0;i<fd.params.size();i++)
			  if(!pfd.params.get(i).type.equals(fd.params.get(i).type))
				error("ClassDecl function override "+fd.name+" different arg at "+i);
		cd.vt.put(fd.name,fd);
	  }
	  if(classes.containsKey(cd.type.name))
		error("ClassDecl class name in use");
	  else
		classes.put(cd.type.name,cd);
	  yield BaseType.NONE;
	}
	case VarDecl vd->{
	  switch(vd.type){
	  case BaseType t->{
		if(t==BaseType.VOID)
		  error("VarDecl void type "+vd.name);
	  }
	  case PointerType t->{visit(t);}
	  case Type t->{
		visit(t);
		vd.r=false;
	  }
	  }
	  yield BaseType.NONE;
	}
	case FunDecl fd->{
	  visit(fd.type);
	  frt=fd;
	  for(VarDecl vd:fd.params){
		visit(vd);
		vd.r=false;
	  }
	  visit(fd.block);
	  frt=null;
	  yield BaseType.NONE;
	}
	case IntLiteral i->
	  i.type=BaseType.INT;
	case StrLiteral s->
	  s.type=new ArrayType(BaseType.CHAR,s.v.length()+1);
	case ChrLiteral c->
	  c.type=BaseType.CHAR;
	case VarExpr v->{
	  if(v.vd==null){
		error("VarExpr unknown type as declaration missing "+v.name);
		yield v.type=BaseType.UNKNOWN;
	  }else
		yield v.type=v.vd.type;
	}
	case FunCallExpr fc->{
	  if(fc.fd==null){
		error("FunCallExpr unknown type as declaration missing "+fc.f);
		yield fc.type=BaseType.UNKNOWN;
	  }
	  int n=fc.args.size();
	  if(n==fc.fd.params.size()){
		for(int i=0;i<n;i++)
		  if(!visit(fc.args.get(i)).equals(fc.fd.params.get(i).type))
			error("FunCallExpr argument "+i+" types differ "+fc.f);
	  }else
		error("FunCallExpr #args != "+fc.f+" expect "+fc.fd.params.size()+" found "+n);
	  yield fc.type=fc.fd.type;
	}
	case ClassFunCallExpr cfc->{
	  yield cfc.type=switch(visit(cfc.object)){
	  case ClassType ct->{
		FunDecl fd=cfc.call.fd=ct.decl.vt.get(cfc.call.f);
		if(fd!=null){
		  int n=cfc.call.args.size();
		  if(n==fd.params.size()){
			for(int i=0;i<n;i++)
			  if(!visit(cfc.call.args.get(i)).equals(fd.params.get(i).type))
				error("ClassFunCallExpr argument "+i+" types differ "+cfc.call.f);
		  }else
			error("ClassFunCallExpr #args != "+cfc.call.f+": e"+fd.params.size()+" f"+n);
		  yield cfc.call.type=fd.type;
		}
		error("ClassFunCallExpr class does not have method "+cfc.call.f);
		yield cfc.call.type=BaseType.UNKNOWN;
	  }
	  default->{
		error("ClassFunCallExpr not a class "+cfc.call.f);
		yield BaseType.UNKNOWN;
	  }
	  };
	}
	case BinOp bo->{
	  bo.type=switch(bo.op){
	  case NE,EQ->{
		visit(bo.lhs);
		visit(bo.rhs);
		if(!(bo.lhs.type instanceof StructType)&&!(bo.lhs.type instanceof ArrayType)&&bo.lhs.type!=BaseType.VOID&&bo.lhs.type.equals(bo.rhs.type))
			yield BaseType.INT;
		error("BinOp invalid NE/EQ case");
		yield BaseType.UNKNOWN;
	  }
	  case default->{
		if(visit(bo.lhs)==BaseType.INT&&visit(bo.rhs)==BaseType.INT)
		  yield BaseType.INT;
		error("BinOp invalid integer operator case");
		yield BaseType.UNKNOWN;
	  }
	  };
	  yield bo.type;
	}
	case ArrayAccessExpr ra->{
	  ra.type=switch(visit(ra.arr)){
	  case ArrayType t->t.type;
	  case PointerType t->t.type;
	  case default->{
		error("ArrayAccessExpr invalid not array/pointer");
		yield BaseType.UNKNOWN;
	  }
	  };
	  if(visit(ra.ind)!=BaseType.INT){
		error("ArrayAccessExpr invalid not int index");
		ra.type=BaseType.UNKNOWN;
	  }
	  yield ra.type;
	}
	case FieldAccessExpr fa->{
	  yield fa.type=switch(visit(fa.struct)){
	  case StructType t->{
		if(t.decl!=null){
		  VarDecl ft=t.decl.vst.get(fa.field);
		  if(ft!=null)
			yield ft.type;
		  error("FieldAccessExpr undefined struct field <"+t.name+">."+fa.field);
		}else
		  error("FieldAccessExpr struct undefined "+t.name);
		yield BaseType.UNKNOWN;
	  }
	  case ClassType t->{
		if(t.decl!=null){
		  VarDecl ft=t.decl.vst.get(fa.field);
		  if(ft!=null)
			yield ft.type;
		  error("FieldAccessExpr undefined class field <"+t.name+">."+fa.field);
		}else
		  error("FieldAccessExpr class undefined "+t.name);
		yield BaseType.UNKNOWN;
	  }
	  case default->{
		error("FieldAccessExpr not a struct/class");
		yield BaseType.UNKNOWN;
	  }
	  };
	}
	case ValueAtExpr va->{
	  yield va.type=switch(visit(va.e)){
	  case PointerType t->t.type;
	  case default->{
		error("ValueAtExpr invalid not pointer");
		yield BaseType.UNKNOWN;
	  }
	  };
	}
	case AddressOfExpr ao->{
	  if(visit(ao.e)==BaseType.UNKNOWN){
		error("AddressOfExpr invalid unknown type");
		yield ao.type=BaseType.UNKNOWN;
	  }else
		yield ao.type=new PointerType(ao.e.type);
	}
	case SizeOfExpr so->{
	  if(visit(so.t)==BaseType.UNKNOWN)
		error("SizeOfExpr unknown type");
	  yield so.type=BaseType.INT;
	}
	case TypecastExpr tc->{
	  visit(tc.e);
	  visit(tc.t);
	  tc.type=switch(tc.t){
	  case BaseType b->{
		if(b==BaseType.INT&&tc.e.type==BaseType.CHAR)
		  yield BaseType.INT;
		error("TypecastExpr BaseType must be (int)char");
		yield BaseType.UNKNOWN;
	  }
	  case PointerType pt->{
		yield switch(tc.e.type){
		case ArrayType af->{
		  if(af.type.equals(pt.type))
			yield pt;
		  error("TypeCastExpr (T1*)T2[], T1!=T2");
		  yield BaseType.UNKNOWN;
		}
		case PointerType pf->
		  pt;
		case default->{
		  error("TypeCastExpr cannot cast this type into pointer");
		  yield BaseType.UNKNOWN;
		}
		};
	  }
	  case ClassType ct->{
		if(tc.e.type instanceof ClassType cty){
		  for(ClassDecl tmp=cty.decl;tmp.parent!=null&&(tmp=classes.get(tmp.parent.name))!=null;)
			if(tmp.type.name.equals(ct.name))
			  yield(tmp.type);
		  error("TypeCastExpr "+cty.name+"not a descendant of class "+ct.name);
		}else
		  error("TypeCastExpr cannot cast this type into class "+ct.name);
		yield BaseType.UNKNOWN;
	  }
	  case default->{
		error("TypecastExpr cannot cast to this type");
		yield BaseType.UNKNOWN;
	  }
	  };
	  yield tc.type;
	}
	case ClassInstantiationExpr cie->
	  cie.type=visit(cie.t);
	case Assign as->{
	  visit(as.lhs);
	  visit(as.rhs);
	  if(as.lhs.type==BaseType.VOID||as.lhs.type instanceof ArrayType||!as.lhs.type.equals(as.rhs.type)){
		error("Assign invalid types");
		yield as.type=BaseType.UNKNOWN;
	  }else
		yield as.type=as.lhs.type;
	}
	case ExprStmt es->{
	  visit(es.e);
	  yield BaseType.NONE;
	}
	case While w->{
	  visit(w.y);
	  if(visit(w.c)!=BaseType.INT)
		error("While condition not int");
	  yield BaseType.NONE;
	}
	case If ie->{
	  visit(ie.y);
	  if(ie.n!=null)
		visit(ie.n);
	  if(visit(ie.c)!=BaseType.INT)
		error("If condition not int");
	  yield BaseType.NONE;
	}
	case Return r->{
	  r.d=frt;
	  if(r.e==null){
		if(frt.type!=BaseType.VOID)
		  error("Return expected an expression");
	  }else{
		if(!frt.type.equals(visit(r.e)))
		  error("Return type does not match function header");
	  }
	  yield BaseType.NONE;
	}
	case Block b->{
	  for(VarDecl v:b.vds)
		visit(v);
	  for(Stmt s:b.stmts)
		visit(s);
	  yield BaseType.NONE;
	}
	};
  }
}
