#include"minic-stdlib.h"

int iter;

int bazinga(int a,int b,int c){
  //print_i(a);
  //print_c(' ');
  //print_i(b);
  //print_c(' ');
  //print_i(c);
  //print_s((char*)" -> ");
  a=b-c;
  return a;
}

int fac(int n){
  //print_i(iter);
  //print_c(':');
  //print_i(n);
  //print_c('\n');
  if(n>1){
	int tosub;
	tosub=1;
	return n*fac(n-tosub);
  }else
	return 1;
}

void main(){
  int q;
  int f;
  iter=0;
  q=bazinga(1,7,2);
  print_i(q);
  print_s((char*)"\n--\n");
  f=fac(q);
  print_i(f);
  print_s((char*)"\n--\n");
  return;
}
