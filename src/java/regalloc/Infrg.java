package regalloc;
import gen.asm.*;;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class Infrg{
  public Map<Register.Virtual,Set<Register.Virtual>>edges;
  public final Label func;
  public Infrg(Cfg cfg){
	this.func=cfg.func;
	edges=new HashMap<Register.Virtual,Set<Register.Virtual>>();
	for(Cfgnode n:cfg.nodes){
	  HashSet<Register.Virtual>lives=new HashSet<Register.Virtual>();
	  for(Register.Virtual vr:n.liveout)
		lives.add(vr);
	  for(int j=n.ins.size();j-->0;){
		for(Register.Virtual vr:lives)
		  for(Register.Virtual ur:lives){
			if(vr==ur)
			  continue;
			Set<Register.Virtual>a=edges.get(vr);
			if(a==null)
			  edges.put(vr,a=new HashSet<Register.Virtual>());
			a.add(ur);
		  }
		Instruction i=n.ins.get(j);
		if(i.def()instanceof Register.Virtual vr){
		  lives.remove(vr);
		}
		for(Register r:i.uses())
		  if(r instanceof Register.Virtual vr){
			lives.add(vr);
		  }
	  }
	}
  }
}
