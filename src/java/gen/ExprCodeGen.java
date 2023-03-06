package gen;
import ast.*;
import gen.asm.*;
/**
 * Generates code to evaluate an expression and return the result in a register.
 */
public class ExprCodeGen extends CodeGen{
  public ExprCodeGen(AssemblyProgram asmProg){
	this.asmProg=asmProg;
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
	  if(e.vd.g){//todo? structs, arrays, but i think is ok
		Register va=Register.Virtual.create();
		ts.emit(OpCode.LA,va,e.vd.l);
		if(e.type instanceof PointerType||e.type==BaseType.INT)
		  ts.emit(OpCode.LW,vr(),va,0);
		else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		  ts.emit(OpCode.LB,vr(),va,0);
		else
		  this.rvr=va;
	  }else{
		if(e.type instanceof PointerType||e.type==BaseType.INT)
		  ts.emit(OpCode.LW,vr(),Register.Arch.fp,e.vd.o);
		else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		  ts.emit(OpCode.LB,vr(),Register.Arch.fp,e.vd.o);
		else
		  ts.emit(OpCode.ADDI,vr(),Register.Arch.fp,e.vd.o);
	  }
	}
	case FunCallExpr e->{
	  ts.emit("calling function "+e.f);
	  ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-e.fd.co);
	  for(int i=0;i<e.args.size();i++){
		VarDecl pr=e.fd.params.get(i);
		Register v=visit(e.args.get(i));
		if(pr.type instanceof PointerType||pr.type==BaseType.INT)
		  ts.emit(OpCode.SW,v,Register.Arch.sp,pr.o);
		else if(pr.type==BaseType.CHAR||pr.type==BaseType.VOID)
		  ts.emit(OpCode.SB,v,Register.Arch.sp,pr.o);
		else{
		  Register cp=Register.Virtual.create();//todo? should i make each iter?
		  for(int j=0;j<pr.type.size();j++){
			ts.emit(OpCode.LW,cp,v,j);
			ts.emit(OpCode.SW,cp,Register.Arch.sp,j+pr.o);
		  }
		}
	  }
	  ts.emit(OpCode.SW,Register.Arch.ra,Register.Arch.sp,0);
	  ts.emit(OpCode.JAL,e.fd.in);
	  ts.emit(OpCode.LW,Register.Arch.ra,Register.Arch.sp,0);
	  if(e.type instanceof PointerType||e.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),Register.Arch.sp,4);
	  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),Register.Arch.sp,4);
	  else{//todo -- no clue honestly...
		Register cp=Register.Virtual.create();//todo? should i make each iter?
		for(int i=0;i<e.type.size();i++){
		  ts.emit(OpCode.LW,cp,Register.Arch.sp,i+4);
		  ts.emit(OpCode.SW,cp,Register.Arch.fp,i+e.o);
		}
		ts.emit(OpCode.ADDI,vr(),Register.Arch.fp,e.o);
	  }
	  ts.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,e.fd.co);
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
	  }else{//todo? idk if this is correct
		Register tmp=Register.Virtual.create(),tmp2=Register.Virtual.create();
		ts.emit(OpCode.LI,tmp,e.arr.type.size());
		ts.emit(OpCode.MUL,tmp2,tmp,ind);
		ts.emit(OpCode.ADD,vr(),tmp2,arr);
	  }
	}
	case FieldAccessExpr e->{
	  Register st=visit(e.struct);
	  int o=((StructType)e.struct.type).decl.vst.get(e.field).o;
	  if(e.type instanceof PointerType||e.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),st,o);
	  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),st,o);
	  else//todo? idk if this is correct
		ts.emit(OpCode.ADDI,vr(),st,o);
	}
	case ValueAtExpr e->{
	  Register vl=visit(e.e);
	  if(e.type instanceof PointerType||e.type==BaseType.INT)
		ts.emit(OpCode.LW,vr(),vl,0);
	  else if(e.type==BaseType.CHAR||e.type==BaseType.VOID)
		ts.emit(OpCode.LB,vr(),vl,0);
	  else
		this.rvr=vl;//todo? i think this is fine
	}
	case AddressOfExpr e->{
	  this.rvr=visitAddress(e.e);
	}
	case SizeOfExpr e->
	  ts.emit(OpCode.LI,vr(),e.t.size());
	case TypecastExpr e->
	  this.rvr=visit(e.e);//todo? pretty sure i don't need to do anything
	case Assign e->{
	  Register a2=visitAddress(e.lhs);
	  this.rvr=visit(e.rhs);
	  if(e.type instanceof StructType s){
		Register cp=Register.Virtual.create();//todo? should i make each iter.?
		for(int i=0;i<s.size();i++){
		  ts.emit(OpCode.LW,cp,this.rvr,i);
		  ts.emit(OpCode.SW,cp,a2,i);
		}
 		this.rvr=a2;
	  }else if(e.type instanceof PointerType||e.type==BaseType.INT)
		ts.emit(OpCode.SW,this.rvr,a2,0);
	  else if(e.type==BaseType.CHAR)
		ts.emit(OpCode.SB,this.rvr,a2,0);
	  else
		throw new IllegalStateException("Unreachable assigns type");
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
	case FieldAccessExpr e->{
	  Register st=visitAddress(e.struct);
	  int o=((StructType)e.struct.type).decl.vst.get(e.field).o;
	  ts.emit(OpCode.ADDI,vr(),st,o);
	}
	case ValueAtExpr e->
	  this.rvr=visit(e.e);//todo? i think this is fine
	case default->
	  throw new IllegalStateException("Unreachable not an lval");
	};
	return this.rvr;
  }
}
