package com.jvmutil.mbean;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;

/**
 * @author Robert HG (254963746@qq.com) on 9/15/15.
 */
@SuppressWarnings("restriction")
public class JVMThread implements JVMThreadMBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(JVMThread.class);

    private volatile long lastCPUTime;
    private volatile long lastCPUUpTime;
	private OperatingSystemMXBean OperatingSystem;
    private RuntimeMXBean Runtime;

    private static final JVMThread instance = new JVMThread();

    public static JVMThread getInstance() {
        return instance;
    }

    private ThreadMXBean threadMXBean;

	private JVMThread() {
        threadMXBean = ManagementFactory.getThreadMXBean();
        OperatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        Runtime = ManagementFactory.getRuntimeMXBean();

        try {
            lastCPUTime = OperatingSystem.getProcessCpuTime();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal getProcessCpuTimeRate() {
		long cpuTime = OperatingSystem.getProcessCpuTime();
        long upTime = Runtime.getUptime();

        long elapsedCpu = cpuTime - lastCPUTime;
        long elapsedTime = upTime - lastCPUUpTime;

        lastCPUTime = cpuTime;
        lastCPUUpTime = upTime;

        BigDecimal cpuRate;
        if (elapsedTime <= 0) {
            return new BigDecimal(0);
        }

        float cpuUsage = elapsedCpu / (elapsedTime * 10000F);
        cpuRate = new BigDecimal(cpuUsage, new MathContext(4));

        return cpuRate;
    }

    @Override
    public int getDaemonThreadCount() {
        return threadMXBean.getDaemonThreadCount();
    }

    @Override
    public int getThreadCount() {
        return threadMXBean.getThreadCount();
    }

    @Override
    public long getTotalStartedThreadCount() {
        return threadMXBean.getTotalStartedThreadCount();
    }
    
    

    @Override
    public int getDeadLockedThreadCount() {
        try {
            long[] deadLockedThreadIds = threadMXBean.findDeadlockedThreads();
            if (deadLockedThreadIds == null) {
                return 0;
            }
            return deadLockedThreadIds.length;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    
    @Override
	public String toString() {

		StringBuffer result = new StringBuffer();
		try {
			Class<? extends JVMThread> clazz = this.getClass();
			// 获取当前类的全部属性
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// 遍历属性得到属性名
				String fieldName = field.getName();
				// 如果是用于序列化的直接过滤掉
				if ("serialVersionUID".equals(fieldName)) {
					continue;
				}
				// 判断属性的类型，主要是区分boolean和其他类型
				Class<?> type = field.getType();

				// boolean的取值是is,其他是get
				String methodName = (type.getTypeName().equals("boolean") ? "is" : "get")
						+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
				
				// 通过方法名得到方法对象
				Method method = clazz.getMethod(methodName);
				// 得到这个方法的返回值
				Object resultObj = method.invoke(this);
				if (resultObj != null && !"".equals(resultObj)) {
					result.append(fieldName).append(":").append(resultObj).append("|");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}
