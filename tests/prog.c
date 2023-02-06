void foo(){
  void*v;
  return*v;
}

void bar(){
  return foo();
}

int main(){
  void v[10];
  bar();
}
