class Azinga{
  int y;
  void func(){
	print_s((char*)"goodbye gamers");
  }
}
class Bazinga extends Azinga{
  int x;
  void func(){
	print_s((char*)"hello gamers");
  }
}
int main(){
  class Bazinga bazinga;
  bazinga=new class Bazinga();
  bazinga.x=56;
  bazinga.y=22;
  bazinga.func();
}
