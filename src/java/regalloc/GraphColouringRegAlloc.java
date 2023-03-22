package regalloc;
import gen.asm.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GraphColouringRegAlloc implements AssemblyPass{
  public static final GraphColouringRegAlloc INSTANCE=new GraphColouringRegAlloc();
  public List<Cfg>cfgs;
  public List<Infrg>infrgs;
  public GraphColouringRegAlloc(){
	this.cfgs=new ArrayList<Cfg>();
	this.infrgs=new ArrayList<Infrg>();
  }
  @Override
  public AssemblyProgram apply(AssemblyProgram prog){
	AssemblyProgram np=new AssemblyProgram();
	for(AssemblyProgram.Section s:prog.sections)
	  if(s.type==AssemblyProgram.Section.Type.TEXT){
		Cfg cfg=new Cfg(s);
		cfgs.add(cfg);//so i can print them
		Infrg infrg=new Infrg(cfg);
		infrg.allocate();
		infrgs.add(infrg);//so i can print them
		AssemblyProgram.Section func,spills=null;
		if(!cfg.canfo&&infrg.numSpills>0){
		  spills=np.newSection(AssemblyProgram.Section.Type.DATA);
		  for(Label spl:cfg.spls){
			spills.emit(spl);
			spills.emit(new Directive("space "+4));
		  }
		}
		func=np.newSection(AssemblyProgram.Section.Type.TEXT);
		for(Cfgnode n:cfg.nodes){
		  int misci=0;
		  for(Instruction i:n.ins){
			List<AssemblyItem>misc=n.miscs.get(misci++);
			if(misc!=null)
			  for(AssemblyItem ai:misc)
				switch(ai){
				case Comment aaii->func.emit(aaii);
				case Instruction aaii->
				  throw new IllegalStateException("unreachable not misc");
				case Label aaii->func.emit(aaii);
				case Directive aaii->func.emit(aaii);
				};
			if(i.opcode==OpCode.PUSH_REGISTERS){
			  func.emit("pushing regs");
			  if(infrg.numSpills>0)
				if(!cfg.canfo){
				  Register.Arch ppreg=Infrg.regs[infrg.allocs.get(cfg.ppreg)];
				  int splo=-4;
				  for(Label spl:cfg.spls){
					func.emit(OpCode.LA,ppreg,spl);
					func.emit(OpCode.LW,ppreg,ppreg,0);
					func.emit(OpCode.SW,ppreg,Register.Arch.sp,splo);
					splo-=4;
				  }
				}
			  func.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,cfg.splo-4*infrg.numRegs);
			  for(int o=0;o<infrg.numRegs;o++)
				func.emit(OpCode.SW,infrg.regs[o],Register.Arch.sp,4*o);
			  func.emit("gnihsup regs");
			}else if(i.opcode==OpCode.POP_REGISTERS){
			  func.emit("popping regs");
			  for(int o=0;o<infrg.numRegs;o++)
				func.emit(OpCode.LW,infrg.regs[o],Register.Arch.sp,4*o);
			  func.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,4*infrg.numRegs-cfg.splo);
			  if(!cfg.canfo){
				Register.Arch ppreg=Infrg.regs[infrg.allocs.get(cfg.ppreg)],poreg=Infrg.regs[infrg.allocs.get(cfg.poreg)];
				int splo=-4;
				for(Label spl:cfg.spls){
				  func.emit(OpCode.LA,ppreg,spl);
				  func.emit(OpCode.LW,poreg,Register.Arch.sp,splo);
				  func.emit(OpCode.SW,poreg,ppreg,0);
				  splo-=4;
				}
			  }
			  func.emit("gnippop regs");
			}else{
			  Map<Register,Register>repl=new HashMap<Register,Register>();
			  for(Register reg:i.registers())
				if(reg instanceof Register.Virtual vr){
				  int r=infrg.allocs.get(reg);
				  switch(r){
				  case-2:
				  case-1:
					repl.put(reg,Register.Arch.zero);
					break;
				  default:
					repl.put(reg,Infrg.regs[r]);
				  }
				}
			  func.emit(i.rebuild(repl));
			}
		  }
		}
	  }else
		np.emitSection(s);
	return np;
  }
}
