int main(){
  int i;
  int*p;
  char s[7];
  p=(int*)mcmalloc(sizeof(int));
  i=0;
  *p=i;//segfault
  p=&i;
  s[4]='e';
}
