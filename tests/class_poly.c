class A{
  int x;
  void foo(){
	print_i(x);
	print_s((char *)" A\n");
  }
  void bar(){
	x=x+1;
	foo();
  }
}
class B extends A{
  void foo(){
	print_i(x);
	print_s((char *)" B\n");
  }
}
void main(){
  class B b;
  b=new class B();
  b.x=5;
  b.bar();
  ((class A)b).bar();
}
