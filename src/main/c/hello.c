#include <jni.h>
#include <stdio.h>

#include "org_example_Main.h"

JNIEXPORT void JNICALL Java_org_example_Main_print
  (JNIEnv *, jobject)
{
    printf("Hello From C++ World!\n");
    return;
}