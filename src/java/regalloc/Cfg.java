package regalloc;
import gen.asm.*;
import gen.asm.Instruction.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
public class Cfg{
  public final Label func;
  private Map<Label,Cfgnode>dests;
  public List<Cfgnode>nodes;
  private Stack<Label>pds;
  private Map<Label,Stack<Cfgnode>>pbjs;
  private Cfgnode last;
  public List<AssemblyItem>misc;
  public Cfg(AssemblyProgram.Section fb){
	dests=new HashMap<Label,Cfgnode>();
	nodes=new ArrayList<Cfgnode>();
	pds=new Stack<Label>();
	pbjs=new HashMap<Label,Stack<Cfgnode>>();
	last=null;
	misc=null;
	Label func=null;
	for(AssemblyItem cc:fb.items){
	  switch(cc){
	  case Comment c->{
		if(misc==null)
		  misc=new ArrayList<AssemblyItem>();
		misc.add(c);
	  }
	  case Label l->{
		if(func==null)
		  func=l;
		else
		  pds.push(l);
		if(misc==null)
		  misc=new ArrayList<AssemblyItem>();
		misc.add(l);
	  }
	  case Directive d->{
		if(misc==null)
		  misc=new ArrayList<AssemblyItem>();
		misc.add(d);
	  }
	  case Instruction i->{
		Cfgnode cfg=new Cfgnode(i,last,misc);
		while(!pds.isEmpty()){
		  Stack<Cfgnode>extant=pbjs.remove(pds.peek());
		  while(extant!=null&&!extant.isEmpty())
			cfg.preds.add(extant.pop());
		  dests.put(pds.pop(),cfg);
		}
		last=cfg;
		Label jd=switch(i){
		case TernaryArithmetic ta->null;
		case BinaryArithmetic ba->null;
		case UnaryArithmetic ua->null;
		case BinaryBranch bb->bb.label;
		case UnaryBranch ub->ub.label;
		case ArithmeticWithImmediate awi->null;
		case Jump j->j.label;
		case JumpRegister jr->{
		  last=null;//assume only return from function
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
			dest.preds.add(cfg);
		}
		nodes.add(cfg);
		misc=null;
	  }
	  };
	}
	this.func=func;
  }
}
