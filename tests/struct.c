#include"minic-stdlib.h"

struct bazinga{
  int a;
  int b;
  char ss[9];
  int c;
};

void prntstruct(struct bazinga tp){
  print_i(tp.a);
  print_c(' ');
  print_i(tp.b);
  print_c(' ');
  print_s((char*)tp.ss);
  print_c(' ');
  print_i(tp.c);
  print_c('\n');
}

struct bazinga bazinga(struct bazinga eggs){
  print_s((char*)"inb: ");
  prntstruct(eggs);
  eggs.a=eggs.a+1;
  eggs.b=eggs.b+2;
  eggs.c=eggs.c+3;
  eggs.ss[3]='A';
  eggs.ss[4]='B';
  eggs.ss[5]='C';
  eggs.ss[6]='D';
  print_s((char*)"oub: ");
  prntstruct(eggs);
  return eggs;
}

int main(){
  struct bazinga azinga;
  struct bazinga cazinga;
  int i;
  i=0;
  azinga.a=1;
  azinga.b=2;
  azinga.c=3;
  while(i<9){
	int tmp;
	tmp=i+(int)'a';
	azinga.ss[i]=*(char*)&tmp;
	i=i+1;
  }
  prntstruct(azinga);
  cazinga=azinga;
  prntstruct(cazinga);
  cazinga=bazinga(bazinga(azinga));
  prntstruct(cazinga);
  prntstruct(bazinga(bazinga(cazinga)));
}
