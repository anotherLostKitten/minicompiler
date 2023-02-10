void foo(){
  void*v;
  return*v;
}

struct str {
  int a;
  void*g;
  void****q[100];
  int*b[10];
  struct str*p[10];
  struct str*rl;
  struct str rl[4][10];
};

struct str d;

void bar(){
  return foo();
}

int main(){
  void v[10];
  bar();
}
