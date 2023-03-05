void foo(){
  void*v;
  return*v;
}

struct small{
  char a;
  char b;
  char c[2];
};
struct big{
  int int_var;
  char*char_star_var;
  void*void_star_arr_var[100];
  int*int_star_arr_var[10];
  char char_arr_var[10];
  char char_var;
  char char_arr_arr_var[5][11];
  struct small small_struct_arr_var[3];
};

int int_var;
char*char_star_var;
void*void_star_arr_var[100];
int*int_star_arr_var[10];
char char_arr_var[10];
char char_var;
char char_arr_arr_var[5][11];
struct small small_struct_arr_var[3];

struct small small_struct;
struct big big_struct;

void bar(){
  return foo();
}

int main(){
  void v[10];
  bar();
}
