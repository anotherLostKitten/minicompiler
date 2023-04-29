class Q{
  int a;
  void func(){
	print_s((char*)"howdy\n");
  }
}
class R extends Q{
  int b;
}
class S extends R{
  void chunk(){
	print_i(a+b);
	func();
  }
}
void main(){
  class S q;
  class Q s;
  q=new class S();
  q.a=3;
  q.b=4;
  q.func();
  print_i(q.a);
  q.chunk();
  s=(class Q)q;
  s.func();
}
