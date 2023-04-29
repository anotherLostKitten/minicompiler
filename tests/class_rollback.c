class Q{
  int a;
}
class R{
  class Q a[3];
  class Q b;
}
class Q make_q(){
  class Q q;
  q=new class Q();
  q.a=5;
  return q;
}
void main(){
  class R classes[3];
  class R*cp;
  int q;
  classes[1]=new class R();
  classes[1].a[1]=new class Q();
  classes[1].a[1].a=3;
  print_i(classes[1].a[1].a);
  print_c('\n');
  classes[1].b=classes[1].a[1];
  print_i(classes[1].b==classes[1].a[1]);
  print_c('\n');
  cp=(class R*)classes;
  q=*(int*)&cp+4;
  cp=*(class R**)&q;
  print_i((*cp).b.a);
  print_c('\n');
  *cp=new class R();
  (*cp).b=make_q();
  print_i((*cp).b.a);
  print_c('\n');
  print_i(classes[1].b.a);
  print_c('\n');
}
