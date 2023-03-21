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
  final Map<Instruction,List<AssemblyItem>>miscs;
  final List<AssemblyItem>lastmisc;
  public boolean reachable;
  public Cfgnode(List<Instruction>ins,Map<Instruction,List<AssemblyItem>>miscs,List<AssemblyItem>lastmisc){
	this.ins=ins;
	this.miscs=miscs;
	this.lastmisc=lastmisc;
	this.reachable=false;
	this.succs=new ArrayList<Cfgnode>();
  }
  public Instruction last(){
	return ins.get(ins.size()-1);
  }
  public boolean blockLiveness(boolean purge){
	boolean changed=false;
	if(!purge)
	  this.liveout=new HashSet<Register.Virtual>();
	this.livein=new HashSet<Register.Virtual>();
	this.defs=new HashSet<Register.Virtual>();
	for(Register.Virtual rv:liveout)
	  livein.add(rv);
  	for(int j=ins.size();j-->0;){
	  Instruction i=ins.get(j);
	  if(i.def()instanceof Register.Virtual vr){
		if(purge&&!livein.contains(vr)){
		  ins.remove(j);
		  miscs.remove(i);
		  changed=true;
		  continue;
		}
		defs.add(vr);
		livein.remove(vr);
	  }
	  for(Register r:i.uses())
		if(r instanceof Register.Virtual vr){
		  defs.remove(vr);
		  livein.add(vr);
		}
	}
	return changed;
  }
}
