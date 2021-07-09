package com.jvmutil;

/**
 * @author Robert HG (254963746@qq.com) on 9/15/15.
 */
public interface JVMConstants {

    String JMX_JVM_INFO_NAME = "jvmmonitor:type=JVMInfo";
    String JMX_JVM_MEMORY_NAME = "jvmmonitor:type=JVMMemory";
    String JMX_JVM_GC_NAME = "jvmmonitor:type=JVMGC";
    String JMX_JVM_THREAD_NAME = "jvmmonitor:type=JVMThread";
}
