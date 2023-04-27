class Course {
  char name[20];
  int credit;
  int courseWorkScore;
  void whereToAttend(){
	print_s((char*)"Not determined! The course will be held virtually or in person!\n");
  }
  int hasExam(){
	if(courseWorkScore==100)
	  return 0;
	else
	  return 1;
  }
}
class VirtualCourse extends Course{
  int isOnZoom;
  void whereToAttend(){
	print_s((char*)"The course is going to be held on Zoom!\n");
  }
}
int main() {
  class Course course;
  class VirtualCourse vcourse;
  course=new class Course();
  course.courseWorkScore=40;//haha nerds
  if(course.hasExam())
    print_s((char*)"Be ready for the exam!\n");
  else
    print_s((char*)"Be ready for the project implementation!:)\n");
  vcourse=new class VirtualCourse();
  course.whereToAttend();
  vcourse.whereToAttend();
  return 0;
}
