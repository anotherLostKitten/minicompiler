package regalloc;
import gen.asm.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;
public class Cfgnode{
  public final Instruction ins;
  public Set<Register.Virtual>livein,liveout;
  public List<Cfgnode>preds;
  public List<AssemblyItem>misc;
  public Cfgnode(Instruction ins,Cfgnode b4,List<AssemblyItem>misc){
	this.ins=ins;
	this.preds=new ArrayList<Cfgnode>();
	if(b4!=null)
	  this.preds.add(b4);
	this.misc=misc;
	this.livein=new HashSet<Register.Virtual>();
	this.liveout=new HashSet<Register.Virtual>();
	for(Register r:ins.uses())
	  if(r instanceof Register.Virtual vr)
		this.livein.add(vr);
  }
}
