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
	Register vr=Register.Virtual.create();
    switch(exp){
	case IntLiteral e->
	  ts.emit(OpCode.LI,vr,e.v);
	case StrLiteral e->
	  ts.emit(OpCode.LA,vr,e.l);
	case ChrLiteral e->
	  ts.emit(OpCode.LI,vr,(int)e.v);
	case VarExpr e->{
	  if(e.vd.g){//todo structs
		Register va=Register.Virtual.create();
		ts.emit(OpCode.LA,va,e.vd.l);
		ts.emit(OpCode.LW,vr,va,0);
	  }else
		ts.emit(OpCode.LW,vr,Register.Arch.fp,e.vd.o);
	}
	case FunCallExpr e->{
	  //todo lmao
	}
	case BinOp e->{
	  Register l=visit(e.lhs),r;
	  switch(e.op){
	  case ADD->{
		r=visit(e.rhs);
		ts.emit(OpCode.ADD,vr,l,r);
	  }
	  case SUB->{
		r=visit(e.rhs);
		ts.emit(OpCode.SUB,vr,l,r);
	  }
	  case MUL->{
		r=visit(e.rhs);
		ts.emit(OpCode.MUL,vr,l,r);
	  }
	  case DIV->{
		r=visit(e.rhs);
		ts.emit(OpCode.DIV,l,r);
		ts.emit(OpCode.MFLO,vr);
	  }
	  case MOD->{
		r=visit(e.rhs);
		ts.emit(OpCode.DIV,l,r);
		ts.emit(OpCode.MFHI,vr);
	  }
	  case GT->{
		r=visit(e.rhs);
		ts.emit(OpCode.SLT,vr,r,l);
	  }
	  case LT->{
		r=visit(e.rhs);
		ts.emit(OpCode.SLT,vr,l,r);
	  }
	  case GE->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.SLT,tmp,l,r);
		ts.emit(OpCode.XORI,vr,tmp,1);
	  }
	  case LE->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.SLT,tmp,r,l);
		ts.emit(OpCode.XORI,vr,tmp,1);
	  }
	  case NE->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.XOR,tmp,l,r);
		ts.emit(OpCode.SLTU,vr,Register.Arch.zero,tmp);
	  }
	  case EQ->{
		r=visit(e.rhs);
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.XOR,tmp,l,r);
		ts.emit(OpCode.SLTIU,vr,tmp,1);
	  }
	  case OR->{
		Label t=Label.create("binop_or_true"),n=Label.create("binop_or_end");
		ts.emit(OpCode.BNEZ,l,t);
		r=visit(e.rhs);
		ts.emit(OpCode.BNEZ,r,t);
		ts.emit(OpCode.LI,vr,0);
		ts.emit(OpCode.B,n);
		ts.emit(t);
		ts.emit(OpCode.LI,vr,1);
		ts.emit(n);
	  }
	  case AND->{
		Label f=Label.create("binop_and_false"),n=Label.create("binop_and_end");
		ts.emit(OpCode.BEQZ,l,f);
		r=visit(e.rhs);
		ts.emit(OpCode.BEQZ,r,f);
		ts.emit(OpCode.LI,vr,1);
		ts.emit(OpCode.B,n);
		ts.emit(f);
		ts.emit(OpCode.LI,vr,0);
		ts.emit(n);
	  }
	  };
	}
	case ArrayAccessExpr e->{
	  Register arr=visit(e.arr),ind=visit(e.ind);
	  Type rt=switch(e.arr.type){
	  case PointerType t->t.type;
	  case ArrayType t->t.type;
	  case default->null;
	  };
	  if(rt instanceof PointerType||rt==BaseType.INT){
		Register tmp=Register.Virtual.create(),tmp2=Register.Virtual.create();
		ts.emit(OpCode.SLL,tmp2,ind,2);
		ts.emit(OpCode.ADD,tmp,tmp2,arr);
		ts.emit(OpCode.LW,vr,tmp,0);
	  }else if(rt==BaseType.CHAR||rt==BaseType.VOID){
		Register tmp=Register.Virtual.create();
		ts.emit(OpCode.ADD,tmp,ind,arr);
		ts.emit(OpCode.LB,vr,tmp,0);
	  }else{//todo idk if this is correct?
		Register tmp=Register.Virtual.create(),tmp2=Register.Virtual.create();
		ts.emit(OpCode.LI,tmp,e.arr.type.size());
		ts.emit(OpCode.MUL,tmp2,tmp,ind);
		ts.emit(OpCode.ADD,vr,tmp2,arr);
	  }
	}
	//todo &c.
	case FieldAccessExpr e->{}
	case ValueAtExpr e->{}
	case AddressOfExpr e->{}
	case SizeOfExpr e->{}
	case TypecastExpr e->{}
	case Assign e->{}
	};
	return vr;
  }
}
