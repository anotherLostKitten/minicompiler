package regalloc;
import gen.asm.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
public class Cfgnode{
  public final List<Instruction>ins;
  public Set<Register.Virtual>livein,liveout,defs;
  public List<Cfgnode>succs;
  public final Map<Instruction,List<AssemblyItem>>miscs;
  public final List<AssemblyItem>lastmisc;
  public boolean reachable;
  public Cfgnode(List<Instruction>ins,Map<Instruction,List<AssemblyItem>>miscs,List<AssemblyItem>lastmisc){
	this.ins=ins;
	this.miscs=miscs;
	this.lastmisc=lastmisc;
	this.reachable=false;
	this.succs=new ArrayList<Cfgnode>();
	this.livein=new HashSet<Register.Virtual>();
	this.liveout=new HashSet<Register.Virtual>();
	this.defs=new HashSet<Register.Virtual>();
	for(int j=ins.size();j-->0;){
	  Instruction i=ins.get(j);
	  if(i.def()instanceof Register.Virtual vr){
		defs.add(vr);
		livein.remove(vr);
	  }
	  for(Register r:i.uses())
		if(r instanceof Register.Virtual vr){
		  defs.remove(vr);
		  livein.add(vr);
		}
	}
  }
  public Instruction last(){
	return ins.get(ins.size()-1);
  }
}
