#ifndef STUDENT_H
#define STUDENT_H

class Student{
public:
    const char *mName;
    int mAge;
    void setName(const char *name);
    void setAge(int age);
    int getAge();
    const char * getName();

};


#endif // STUDENT_H
