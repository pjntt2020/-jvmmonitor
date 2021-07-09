package com.jvmutil;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.jvmutil.mbean.JVMGC;
import com.jvmutil.mbean.JVMInfo;
import com.jvmutil.mbean.JVMMemory;
import com.jvmutil.mbean.JVMThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Robert HG (254963746@qq.com) on 9/15/15.
 */
public class JVMMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JVMMonitor.class);

    private static final MBeanServer MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final AtomicBoolean start = new AtomicBoolean(false);

    private final static Map<String, Object> MONITOR_MAP = new HashMap<String, Object>();

    public static void start() {
        if (start.compareAndSet(false, true)) {
            if (MONITOR_MAP.size()<1) {
                MONITOR_MAP.put(JVMConstants.JMX_JVM_INFO_NAME, JVMInfo.getInstance());
                MONITOR_MAP.put(JVMConstants.JMX_JVM_MEMORY_NAME, JVMMemory.getInstance());
                MONITOR_MAP.put(JVMConstants.JMX_JVM_GC_NAME, JVMGC.getInstance());
                MONITOR_MAP.put(JVMConstants.JMX_JVM_THREAD_NAME, JVMThread.getInstance());
            }
            try {
                for (Map.Entry<String, Object> entry : MONITOR_MAP.entrySet()) {
                    ObjectName objectName = new ObjectName(entry.getKey());
                    if (!MBEAN_SERVER.isRegistered(objectName)) {
                        MBEAN_SERVER.registerMBean(entry.getValue(), objectName);
                    }
                }
                LOGGER.info("Start JVMMonitor succeed ");
            } catch (Exception e) {
                LOGGER.error("Start JVMMonitor error ", e);
            }
        }
    }

    public static void stop() {
        if (start.compareAndSet(true, false) ) {
            for (Map.Entry<String, Object> entry : MONITOR_MAP.entrySet()) {
                try {
                    ObjectName objectName = new ObjectName(entry.getKey());
                    if (MBEAN_SERVER.isRegistered(objectName)) {
                        MBEAN_SERVER.unregisterMBean(objectName);
                    }
                } catch (Exception e) {
                    LOGGER.error("Stop JVMMonitor {} error", entry.getKey(), e);
                }
            }
            LOGGER.info("Stop JVMMonitor succeed ");
        }
    }

    public static Map<String, Object> getAttribute(String objectName, List<String> attributeNames) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            for (String attributeName : attributeNames) {
                try {
                    Object value = MBEAN_SERVER.getAttribute(new ObjectName(objectName), attributeName);
                    result.put(attributeName, value);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            LOGGER.error("get Attribute error, objectName=" + objectName + ", attributeName=" + attributeNames, e);
        }
        return result;
    }

    public static Object getAttribute(String objectName, String attributeName) {
        try {
            return MBEAN_SERVER.getAttribute(new ObjectName(objectName), attributeName);
        } catch (Exception e) {
            LOGGER.error("get Attribute error, objectName=" + objectName + ", attributeName=" + attributeName, e);
        }
        return null;
    }

}



