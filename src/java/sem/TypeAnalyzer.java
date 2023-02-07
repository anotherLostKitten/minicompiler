package sem;
import ast.*;
import ast.BinOp.Op;
import java.util.Map;
import java.util.HashMap;
public class TypeAnalyzer extends BaseSemanticAnalyzer{
  private Type frt=BaseType.UNKNOWN;
  private Map<String,Map<String,Type>>structs;
  public Type visit(ASTNode node){
	return switch(node){
	case null->{
	  throw new IllegalStateException("Unexpected null value");
	}
	case Program p->{
	  structs=new HashMap<String,Map<String,Type>>();
	  for(Decl d:p.decls)
		visit(d);
	  yield BaseType.NONE;
	}
	case BaseType t->t;
	case PointerType t->{
	  visit(t.type);
	  yield t;
	}
	case StructType t->{
	  if(structs.containsKey(t.name))
		yield t;
	  error("StructType references undefined struct "+t.name);
	  yield BaseType.UNKNOWN;
	}
	case ArrayType t->{
	  visit(t.type);
	  yield t;
	}
	case StructTypeDecl std->{
	  Map<String,Type>vs=new HashMap<String,Type>();
	  for(VarDecl vd:std.vs){
		visit(vd);
		vs.put(vd.name,vd.type);//assumes name analysis passed
	  }
	  if(structs.containsKey(std.type.name))
		error("StructTypeDecl struct name in use");
	  else
		structs.put(std.type.name,vs);
	  yield BaseType.NONE;
	}
	case VarDecl vd->{
	  switch(vd.type){
	  case StructType t->{
		if(!structs.containsKey(t.name))
		  error("VarDecl "+vd.name+" references undefined struct "+t.name);
	  }
	  case BaseType t->{
		if(t==BaseType.VOID)
		  error("VarDecl void type "+vd.name);
	  }
	  case Type t->{visit(t);}
	  }
	  yield BaseType.NONE;
	}
	case FunDecl fd->{
	  switch(fd.type){
	  case StructType t->{
		if(!structs.containsKey(t.name))
		  error("FunDecl "+fd.name+" returns undefined struct "+t.name);
	  }
	  case Type t->{visit(t);}
	  }
	  for(VarDecl vd:fd.params)
		visit(vd);
	  frt=fd.type;
	  visit(fd.block);
	  frt=BaseType.UNKNOWN;
	  yield BaseType.NONE;
	}
	case IntLiteral i->{
	  i.type=BaseType.INT;
	  yield i.type;
	}
	case StrLiteral s->{
	  s.type=new ArrayType(BaseType.CHAR,s.v.length()+1);
	  yield s.type;
	}
	case ChrLiteral c->{
	  c.type=BaseType.CHAR;
	  yield c.type;
	}
	case VarExpr v->{
	  if(v.vd!=null)
		v.type=v.vd.type;
	  else{
		error("VarExpr unknown type as declaration missing "+v.name);
		v.type=BaseType.UNKNOWN;
	  }
	  yield v.type;
	}
	case FunCallExpr fc->{
	  if(fc.fd==null){
		error("FunCallExpr unknown type as declaration missing "+fc.f);
		fc.type=BaseType.UNKNOWN;
	  }else{
		int n=fc.args.size();
		if(n==fc.fd.params.size()){
		  for(int i=0;i<n;i++)
			if(!visit(fc.args.get(i)).equals(fc.fd.params.get(i).type))
			  error("FunCallExpr argument "+i+" types differ "+fc.f);
		}else
		  error("FunCallExpr number arguments differ "+fc.f+"; expected "+fc.fd.params.size()+" found "+n);
		fc.type=fc.fd.type;
	  }
	  yield fc.type;
	}
	case BinOp bo->{
	  bo.type=switch(bo.op){
	  case NE,EQ->{
		visit(bo.lhs);
		visit(bo.rhs);
		if(!(bo.lhs.type instanceof StructType)&&!(bo.lhs.type instanceof ArrayType)||bo.lhs.type!=BaseType.VOID&&bo.lhs.type.equals(bo.rhs.type))
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
	  fa.type=switch(visit(fa.struct)){
	  case StructType t->{
		Map<String,Type>vs=structs.get(t.name);
		if(vs==null){
		  error("FieldAccessExpr struct undefined "+t.name);
		  yield BaseType.UNKNOWN;
		}
		Type ft=vs.get(fa.field);
		if(ft!=null)
		  yield ft;
		error("FieldAccessExpr undefined field <"+t.name+">."+fa.field);
		yield BaseType.UNKNOWN;
	  }
	  case default->{
		error("FieldAccessExpr not a struct");
		yield BaseType.UNKNOWN;
	  }
	  };
	  yield fa.type;
	}
	case ValueAtExpr va->{
	  va.type=switch(visit(va.e)){
	  case PointerType t->t.type;
	  case default->{
		error("ValueAtExpr invalid not pointer");
		yield BaseType.UNKNOWN;
	  }
	  };
	  yield va.type;
	}
	case AddressOfExpr ao->{
	  if(visit(ao.e)==BaseType.UNKNOWN){
		ao.type=BaseType.UNKNOWN;
		error("AddressOfExpr invalid unknown type");
	  }else
		ao.type=new PointerType(ao.e.type);
	  yield ao.type;
	}
	case SizeOfExpr so->{
	  switch(so.t){
	  case StructType t->{
		if(!structs.containsKey(t.name))
		  error("SizeOfExpr cannot find struct "+t.name);
	  }
	  case Type t->visit(t);
	  };
	  yield BaseType.INT;
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
		yield switch(tc.t){
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
	  case default->{
		error("TypecastExpr cannot cast to this type");
		yield BaseType.UNKNOWN;
	  }
	  };
	  yield tc.type;
	}
	case Assign as->{
	  visit(as.lhs);
	  visit(as.rhs);
	  if(as.lhs.type==BaseType.VOID||as.lhs.type instanceof ArrayType||!as.lhs.type.equals(as.rhs.type)){
		error("Assign invalid types");
		as.type=BaseType.UNKNOWN;
	  }else
		as.type=as.lhs.type;
	  yield as.type;
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
	  if(r.e==null){
		if(frt!=BaseType.VOID)
		  error("Return expected an expression");
	  }else{
		if(!frt.equals(visit(r.e)))
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
