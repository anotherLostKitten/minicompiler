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
  private static final boolean DO_PURGE=true;
  public final Label func;
  private Map<Label,Cfgnode>dests;
  public List<Cfgnode>nodes;
  private Stack<Label>pds;
  private Map<Label,Stack<Cfgnode>>pbjs;
  private Cfgnode last=null;
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
	  last=null;//assume only used to return from function
	  yield null;
	}
	case Load l->null;
	case Store s->null;
	case LoadImmediate li->null;
	case LoadAddress la->null;
	case Nullary nl->null;//todo? anything special for push/pop registers?
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
	List<AssemblyItem>misc=null;
	List<Instruction>ins=new ArrayList<Instruction>();
	Map<Instruction,List<AssemblyItem>>miscs=new HashMap<Instruction,List<AssemblyItem>>();
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
		  initCfgnode(new Cfgnode(ins,miscs,misc));
		  misc=new ArrayList<AssemblyItem>();
		  ins=new ArrayList<Instruction>();
		  miscs=new HashMap<Instruction,List<AssemblyItem>>();
		}else if(misc==null)
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
		miscs.put(i,misc);
		misc=null;
		ins.add(i);
		if(i instanceof ControlFlow&&i.opcode!=OpCode.JAL&&i.opcode!=OpCode.BAL){
		  initCfgnode(new Cfgnode(ins,miscs,misc));
		  ins=new ArrayList<Instruction>();
		  miscs=new HashMap<Instruction,List<AssemblyItem>>();
		}
	  }
	  };
	if(ins.size()>0)
	  initCfgnode(new Cfgnode(ins,miscs,misc));
	this.func=func;
	//prune dead code
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
	//liveness analysis
	boolean purged,changed;
	do{
	  purged=false;
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
	  //remove dead vrs
	  if(DO_PURGE){
		for(Cfgnode n:this.nodes)
		  if(n.blockLiveness(true))
			purged=true;
		if(purged)
		  for(Cfgnode n:this.nodes){
			n.liveout=new HashSet<Register.Virtual>();
			n.blockLiveness(false);
		  }
	  }
	}while(purged);
  }
}
