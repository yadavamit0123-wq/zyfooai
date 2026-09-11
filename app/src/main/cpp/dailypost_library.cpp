
#include <jni.h>
#include <string>
#include <vector>


extern "C"
__attribute__((unused)) JNIEXPORT jstring JNICALL

Java_com_growwthapps_dailypost_v2_api_ApiClient_baseUrlFromJNI(JNIEnv *env, jclass clazz) {

    std::string baseURL = "https://home.zyfooai.com/";
    return env->NewStringUTF(baseURL.c_str());

}
