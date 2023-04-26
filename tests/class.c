class Azinga{
  int y;
  void func(){
	print_s((char*)"goodbye gamers\n");
  }
  void chunk(int a){
	print_i(a+y);
  }
}
class Bazinga extends Azinga{
  int x;
  void func(){
	print_s((char*)"hello gamers\n");
  }
  void anotherFunc(int a){
	print_i(a);
	chunk(a);
  }
}
int main(){
  class Bazinga bazinga;
  class Azinga tSatF;
  class Azinga aILD;
  bazinga=new class Bazinga();
  tSatF=(class Azinga)new class Bazinga();
  aILD=new class Azinga();
  bazinga.x=56;
  bazinga.y=22;
  bazinga.func();
  tSatF.func();
  aILD.func();
  bazinga.anotherFunc(bazinga.y);
}
