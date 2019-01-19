#include "Student.h"

void Student:: setName(const char *name){
    mName = name;
}
void Student:: setAge(int age){
    mAge = age;
}
int Student:: getAge(){
    return mAge;
}
const char*  Student:: getName(){
    return mName;
}
