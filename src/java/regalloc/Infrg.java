package regalloc;
import gen.asm.*;;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
public class Infrg{
  public static final Register.Arch[]regs={Register.Arch.t0,Register.Arch.t1,Register.Arch.t2,Register.Arch.t3,Register.Arch.t4,Register.Arch.t5,Register.Arch.t6,Register.Arch.t7,Register.Arch.t8,Register.Arch.t9,Register.Arch.s0,Register.Arch.s1,Register.Arch.s2,Register.Arch.s3,Register.Arch.s4,Register.Arch.s5,Register.Arch.s6,Register.Arch.s7};//-1: $zero
  public Map<Register.Virtual,Integer>allocs;
  public Map<Register.Virtual,Set<Register.Virtual>>edges;
  public final Label func;
  public int numRegs=0,numSpills=0;
  private Cfg cfg;
  private Set<Register.Virtual>unal,dnsp;
  public Infrg(Cfg cfg){
	this.cfg=cfg;
	this.func=cfg.func;
  }
  private void computeEdges(){
	edges=new HashMap<Register.Virtual,Set<Register.Virtual>>();
	unal=new HashSet<Register.Virtual>();
	for(Cfgnode n:cfg.nodes){
	  HashSet<Register.Virtual>lives=new HashSet<Register.Virtual>();
	  for(Register.Virtual vr:n.liveout)
		lives.add(vr);
	  for(int j=n.ins.size();j-->0;){
		for(Register.Virtual vr:lives){
		  Set<Register.Virtual>a=edges.get(vr);
		  if(a==null)
			edges.put(vr,a=new HashSet<Register.Virtual>());
		  for(Register.Virtual ur:lives){
			if(vr==ur)
			  continue;
			a.add(ur);
		  }
		}
		Instruction i=n.ins.get(j);
		if(!cfg.canfo&&numSpills>0&&i.opcode==OpCode.PUSH_REGISTERS){
		  Set<Register.Virtual>a=edges.get(cfg.ppreg);
		  if(a==null)
			edges.put(cfg.ppreg,a=new HashSet<Register.Virtual>());
		  for(Register.Virtual vr:lives){
			a.add(vr);
			edges.get(vr).add(cfg.ppreg);
		  }
		  unal.add(cfg.ppreg);
		  dnsp.add(cfg.ppreg);
		}else if(!cfg.canfo&&numSpills>0&&i.opcode==OpCode.POP_REGISTERS){
		  Set<Register.Virtual>a=edges.get(cfg.ppreg),b=edges.get(cfg.poreg);
		  if(a==null)
			edges.put(cfg.ppreg,a=new HashSet<Register.Virtual>());
		  if(b==null)
			edges.put(cfg.poreg,b=new HashSet<Register.Virtual>());
		  a.add(cfg.poreg);
		  b.add(cfg.ppreg);
		  for(Register.Virtual vr:lives){
			a.add(vr);
			b.add(vr);
			edges.get(vr).add(cfg.ppreg);
			edges.get(vr).add(cfg.poreg);
		  }
		  unal.add(cfg.ppreg);
		  dnsp.add(cfg.ppreg);
		  unal.add(cfg.poreg);
		  dnsp.add(cfg.poreg);
		}else{
		  if(i.def()instanceof Register.Virtual vr){
			lives.remove(vr);
			unal.add(vr);
		  }
		  for(Register r:i.uses())
			if(r instanceof Register.Virtual vr){
			  lives.add(vr);
			  unal.add(vr);
			}
		}
	  }
	}
	allocs=new HashMap<Register.Virtual,Integer>();
	Iterator<Register.Virtual>nohav=unal.iterator();
	while(nohav.hasNext()){
	  Register.Virtual vr=nohav.next();
	  if(!edges.containsKey(vr)){
		nohav.remove();
		allocs.put(vr,-1);//in case we save to a dead register, just use $zero
	  }
	}
  }
  public void allocate(){
	dnsp=new HashSet<Register.Virtual>();
	Register.Virtual nvr;
	Stack<Register.Virtual>ddsp=null;
	do{
	  computeEdges();
	  ddsp=tryAllocate();
	  if(ddsp!=null)
		cfg.spill(ddsp,dnsp,unal);
	}while(ddsp!=null);
	dnsp=null;
  }
  private Stack<Register.Virtual>tryAllocate(){
	Stack<Register.Virtual>toalloc=new Stack<Register.Virtual>(),spills=new Stack<Register.Virtual>();
	Map<Register.Virtual,Integer>nbrs=new HashMap<Register.Virtual,Integer>();
	for(Register.Virtual vr:edges.keySet())
	  nbrs.put(vr,edges.get(vr).size());
	while(!unal.isEmpty()){
	  Register.Virtual next=null;
	  for(Register.Virtual vr:edges.keySet()){
		if(unal.contains(vr)&&nbrs.get(vr)<regs.length){
		  next=vr;
		  break;
		}
	  }
	  if(next==null){
		int maxnb=0;//todo better heuristic
		for(Register.Virtual vr:edges.keySet())
		  if(!dnsp.contains(vr)&&nbrs.get(vr)>maxnb&&unal.contains(vr)){
			maxnb=nbrs.get(vr);
			next=vr;
		  }
		System.out.println("spilled "+next.toString());
		numSpills++;
		spills.push(next);
	  }else
		toalloc.push(next);
	  unal.remove(next);
	  for(Register.Virtual nb:edges.get(next))
		nbrs.put(nb,nbrs.get(nb)-1);
	}
	if(!spills.isEmpty()){
	  System.out.println("recomputing liveness");
	  return spills;
	}
	while(!toalloc.isEmpty()){
	  Register.Virtual vr=toalloc.pop();
	  boolean[]conts=new boolean[regs.length];
	  for(Register.Virtual nb:edges.get(vr)){
		  Integer v=allocs.get(nb);
		  if(v!=null&&v>=0)
			conts[v]=true;
	  }
	  int i=0;
	  for(;conts[i];i++);
	  allocs.put(vr,i);
	  if(i+1>numRegs)
		numRegs=i+1;
	}
	return null;
  }
}
