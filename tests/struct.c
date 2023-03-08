#include"minic-stdlib.h"

struct bazinga{
  int a;
  int b;
  int c;
};

struct bazinga bazinga(struct bazinga eggs){
  eggs.a=eggs.a+1;
  return eggs;
}

int main(){
  struct bazinga azinga;
  struct bazinga cazinga;
  azinga.a=1;
  cazinga=bazinga(bazinga(azinga));
  print_i(cazinga.a);
}
