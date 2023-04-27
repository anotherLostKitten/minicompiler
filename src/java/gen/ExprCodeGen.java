package gen;
import ast.*;
import gen.asm.*;
/**
 * Generates code to evaluate an expression and return the result in a register.
 */
public class ExprCodeGen extends CodeGen{
  private final FunDecl encapsf;
  public ExprCodeGen(AssemblyProgram asmProg,FunDecl encapsf){
	this.asmProg=asmProg;
	this.encapsf=encapsf;
  }
  private Register rvr;
  private Register vr(){
	return this.rvr=Register.Virtual.create();
  }
  public Register visit(Expr exp){
	AssemblyProgram.Section ts=asmProg.getCurrentSection();
	if(exp instanceof FunCallExpr e&&e.fd.sc){
	  switch(e.f){
	  case"print_s"->{
		ts.emit(OpCode.ADD,Register.Arch.a0,visit(e.args.get(0)),Register.Arch.zero);
		ts.emit(OpCode.LI,Register.Arch.v0,4);
	  }
	  case"print_i"->{
		ts.emit(OpCode.ADD,Register.Arch.a0,visit(e.args.get(0)),Register.Arch.zero);
		ts.emit(OpCode.LI,Register.Arch.v0,1);
	  }
	  case"print_c"->{
		ts.emit(OpCode.ADD,Register.Arch.a0,visit(e.args.get(0)),Register.Arch.zero);
		ts.emit(OpCode.LI,Register.Arch.v0,11);
	  }
	  case"read_c"->{
		ts.emit(OpCode.LI,Register.Arch.v0,12);
	  }
	  case"read_i"->{
		ts.emit(OpCode.LI,Register.Arch.v0,5);
	  }
	  case"mcmalloc"->{
		ts.emit(OpCode.ADD,Register.Arch.a0,visit(e.args.get(0)),Register.Arch.zero);
		ts.emit(OpCode.LI,Register.Arch.v0,9);
	  }
	  case default->
		throw new IllegalStateException("Unreachable not a stdlib function");
	  };
	  ts.emit(OpCode.SYSCALL);
	  return Register.Arch.v0;
	}
    switch(exp){
	case IntLiteral e->
	  ts.emit(OpCode.LI,vr(),e.v);
	case StrLiteral e->
	  ts.emit(OpCode.LA,vr(),e.l);
	case ChrLiteral e->
	  ts.emit(OpCode.LI,vr(),(int)e.v);
	case VarExpr e->{
	  if(e.vd.g||e.vd.cdl){
		Register va=Register.Virtual.create();
		if(e.vd.cdl){
		  ts.emit(OpCode.LW,va,Register.Arch.fp,encapsf.rvo);
		  ts.emit(OpCode.ADDI,va,va,e.vd.o);
		}else
		  ts.emit(OpCode.LA,va,e.vd.l);
		if(e.type instanceof PointerType||e.type instanceof ClassType||e.type==BaseType.INT)
		  ts.emit(OpCode.LW,vr(),va,0);
		else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		  ts.emit(OpCode.LB,vr(),va,0);
		else
		  this.rvr=va;
	  }else{
		if(!e.vd.r){
		  if(e.type instanceof PointerType||e.type instanceof ClassType||e.type==BaseType.INT)
			ts.emit(OpCode.LW,vr(),Register.Arch.fp,e.vd.o);
		  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
			ts.emit(OpCode.LB,vr(),Register.Arch.fp,e.vd.o);
		  else
			ts.emit(OpCode.ADDI,vr(),Register.Arch.fp,e.vd.o);
		}else
		  this.rvr=e.vd.vr;
	  }
	}
	case FunCallExpr e->{//todo implicit class functions(?)
	  ts.emit("calling function "+e.f);
	  for(int i=e.args.size()-1;i>=0;i--){//idk i guess i have to do rtl
		VarDecl pr=e.fd.params.get(i);
		ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-pr.s);
		Register v=visit(e.args.get(i));
		if(pr.type instanceof PointerType||e.type instanceof ClassType||pr.type==BaseType.INT)
		  ts.emit(OpCode.SW,v,Register.Arch.sp,0);
		else if(pr.type==BaseType.CHAR||pr.type==BaseType.VOID)
		  ts.emit(OpCode.SB,v,Register.Arch.sp,0);
		else{
		  Register cp=Register.Virtual.create();
		  for(int j=0;j<pr.type.size();j+=4){
			ts.emit(OpCode.LW,cp,v,j);
			ts.emit(OpCode.SW,cp,Register.Arch.sp,j);
		  }
		}
	  }
	  if(e.fd.cdl){
		Register cfo=Register.Virtual.create();
		ts.emit(OpCode.LW,cfo,Register.Arch.fp,encapsf.rvo);//todo?? what is this offset?
		ts.emit(OpCode.SW,cfo,Register.Arch.sp,-4);//save class object
		ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-e.fd.rvo-4);
		ts.emit(OpCode.SW,Register.Arch.ra,Register.Arch.sp,0);
		Register q=Register.Virtual.create();
		ts.emit(OpCode.LW,q,cfo,0);//get base of vtbl from class address, then get func off
		ts.emit(OpCode.LW,q,q,encapsf.pcpc.vt.get(e.f).vto);
		ts.emit(OpCode.JALR,q);
	  }else{
		ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-e.fd.rvo);
		ts.emit(OpCode.SW,Register.Arch.ra,Register.Arch.sp,0);
		ts.emit(OpCode.JAL,e.fd.in);
	  }
	  ts.emit(OpCode.LW,Register.Arch.ra,Register.Arch.sp,0);
	  if(e.type instanceof PointerType||e.type instanceof ClassType||e.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),Register.Arch.sp,4);
	  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),Register.Arch.sp,4);
	  else{
		Register cp=Register.Virtual.create();
		for(int i=0;i<e.type.size();i+=4){
		  ts.emit(OpCode.LW,cp,Register.Arch.sp,i+4);
		  ts.emit(OpCode.SW,cp,Register.Arch.fp,i+e.o);
		}
		ts.emit(OpCode.ADDI,vr(),Register.Arch.fp,e.o);
	  }
	  ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,e.fd.co);
	}
	case ClassFunCallExpr cfc->{
	  ts.emit("calling class function "+cfc.call.f);
	  Register cfo=visit(cfc.object);
	  for(int i=cfc.call.args.size()-1;i>=0;i--){//idk i guess i have to do rtl
		VarDecl pr=cfc.call.fd.params.get(i);
		ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-pr.s);
		Register v=visit(cfc.call.args.get(i));
		if(pr.type instanceof PointerType||cfc.call.type instanceof ClassType||pr.type==BaseType.INT)
		  ts.emit(OpCode.SW,v,Register.Arch.sp,0);
		else if(pr.type==BaseType.CHAR||pr.type==BaseType.VOID)
		  ts.emit(OpCode.SB,v,Register.Arch.sp,0);
		else{
		  Register cp=Register.Virtual.create();
		  for(int j=0;j<pr.type.size();j+=4){
			ts.emit(OpCode.LW,cp,v,j);
			ts.emit(OpCode.SW,cp,Register.Arch.sp,j);
		  }
		}
	  }
	  ts.emit(OpCode.SW,cfo,Register.Arch.sp,-4);//save class object
	  ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-cfc.call.fd.rvo-4);
	  ts.emit(OpCode.SW,Register.Arch.ra,Register.Arch.sp,0);
	  Register q=Register.Virtual.create();
	  ts.emit(OpCode.LW,q,cfo,0);//get base of vtbl from class address, then get func off
	  ts.emit(OpCode.LW,q,q,((ClassType)cfc.object.type).decl.vt.get(cfc.call.f).vto);
	  ts.emit(OpCode.JALR,q);
	  ts.emit(OpCode.LW,Register.Arch.ra,Register.Arch.sp,0);
	  if(cfc.call.type instanceof PointerType||cfc.call.type instanceof ClassType||cfc.call.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),Register.Arch.sp,4);
	  else if(cfc.call.type==BaseType.CHAR||cfc.call.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),Register.Arch.sp,4);
	  else{
		Register cp=Register.Virtual.create();
		for(int i=0;i<cfc.call.type.size();i+=4){
		  ts.emit(OpCode.LW,cp,Register.Arch.sp,i+4);
		  ts.emit(OpCode.SW,cp,Register.Arch.fp,i+cfc.call.o);
		}
		ts.emit(OpCode.ADDI,vr(),Register.Arch.fp,cfc.call.o);
	  }
	  ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,cfc.call.fd.co);
	}
	case BinOp e->{
	  Register l=visit(e.lhs),r;
	  switch(e.op){
	  case ADD->{
		r=visit(e.rhs);
		ts.emit(OpCode.ADD,vr(),l,r);
	  }
	  case SUB->{
		r=visit(e.rhs);
		ts.emit(OpCode.SUB,vr(),l,r);
	  }
	  case MUL->{
		r=visit(e.rhs);
		ts.emit(OpCode.MUL,vr(),l,r);
	  }
	  case DIV->{
		r=visit(e.rhs);
		ts.emit(OpCode.DIV,l,r);
		ts.emit(OpCode.MFLO,vr());
	  }
	  case MOD->{
		r=visit(e.rhs);
		ts.emit(OpCode.DIV,l,r);
		ts.emit(OpCode.MFHI,vr());
	  }
	  case GT->{
		r=visit(e.rhs);
		ts.emit(OpCode.SLT,vr(),r,l);
	  }
	  case LT->{
		r=visit(e.rhs);
		ts.emit(OpCode.SLT,vr(),l,r);
	  }
	  case GE->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.SLT,tmp,l,r);
		ts.emit(OpCode.XORI,vr(),tmp,1);
	  }
	  case LE->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.SLT,tmp,r,l);
		ts.emit(OpCode.XORI,vr(),tmp,1);
	  }
	  case NE->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.XOR,tmp,l,r);
		ts.emit(OpCode.SLTU,vr(),Register.Arch.zero,tmp);
	  }
	  case EQ->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.XOR,tmp,l,r);
		ts.emit(OpCode.SLTIU,vr(),tmp,1);
	  }
	  case OR->{
		Label t=Label.create("binop_or_true"),n=Label.create("binop_or_end");
		ts.emit(OpCode.BNEZ,l,t);
		r=visit(e.rhs);
		ts.emit(OpCode.BNEZ,r,t);
		ts.emit(OpCode.LI,vr(),0);
		ts.emit(OpCode.B,n);
		ts.emit(t);
		ts.emit(OpCode.LI,this.rvr,1);
		ts.emit(n);
	  }
	  case AND->{
		Label f=Label.create("binop_and_false"),n=Label.create("binop_and_end");
		ts.emit(OpCode.BEQZ,l,f);
		r=visit(e.rhs);
		ts.emit(OpCode.BEQZ,r,f);
		ts.emit(OpCode.LI,vr(),1);
		ts.emit(OpCode.B,n);
		ts.emit(f);
		ts.emit(OpCode.LI,this.rvr,0);
		ts.emit(n);
	  }
	  };
	}
	case ArrayAccessExpr e->{
	  Register arr=visit(e.arr),ind=visit(e.ind);
	  if(e.type instanceof PointerType||e.type==BaseType.INT){
		Register tmp=Register.Virtual.create(),tmp2=Register.Virtual.create();
		ts.emit(OpCode.SLL,tmp2,ind,2);
		ts.emit(OpCode.ADD,tmp,tmp2,arr);
		ts.emit(OpCode.LW,vr(),tmp,0);
	  }else if(e.type==BaseType.CHAR||e.type==BaseType.VOID){
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.ADD,tmp,ind,arr);
		ts.emit(OpCode.LB,vr(),tmp,0);
	  }else{
		Register tmp=Register.Virtual.create(),tmp2=Register.Virtual.create();
		ts.emit(OpCode.LI,tmp,e.arr.type.size());
		ts.emit(OpCode.MUL,tmp2,tmp,ind);
		ts.emit(OpCode.ADD,vr(),tmp2,arr);
	  }
	}
	case FieldAccessExpr e->{//todo? access class fields?
	  Register st=visit(e.struct);
	  int o;
	  if(e.struct.type instanceof StructType ss)
		o=ss.decl.vst.get(e.field).o;
	  else if(e.struct.type instanceof ClassType cs)
		o=cs.decl.vst.get(e.field).o;
	  else
		throw new IllegalStateException("FieldAccessExpr fetch not struct or class");
	  if(e.type instanceof PointerType||e.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),st,o);
	  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),st,o);
	  else
		ts.emit(OpCode.ADDI,vr(),st,o);
	}
	case ValueAtExpr e->{
	  Register vl=visit(e.e);
	  if(e.type instanceof PointerType||e.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),vl,0);
	  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),vl,0);
	  else
		this.rvr=vl;
	}
	case AddressOfExpr e->{
	  this.rvr=visitAddress(e.e);
	}
	case SizeOfExpr e->
	  ts.emit(OpCode.LI,vr(),e.t.size());
	case TypecastExpr e->
	  this.rvr=visit(e.e);
	case ClassInstantiationExpr cie->{//todo? check this?
	  ts.emit(OpCode.ADDI,Register.Arch.a0,Register.Arch.zero,cie.t.decl.size);
	  ts.emit(OpCode.LI,Register.Arch.v0,9);
	  ts.emit(OpCode.SYSCALL);
	  ts.emit(OpCode.LA,Register.Arch.a0,cie.t.decl.vtl);
	  ts.emit(OpCode.SW,Register.Arch.a0,Register.Arch.v0,0);
	  this.rvr=Register.Arch.v0;
	}
	case Assign e->{//todo? assigning class?
	  if(e.lhs instanceof VarExpr ve&&ve.vd.r){
		Register a2=visit(e.rhs);
		this.rvr=ve.vd.vr;
		if(e.type instanceof PointerType||e.type instanceof ClassType||e.type==BaseType.INT)
		  ts.emit(OpCode.ADD,this.rvr,a2,Register.Arch.zero);
		else if(e.type==BaseType.CHAR)
		  ts.emit(OpCode.ANDI,this.rvr,a2,255);
		else
		  throw new IllegalStateException("Unreachable assigns type");
	  }else{
		Register a2=visitAddress(e.lhs);
		this.rvr=visit(e.rhs);
		if(e.type instanceof StructType s){
		  Register cp=Register.Virtual.create();
		  for(int i=0;i<s.size();i+=4){
			ts.emit(OpCode.LW,cp,this.rvr,i);
			ts.emit(OpCode.SW,cp,a2,i);
		  }
		  this.rvr=a2;
		}else if(e.type instanceof PointerType||e.type instanceof ClassType||e.type==BaseType.INT)
		  ts.emit(OpCode.SW,this.rvr,a2,0);
		else if(e.type==BaseType.CHAR)
		  ts.emit(OpCode.SB,this.rvr,a2,0);
		else
		  throw new IllegalStateException("Unreachable assigns type");
	  }
	}
	};
	return this.rvr;
  }
  public Register visitAddress(Expr exp){
	AssemblyProgram.Section ts=asmProg.getCurrentSection();
	switch(exp){
	case VarExpr e->{
	  if(e.vd.g)
		ts.emit(OpCode.LA,vr(),e.vd.l);
	  else
		ts.emit(OpCode.ADDI,vr(),Register.Arch.fp,e.vd.o);
	}
	case ArrayAccessExpr e->{
	  Register arr=visit(e.arr),ind=visit(e.ind);
	  if(e.type instanceof PointerType||e.type==BaseType.INT){
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.SLL,tmp,ind,2);
		ts.emit(OpCode.ADD,vr(),tmp,arr);
	  }else if(e.type==BaseType.CHAR||e.type==BaseType.VOID){
		ts.emit(OpCode.ADD,vr(),ind,arr);
	  }else{
		Register tmp=Register.Virtual.create(),tmp2=Register.Virtual.create();
		ts.emit(OpCode.LI,tmp,e.arr.type.size());
		ts.emit(OpCode.MUL,tmp2,tmp,ind);
		ts.emit(OpCode.ADD,vr(),tmp2,arr);
	  }
	}
	case FieldAccessExpr e->{//todo? field access class?
	  if(e.struct.type instanceof StructType ss){
		Register st=visitAddress(e.struct);
		ts.emit(OpCode.ADDI,vr(),st,ss.decl.vst.get(e.field).o);
	  }else if(e.struct.type instanceof ClassType cs){
		Register st=visit(e.struct);
		ts.emit(OpCode.ADDI,vr(),st,cs.decl.vst.get(e.field).o);
	  }else
		throw new IllegalStateException("Unreachable field access not struct nor class");
	}
	case ValueAtExpr e->
	  this.rvr=visit(e.e);
	case default->
	  throw new IllegalStateException("Unreachable not an lval");
	};
	return this.rvr;
  }
}
