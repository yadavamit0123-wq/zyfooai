
#include <jni.h>
#include <string>
#include <vector>


extern "C"
__attribute__((unused)) JNIEXPORT jstring JNICALL

Java_com_pt_zyfooai_api_ApiClient_baseUrlFromJNI(JNIEnv *env, jclass clazz) {

    std::string baseURL = "https://home.zyfooai.com/";
    return env->NewStringUTF(baseURL.c_str());

}
