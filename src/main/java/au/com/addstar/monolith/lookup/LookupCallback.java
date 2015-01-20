package au.com.addstar.monolith.lookup;

public interface LookupCallback<T>
{
	public void onResult(boolean success, T value, Throwable error);
}
