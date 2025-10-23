#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include "unishox2.h"

// Maximum buffer size for compression/decompression
#define MAX_BUFFER_SIZE 65536

JNIEXPORT jbyteArray JNICALL
Java_org_operatorfoundation_unishox2_Unishox2_compress(JNIEnv *env, jobject obj, jstring text) {
    if (text == NULL) {
        jclass exClass = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exClass, "Input text cannot be null");
        return NULL;
    }

    // Get UTF-8 bytes from Java String
    const char *input = (*env)->GetStringUTFChars(env, text, NULL);
    if (input == NULL) {
        return NULL; // OutOfMemoryError already thrown
    }

    jsize input_len = (*env)->GetStringUTFLength(env, text);
    
    if (input_len == 0) {
        (*env)->ReleaseStringUTFChars(env, text, input);
        jclass exClass = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exClass, "Input text cannot be empty");
        return NULL;
    }

    // Allocate buffer for compressed output
    unsigned char *output = (unsigned char *)malloc(MAX_BUFFER_SIZE);
    if (output == NULL) {
        (*env)->ReleaseStringUTFChars(env, text, input);
        jclass exClass = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
        (*env)->ThrowNew(env, exClass, "Failed to allocate compression buffer");
        return NULL;
    }

    // Compress the data
    int compressed_len = unishox2_compress_simple(input, input_len, output);
    
    (*env)->ReleaseStringUTFChars(env, text, input);

    if (compressed_len <= 0) {
        free(output);
        jclass exClass = (*env)->FindClass(env, "java/lang/RuntimeException");
        (*env)->ThrowNew(env, exClass, "Compression failed");
        return NULL;
    }

    // Create Java byte array and copy compressed data
    jbyteArray result = (*env)->NewByteArray(env, compressed_len);
    if (result == NULL) {
        free(output);
        return NULL; // OutOfMemoryError already thrown
    }

    (*env)->SetByteArrayRegion(env, result, 0, compressed_len, (jbyte *)output);
    free(output);

    return result;
}

JNIEXPORT jstring JNICALL
Java_org_operatorfoundation_unishox2_Unishox2_decompress(JNIEnv *env, jobject obj, jbyteArray compressed) {
    if (compressed == NULL) {
        jclass exClass = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exClass, "Input compressed data cannot be null");
        return NULL;
    }

    jsize input_len = (*env)->GetArrayLength(env, compressed);
    
    if (input_len == 0) {
        jclass exClass = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exClass, "Input compressed data cannot be empty");
        return NULL;
    }

    // Get the compressed bytes
    jbyte *input = (*env)->GetByteArrayElements(env, compressed, NULL);
    if (input == NULL) {
        return NULL; // OutOfMemoryError already thrown
    }

    // Allocate buffer for decompressed output
    char *output = (char *)malloc(MAX_BUFFER_SIZE);
    if (output == NULL) {
        (*env)->ReleaseByteArrayElements(env, compressed, input, JNI_ABORT);
        jclass exClass = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
        (*env)->ThrowNew(env, exClass, "Failed to allocate decompression buffer");
        return NULL;
    }

    // Decompress the data
    int decompressed_len = unishox2_decompress_simple((unsigned char *)input, input_len, output);
    
    (*env)->ReleaseByteArrayElements(env, compressed, input, JNI_ABORT);

    if (decompressed_len <= 0) {
        free(output);
        jclass exClass = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exClass, "Decompression failed - invalid compressed data");
        return NULL;
    }

    // Create Java String from decompressed UTF-8 data
    jstring result = (*env)->NewStringUTF(env, output);
    free(output);

    return result;
}
