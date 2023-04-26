package sem;
import ast.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class NameAnalyzer extends BaseSemanticAnalyzer{
  private Scope scope;
  private Map<String,Scope>css;
  public void visit(ASTNode node){
	switch(node){
	case null->
	  throw new IllegalStateException("Unexpected null value");
	case Program p->{
	  p.decls.addAll(0,Arrays.asList(new FunDecl[]{new FunDecl(BaseType.VOID,"print_s",Arrays.asList(new VarDecl[]{new VarDecl(new PointerType(BaseType.CHAR),"s")}),new Block(new ArrayList<VarDecl>(0),new ArrayList<Stmt>(0)),true),new FunDecl(BaseType.VOID,"print_i",Arrays.asList(new VarDecl[]{new VarDecl(BaseType.INT,"i")}),new Block(new ArrayList<VarDecl>(0),new ArrayList<Stmt>(0)),true),new FunDecl(BaseType.VOID,"print_c",Arrays.asList(new VarDecl[]{new VarDecl(BaseType.CHAR,"c")}),new Block(new ArrayList<VarDecl>(0),new ArrayList<Stmt>(0)),true),new FunDecl(BaseType.CHAR,"read_c",new ArrayList<VarDecl>(0),new Block(new ArrayList<VarDecl>(0),new ArrayList<Stmt>(0)),true),new FunDecl(BaseType.INT,"read_i",new ArrayList<VarDecl>(0),new Block(new ArrayList<VarDecl>(0),new ArrayList<Stmt>(0)),true),new FunDecl(new PointerType(BaseType.VOID),"mcmalloc",Arrays.asList(new VarDecl[]{new VarDecl(BaseType.INT,"size")}),new Block(new ArrayList<VarDecl>(0),new ArrayList<Stmt>(0)),true)}));
	  scope=new Scope();
	  css=new HashMap<String,Scope>();
	  for(Decl d:p.decls)
		visit(d);
	  if(scope.lookupCurrent("main")instanceof FunSymbol f)
		p.main=f.fd;
	}
	case BaseType t->{}
	case PointerType t->{}
	case StructType t->{}
	case ClassType t->{}
	case ArrayType t->{}
	case StructTypeDecl std->{
	  scope=new Scope(scope);
	  for(VarDecl v:std.vs)
		visit(v);
	  scope=scope.outer;
	}
	case ClassDecl cd->{
	  if(css.containsKey(cd.type.name))
		error("Class name already used "+cd.type.name);
	  Scope gs=scope;
	  if(cd.parent!=null&&(scope=css.get(cd.parent.name))==null)
		error("Parent not declared "+cd.parent.name);
	  scope=new Scope(scope);
	  for(VarDecl v:cd.vs){
		v.cdl=true;
		if(scope.lookupCurrent(v.name)!=null)
		  error("VarDecl already used in class "+v.name);
		else if(scope.lookup(v.name)!=null&&gs.lookup(v.name)==null)
		  error("VarDecl used in a parent class "+v.name);
		else
		  scope.put(new VarSymbol(v));
	  }
	  for(FunDecl f:cd.fs){
		f.cdl=true;
		visit(f);
	  }
	  css.put(cd.type.name,scope);
	  scope=gs;
	}
	case VarDecl vd->{
	  if(scope.lookupCurrent(vd.name)!=null)
		error("VarDecl already used "+vd.name);
	  else
		scope.put(new VarSymbol(vd));
	}
	case FunDecl fd->{
	  if(scope.lookupCurrent(fd.name)!=null)
		error("FunDecl already used "+fd.name);
	  else
		scope.put(new FunSymbol(fd));
	  scope=new Scope(scope);
	  for(VarDecl v:fd.params)
		visit(v);
	  if(fd.block==null)
		throw new IllegalStateException("Unexpected null value");
	  for(VarDecl v:fd.block.vds)
		visit(v);
	  for(Stmt s:fd.block.stmts)
		visit(s);
	  scope=scope.outer;
	}
	case IntLiteral i->{}
	case StrLiteral s->{}
	case ChrLiteral c->{}
	case VarExpr v->{
	  switch(scope.lookup(v.name)){
	  case VarSymbol vs->v.vd=vs.vd;
	  case null->error("VarExpr unkown "+v.name);
	  case default->error("VarExpr references function "+v.name);
	  }
	}
	case FunCallExpr fc->{
	  switch(scope.lookup(fc.f)){
	  case FunSymbol fs->fc.fd=fs.fd;
	  case null->error("FunCallExpr unknown "+fc.f);
	  case default->error("FunCallExpr references variable "+fc.f);
	  }
	  for(Expr r:fc.args)
		visit(r);
	}
	case ClassFunCallExpr cfc->{
	  visit(cfc.object);
	  for(Expr r:cfc.call.args)
		visit(r);
	}
	case BinOp bo->{
	  visit(bo.lhs);
	  visit(bo.rhs);
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
	case ClassInstantiationExpr cie->{}
	case Assign as->{
	  visit(as.lhs);
	  visit(as.rhs);
	}
	case ExprStmt es->
	  visit(es.e);
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
	  scope=new Scope(scope);
	  for(VarDecl v:b.vds)
		visit(v);
	  for(Stmt s:b.stmts)
		visit(s);
	  scope=scope.outer;
	}
	};
  }
}
