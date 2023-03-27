package regalloc;
import gen.asm.*;
import gen.asm.Instruction.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
public class Cfg{
  private static final boolean DO_PURGE=true,DO_PRUNE=true,TRY_CANFO=true;
  public final Label func;
  private Map<Label,Cfgnode>dests;
  public List<Cfgnode>nodes;
  private Stack<Label>pds;
  private Map<Label,Stack<Cfgnode>>pbjs;
  private Cfgnode last=null;
  public List<Label>spls;
  public int spoff=0,fpoff=0,splo=0;
  public boolean canfo;
  public Register.Virtual ppreg,poreg;
  private void initCfgnode(Cfgnode cfg){
	if(last!=null)
	  last.succs.add(cfg);
	Instruction i=cfg.last();
	last=cfg;
	Label jd=switch(i){
	case TernaryArithmetic ta->null;
	case BinaryArithmetic ba->null;
	case UnaryArithmetic ua->null;
	case BinaryBranch bb->bb.label;
	case UnaryBranch ub->ub.label;
	case ArithmeticWithImmediate awi->null;
	case Jump j->{
	  if(j.opcode==OpCode.J||j.opcode==OpCode.B)
		last=null;//if not a func call--func call will return to next expr
	  yield j.label;
	}
	case JumpRegister jr->{
	  if(j.opcode==OpCode.JR)//JALR does nothing--oop function call
		last=null;//assume only used to return from function
	  yield null;
	}
	case Load l->null;
	case Store s->null;
	case LoadImmediate li->null;
	case LoadAddress la->null;
	case Nullary nl->null;
	};
	if(jd!=null){
	  Cfgnode dest=dests.get(jd);
	  if(dest==null){
		Stack<Cfgnode>extant=pbjs.get(jd);
		if(extant==null)
		  pbjs.put(jd,extant=new Stack<Cfgnode>());
		extant.push(cfg);
	  }else
		cfg.succs.add(dest);
	}
	while(!pds.isEmpty()){
	  Stack<Cfgnode>extant=pbjs.remove(pds.peek());
	  while(extant!=null&&!extant.isEmpty())
		extant.pop().succs.add(cfg);
	  dests.put(pds.pop(),cfg);
	}
	nodes.add(cfg);
  }
  public Cfg(AssemblyProgram.Section fb){
	dests=new HashMap<Label,Cfgnode>();
    nodes=new ArrayList<Cfgnode>();
	pds=new Stack<Label>();
	pbjs=new HashMap<Label,Stack<Cfgnode>>();
	last=null;
	spls=new ArrayList<Label>();
	List<AssemblyItem>misc=null;
	List<Instruction>ins=new ArrayList<Instruction>();
	List<List<AssemblyItem>>miscs=new ArrayList<List<AssemblyItem>>();
	Label func=null;
	for(AssemblyItem cc:fb.items)
	  switch(cc){
	  case Comment c->{
		if(misc==null)
		  misc=new ArrayList<AssemblyItem>();
		misc.add(c);
	  }
	  case Label l->{
		if(ins.size()>0){
		  initCfgnode(new Cfgnode(ins,miscs));
		  ins=new ArrayList<Instruction>();
		  miscs=new ArrayList<List<AssemblyItem>>();
		}
		if(misc==null)
		  misc=new ArrayList<AssemblyItem>();
		misc.add(l);
		if(func==null)
		  func=l;
		else
		  pds.push(l);
	  }
	  case Directive d->{
		if(misc==null)
		  misc=new ArrayList<AssemblyItem>();
		misc.add(d);
	  }
	  case Instruction i->{
		miscs.add(misc);
		misc=null;
		ins.add(i);
		if(i instanceof ControlFlow&&i.opcode!=OpCode.JAL&&i.opcode!=OpCode.BAL){
		  initCfgnode(new Cfgnode(ins,miscs));
		  ins=new ArrayList<Instruction>();
		  miscs=new ArrayList<List<AssemblyItem>>();
		}
	  }
	  };
	if(ins.size()>0)
	  initCfgnode(new Cfgnode(ins,miscs));
	this.func=func;
	//prune dead code
	if(DO_PRUNE){
	  Stack<Cfgnode>toparse=new Stack<Cfgnode>();
	  toparse.push(nodes.get(0));
	  while(!toparse.isEmpty()){
		Cfgnode n=toparse.pop();
		if(n.reachable)continue;
		n.reachable=true;
		for(Cfgnode p:n.succs)
		  if(!p.reachable)
			toparse.push(p);
	  }
	  List<Cfgnode>nodes=new ArrayList<Cfgnode>();
	  for(Cfgnode n:this.nodes)
		if(n.reachable)
		  nodes.add(n);
	  this.nodes=nodes;
	}
	//liveness analysis
	boolean purged;
	do{
	  purged=false;
	  computeLive();
	  if(DO_PURGE)//remove dead vrs
		for(Cfgnode n:this.nodes)
		  if(n.blockLiveness(true))
			purged=true;
	}while(purged);
	canfo=TRY_CANFO?tryOffset():false;
	if(!canfo){
	  ppreg=Register.Virtual.create();
	  poreg=Register.Virtual.create();
	}
  }
  public void spill(Stack<Register.Virtual>ddsp,Set<Register.Virtual>dnsp,Set<Register.Virtual>unal){
	while(!ddsp.isEmpty()){
	  Register.Virtual tospill=ddsp.pop();
	  Comment cmt=new Comment("spill "+tospill+(canfo?" to stack":" to label"));
	  Label spl=Label.create("spilled_"+tospill);
	  splo-=4;
	  int moff=spoff-fpoff+splo;
	  spls.add(spl);
	  for(Cfgnode n:nodes)
		for(int v=0;v<n.ins.size();v++){
		  Instruction i=n.ins.get(v);
		  Register.Virtual nvr=null;
		  for(Register rr:i.uses())
			if(rr==tospill){
			  Map<Register,Register>torep=new HashMap<Register,Register>();
			  nvr=Register.Virtual.create();
			  dnsp.add(nvr);
			  unal.add(nvr);
			  torep.put(tospill,nvr);
			  n.ins.set(v,i.rebuild(torep));
			  List<AssemblyItem>a=n.miscs.get(v);
			  if(a==null)
				n.miscs.set(v,a=new ArrayList<AssemblyItem>());
			  a.add(cmt);
			  if(!this.canfo){
				n.ins.add(v++,new Instruction.LoadAddress(nvr,spl));
				n.miscs.add(v,null);
				n.ins.add(v++,new Instruction.Load(OpCode.LW,nvr,nvr,0));
			  }else
				n.ins.add(v++,new Instruction.Load(OpCode.LW,nvr,Register.Arch.fp,moff));
			  n.miscs.add(v,null);
			  break;
			}
		  if(i.def()==tospill){
			if(nvr==null){
			  Map<Register,Register>torep=new HashMap<Register,Register>();
			  nvr=Register.Virtual.create();
			  dnsp.add(nvr);
			  unal.add(nvr);
			  torep.put(tospill,nvr);
			  n.ins.set(v,i.rebuild(torep));
			}
			n.miscs.add(++v,List.of(cmt));
			if(!this.canfo){
			  Register.Virtual nvr2=Register.Virtual.create();
			  dnsp.add(nvr2);
			  unal.add(nvr2);
			  n.ins.add(v,new Instruction.LoadAddress(nvr2,spl));
			  n.ins.add(++v,new Instruction.Store(OpCode.SW,nvr,nvr2,0));
			  n.miscs.add(v,null);
			}else
			  n.ins.add(v,new Instruction.Store(OpCode.SW,nvr,Register.Arch.fp,moff));
		  }
		}
	}
	computeLive();
  }
  private boolean tryOffset(){
	boolean knowo=true,initfp=false;
	Cfgnode n=nodes.get(0);
	for(Instruction i:n.ins){
	  if(i.opcode==OpCode.PUSH_REGISTERS)
		return knowo&&initfp;
	  //best effort attempt to find offset we need from frame pointer
	  if(i.def()==Register.Arch.sp){
		if(i instanceof Instruction.ArithmeticWithImmediate awi&&awi.src==Register.Arch.sp)
		  if(awi.opcode==OpCode.ADDI&&awi.imm>-32769&&awi.imm<32768)
			spoff+=awi.imm;
		  else if(awi.opcode==OpCode.ADDIU&&awi.imm>=0&&awi.imm<65536)
			spoff+=awi.imm;
		  else
			knowo=false;
		else
		  knowo=false;
	  }else if(i.def()==Register.Arch.fp){
		if(i instanceof Instruction.ArithmeticWithImmediate awi){
		  if(awi.src==Register.Arch.fp){
			if(initfp)
			  if(awi.opcode==OpCode.ADDI&&awi.imm>-32769&&awi.imm<32768)
				fpoff+=awi.imm;
			  else if(awi.opcode==OpCode.ADDIU&&awi.imm>=0&&awi.imm<65536)
				fpoff+=awi.imm;
			  else
				initfp=false;
		  }else if(awi.src==Register.Arch.sp){
			if(awi.opcode==OpCode.ADDI&&awi.imm>-32769&&awi.imm<32768){
			  fpoff=spoff+awi.imm;
			  initfp=true;
			}else if(awi.opcode==OpCode.ADDIU&&awi.imm>=0&&awi.imm<65536){
			  fpoff=spoff+awi.imm;
			  initfp=true;
			}else
			  initfp=false;
		  }
		}else if(i instanceof Instruction.TernaryArithmetic ta){
		  Register a=ta.src1,b=ta.src2;
		  if(a==Register.Arch.zero){
			a=ta.src2;
			b=ta.src1;
		  }
		  if(a==Register.Arch.sp&&b==Register.Arch.zero){
			fpoff=spoff;
			initfp=true;
		  }else if(a!=Register.Arch.fp||b!=Register.Arch.zero||!initfp)
			initfp=false;
		}
	  }
	}
	return false;
  }
  public void computeLive(){
	for(Cfgnode n:this.nodes)
	  n.blockLiveness(false);
	boolean changed;
	do{
	  changed=false;
	  for(int i=this.nodes.size();i-->0;){
		Cfgnode n=this.nodes.get(i);
		for(Cfgnode s:n.succs)
		  for(Register.Virtual vr:s.livein)
			if(n.liveout.add(vr))
			  changed=true;
		for(Register.Virtual vr:n.liveout)
		  if(!n.defs.contains(vr))
			if(n.livein.add(vr))
			  changed=true;
	  }
	}while(changed);
  }
}
