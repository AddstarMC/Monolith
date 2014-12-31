package au.com.addstar.monolith.villagers;

import java.util.AbstractList;
import java.util.List;
import com.google.common.base.Converter;

public class TransformingList<A, B> extends AbstractList<A>
{
	private List<B> mSourceList;
	
	private Converter<B, A> mConverter;
	
	public TransformingList(List<B> source, Converter<B, A> converter)
	{
		mSourceList = source;
		mConverter = converter;
	}
	
	@Override
	public A get( int index )
	{
		return mConverter.convert(mSourceList.get(index));
	}

	@Override
	public int size()
	{
		return mSourceList.size();
	}
	
	@Override
	public void add( int index, A element )
	{
		mSourceList.add(index, mConverter.reverse().convert(element));
	}
	
	@Override
	public A set( int index, A element )
	{
		return mConverter.convert(mSourceList.set(index, mConverter.reverse().convert(element)));
	}
	
	@Override
	public A remove( int index )
	{
		return mConverter.convert(mSourceList.remove(index));
	}
	
	@Override
	public void clear()
	{
		mSourceList.clear();
	}
}
