package com.streaming.aop;

public class DataSourceContextHolder {
	private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();

	public static void setDataSourceType(DataSourceType dataSourceType) {
		CONTEXT_HOLDER.set(dataSourceType);
	}

	public static DataSourceType getDataSourceType() {
		return CONTEXT_HOLDER.get();
	}

	public static void clearDataSourceType() {
		CONTEXT_HOLDER.remove();
	}

	public static boolean isRead() {
		return DataSourceType.READ.equals(getDataSourceType());
	}

	public static boolean isWrite() {
		return DataSourceType.WRITE.equals(getDataSourceType());
	}
}