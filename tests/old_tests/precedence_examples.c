struct node_t{
  int field1;
  char field2;
};
struct node_t n;
void foo(){return;}
int main(){
  int array[3][4];
  array[1][2];       // (array[1])[2]
  mystruct.field[1]; // (mystruct.field)[1]
  2*3+4;             // (2*3)+4
  2+3*4;             // 2+(3*4)
  &*ptr;             // &(*ptr)
  &p[1];             // &(p[1])
  a+b+c;             // (a+b)+c
  a=b=c;             // a=(b=c)
  a=b.c=d;           // a=((b.c)=d)
  y = 3*x;
  +x;
  -x;
  -x*3;
  -1;
}
