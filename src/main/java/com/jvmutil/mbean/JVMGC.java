package com.jvmutil.mbean;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Robert HG (254963746@qq.com) on 9/15/15.
 */
public class JVMGC implements JVMGCMBean {

	private static final JVMGC instance = new JVMGC();

	public static JVMGC getInstance() {
		return instance;
	}

	private GarbageCollectorMXBean fullGC;
	private GarbageCollectorMXBean youngGC;

	private long lastYoungGCCollectionCount = -1;
	private long lastYoungGCCollectionTime = -1;
	private long lastFullGCCollectionCount = -1;
	private long lastFullGCCollectionTime = -1;

	private JVMGC() {
		for (GarbageCollectorMXBean item : ManagementFactory.getGarbageCollectorMXBeans()) {
			if ("ConcurrentMarkSweep".equals(item.getName()) //
					|| "MarkSweepCompact".equals(item.getName()) //
					|| "PS MarkSweep".equals(item.getName()) //
					|| "G1 Old Generation".equals(item.getName()) //
					|| "Garbage collection optimized for short pausetimes Old Collector".equals(item.getName()) //
					|| "Garbage collection optimized for throughput Old Collector".equals(item.getName()) //
					|| "Garbage collection optimized for deterministic pausetimes Old Collector".equals(item.getName()) //
			) {
				fullGC = item;
			} else if ("ParNew".equals(item.getName()) //
					|| "Copy".equals(item.getName()) //
					|| "PS Scavenge".equals(item.getName()) //
					|| "G1 Young Generation".equals(item.getName()) //
					|| "Garbage collection optimized for short pausetimes Young Collector".equals(item.getName()) //
					|| "Garbage collection optimized for throughput Young Collector".equals(item.getName()) //
					|| "Garbage collection optimized for deterministic pausetimes Young Collector"
							.equals(item.getName()) //
			) {
				youngGC = item;
			}
		}
	}

	@Override
	public long getYoungGCCollectionCount() {
		if (youngGC == null) {
			return 0;
		}
		return youngGC.getCollectionCount();
	}

	@Override
	public long getYoungGCCollectionTime() {
		if (youngGC == null) {
			return 0;
		}
		return youngGC.getCollectionTime();
	}

	@Override
	public long getFullGCCollectionCount() {
		if (fullGC == null) {
			return 0;
		}
		return fullGC.getCollectionCount();
	}

	@Override
	public long getFullGCCollectionTime() {
		if (fullGC == null) {
			return 0;
		}
		return fullGC.getCollectionTime();
	}

	@Override
	public long getSpanYoungGCCollectionCount() {
		long current = getYoungGCCollectionCount();
		if (lastYoungGCCollectionCount == -1) {
			lastYoungGCCollectionCount = current;
			return 0;
		} else {
			long result = current - lastYoungGCCollectionCount;
			lastYoungGCCollectionCount = current;
			return result;
		}
	}

	@Override
	public long getSpanYoungGCCollectionTime() {
		long current = getYoungGCCollectionTime();
		if (lastYoungGCCollectionTime == -1) {
			lastYoungGCCollectionTime = current;
			return 0;
		} else {
			long result = current - lastYoungGCCollectionTime;
			lastYoungGCCollectionTime = current;
			return result;
		}
	}

	@Override
	public long getSpanFullGCCollectionCount() {
		long current = getFullGCCollectionCount();
		if (lastFullGCCollectionCount == -1) {
			lastFullGCCollectionCount = current;
			return 0;
		} else {
			long result = current - lastFullGCCollectionCount;
			lastFullGCCollectionCount = current;
			return result;
		}
	}

	@Override
	public long getSpanFullGCCollectionTime() {
		long current = getFullGCCollectionTime();
		if (lastFullGCCollectionTime == -1) {
			lastFullGCCollectionTime = current;
			return 0;
		} else {
			long result = current - lastFullGCCollectionTime;
			lastFullGCCollectionTime = current;
			return result;
		}
	}

	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();
		try {
			Class<? extends JVMGC> clazz = this.getClass();
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
