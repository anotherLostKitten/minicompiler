struct fungus{
  char a[10];
  int b;
};
class Azinga{
  int y;
  struct fungus fungus;
  char a[2];
  void func(){
	print_s((char*)"goodbye gamers\n");
  }
  void chunk(int a){
	print_i(a+y);
  }
  int to_over(){
	return 0;
  }
}
class Bazinga extends Azinga{
  int x;
  char qq;
  int bb;
  class Azinga nest_azing;
  int nest_count;
  int acc;
  void func(){
	print_s((char*)"hello gamers\n");
  }
  void anotherFunc(int a){
	print_i(a);
	chunk(a);
  }
  void startums(){
	int i;
	i=0;
	fungus.b=757;
	while(i<10){
	  fungus.a[i]="tetrisch\n\0"[i];
	  i=i+1;
	}
	y=2;
	a[0]='q';
	a[1]='\0';
	x=99;
	qq='p';
	bb=808;
  }
  struct fungus testums(){
	print_s((char*)fungus.a);
	print_i(fungus.b);
	print_c('\n');
	print_c(qq);
	print_i(bb);
	print_s((char*)a);
	return fungus;
  }
  int fibb(int a){
	acc=1;
	nest_count=a;
	return to_over();
  }
  int to_over(){
	if(nest_count>0){
	  acc=acc*nest_count;
	  nest_count=nest_count-1;
	  return nest_azing.to_over();
	}else
	  return acc;
  }
  class Azinga returnor(){
	return nest_azing;
  }
}
int main(){
  class Bazinga bazinga;
  class Azinga tSatF;
  class Azinga aILD;
  class Azinga classes[3];
  struct fungus asdf;
  int*tmp;
  bazinga=new class Bazinga();
  tSatF=(class Azinga)new class Bazinga();
  aILD=new class Azinga();
  bazinga.x=56;
  bazinga.y=22;
  bazinga.func();
  tSatF.func();
  aILD.func();
  bazinga.anotherFunc(bazinga.y);
  print_s((char*)"\nnow doing other tests\n");
  bazinga.startums();
  print_s((char*)"\nstartedum\n");
  asdf=bazinga.testums();
  print_c('\n');
  print_s((char*)asdf.a);
  print_i(asdf.b);
  print_c('\n');
  print_c(bazinga.qq);
  print_i(bazinga.bb);
  print_s((char*)bazinga.a);
  print_c('\n');
  asdf=((class Azinga)bazinga).fungus;
  print_s((char*)asdf.a);
  print_i(asdf.b);
  print_s((char*)"\ntesting cursed factorial\n");
  bazinga.nest_azing=(class Azinga)bazinga;
  print_i(bazinga.fibb(5));
  print_s((char*)"\ntesting some reference stuff\n");
  print_i(bazinga==bazinga);
  print_i((class Azinga)bazinga==aILD);
  print_i((class Azinga)bazinga==(class Azinga)bazinga);
  print_i(sizeof(class Azinga));
  print_c('\n');
  print_i(*(int*)&bazinga);
  print_c('\n');
  tmp=&bazinga.y;
  print_i(*(int*)&tmp);
  print_c('\n');
  tmp=(int*)&bazinga.fungus;
  print_i(*(int*)&tmp);
  print_s((char*)"\nvirtable\n");
  tmp=**(int***)&bazinga;
  print_i(tmp[0]);
  print_c('\n');
  print_i(tmp[1]);
  print_c('\n');
  print_i(tmp[2]);
  print_c('\n');
  print_s((char*)"\nidk man\n");
  bazinga.returnor().func();
  classes[0]=tSatF;
  classes[1]=aILD;
  classes[2]=(class Azinga)bazinga;
  classes[0].func();
  classes[1].func();
  classes[2].func();
}
