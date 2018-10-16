package au.com.addstar.monolith.lookup;

public interface LookupCallback<T>
{
	void onResult(boolean success, T value, Throwable error);
}
