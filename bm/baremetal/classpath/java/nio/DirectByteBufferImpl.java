/* DirectByteBufferImpl.java -- 
   Copyright (C) 2003, 2004 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package java.nio;

import gnu.classpath.Configuration;
import gnu.gcj.RawData;

class DirectByteBufferImpl extends ByteBuffer
{
  static
  {
    // load the shared library needed for native methods.
    if (Configuration.INIT_LOAD_LIBRARY)
      {
        System.loadLibrary ("javanio");
      }
  }
  
  /** Used by MappedByteBufferImpl to prevent premature GC. */
  protected Object owner;

  RawData address;
  private boolean readOnly;

  public DirectByteBufferImpl (RawData address, long len)
  {
    this (null, address, (int) len, (int) len, 0, false);
  }
  
  public DirectByteBufferImpl (Object owner, RawData address,
			       int capacity, int limit,
			       int position, boolean readOnly)
  {
    super (capacity, limit, position, -1);
    this.address = address;
    this.readOnly = readOnly;
    this.owner = owner;
  }

  private static RawData allocateImpl (int capacity) {
    throw new RuntimeException("directbytbufferimpl.RawData");    
  }
  private static void freeImpl (RawData address) {
    throw new RuntimeException("DirectByteBufferImpl.freeImpl");    
  }
  
  protected void finalize () throws Throwable
  {
    freeImpl (address);
  }
  
  static byte getImpl (RawData address, int index) {
    throw new RuntimeException("DirectByteBufferImpl.getImpl");    
  }
  static void putImpl (RawData address, int index, byte value) {
    throw new RuntimeException("DirectByteBufferImpl.putImpll");    
  }

  public byte get ()
  {
    int pos = position();
    if (pos >= limit())
      throw new BufferUnderflowException();
    byte result = getImpl (address, pos);
    position (pos + 1);
    return result;
  }

  public byte get (int index)
  {
    if (index >= limit())
      throw new BufferUnderflowException();
    return getImpl (address, index);
  }

  static void getImpl (RawData address, int index,
			      byte[] dst, int offset, int length)
  {
    throw new RuntimeException("DirectByteBufferImpl.getImp2");    
  }

  public ByteBuffer get (byte[] dst, int offset, int length)
  {
    if (offset < 0 || length < 0 || offset + length > dst.length)
      throw new IndexOutOfBoundsException ();
    if (length > remaining())
      throw new BufferUnderflowException();

    int index = position();
    getImpl(address, index, dst, offset, length);
    position(index+length);

    return this;
  }

  public ByteBuffer put (byte value)
  {
    int pos = position();
    if (pos >= limit())
      throw new BufferUnderflowException();
    putImpl (address, pos, value);
    position (pos + 1);
    return this;
  }
  
  public ByteBuffer put (int index, byte value)
  {
    if (index >= limit())
      throw new BufferUnderflowException();
    putImpl (address, index, value);
    return this;
  }
  
  void shiftDown (int dst_offset, int src_offset, int count) {
    throw new RuntimeException("DirectByteBufferImpl.shiftDown");    
  }

  public ByteBuffer compact ()
  {
    int pos = position();
    if (pos > 0)
      {
	int count = remaining();
	shiftDown(0, pos, count);
	position(count);
	limit(capacity());
      }
    return this;
  }

  public static RawData adjustAddress(RawData address, int offset) {
    throw new RuntimeException("DirectByteBuffer.adjustAddress");    
  }

  public ByteBuffer slice ()
  {
    int rem = remaining();
    return new DirectByteBufferImpl (owner,
				     adjustAddress(address, position()),
				     rem, rem, 0, isReadOnly ());
  }

  private ByteBuffer duplicate (boolean readOnly)
  {
    int pos = position();
    reset();
    int mark = position();
    position(pos);
    DirectByteBufferImpl result
      = new DirectByteBufferImpl (owner, address, capacity (), limit (),
				  pos, readOnly);
    if (mark != pos)
      {
	result.position(mark);
	result.mark();
	result.position(pos);
      }
    return result;
  }

  public ByteBuffer duplicate ()
  {
    return duplicate(isReadOnly());
  }

  public ByteBuffer asReadOnlyBuffer ()
  {
    return duplicate(true);
  }

  public boolean isReadOnly ()
  {
    return readOnly;
  }

  public boolean isDirect ()
  {
    return true;
  }

  public CharBuffer asCharBuffer ()
  {
    return new CharViewBufferImpl (this, remaining() >> 1);
  }

  public ShortBuffer asShortBuffer ()
  {
    return new ShortViewBufferImpl (this, remaining() >> 1);
  }

  public IntBuffer asIntBuffer ()
  {
    return new IntViewBufferImpl (this, remaining() >> 2);
  }

  public LongBuffer asLongBuffer ()
  {
    return new LongViewBufferImpl (this, remaining() >> 3);
  }

  public FloatBuffer asFloatBuffer ()
  {
    return new FloatViewBufferImpl (this, remaining() >> 2);
  }

  public DoubleBuffer asDoubleBuffer ()
  {
    return new DoubleViewBufferImpl (this, remaining() >> 3);
  }

  final public char getChar ()
  {
    return ByteBufferHelper.getChar(this, order());
  }
  
  final public ByteBuffer putChar (char value)
  {
    ByteBufferHelper.putChar(this, value, order());
    return this;
  }
  
  final public char getChar (int index)
  {
    return ByteBufferHelper.getChar(this, index, order());
  }
  
  final public ByteBuffer putChar (int index, char value)
  {
    ByteBufferHelper.putChar(this, index, value, order());
    return this;
  }

  final public short getShort ()
  {
    return ByteBufferHelper.getShort(this, order());
  }
  
  final public ByteBuffer putShort (short value)
  {
    ByteBufferHelper.putShort(this, value, order());
    return this;
  }
  
  final public short getShort (int index)
  {
    return ByteBufferHelper.getShort(this, index, order());
  }
  
  final public ByteBuffer putShort (int index, short value)
  {
    ByteBufferHelper.putShort(this, index, value, order());
    return this;
  }

  final public int getInt ()
  {
    return ByteBufferHelper.getInt(this, order());
  }
  
  final public ByteBuffer putInt (int value)
  {
    ByteBufferHelper.putInt(this, value, order());
    return this;
  }
  
  final public int getInt (int index)
  {
    return ByteBufferHelper.getInt(this, index, order());
  }
  
  final public ByteBuffer putInt (int index, int value)
  {
    ByteBufferHelper.putInt(this, index, value, order());
    return this;
  }

  final public long getLong ()
  {
    return ByteBufferHelper.getLong(this, order());
  }
  
  final public ByteBuffer putLong (long value)
  {
    ByteBufferHelper.putLong (this, value, order());
    return this;
  }
  
  final public long getLong (int index)
  {
    return ByteBufferHelper.getLong (this, index, order());
  }
  
  final public ByteBuffer putLong (int index, long value)
  {
    ByteBufferHelper.putLong (this, index, value, order());
    return this;
  }

  final public float getFloat ()
  {
    return ByteBufferHelper.getFloat (this, order());
  }
  
  final public ByteBuffer putFloat (float value)
  {
    ByteBufferHelper.putFloat (this, value, order());
    return this;
  }
  
  public final float getFloat (int index)
  {
    return ByteBufferHelper.getFloat (this, index, order());
  }

  final public ByteBuffer putFloat (int index, float value)
  {
    ByteBufferHelper.putFloat (this, index, value, order());
    return this;
  }

  final public double getDouble ()
  {
    return ByteBufferHelper.getDouble (this, order());
  }

  final public ByteBuffer putDouble (double value)
  {
    ByteBufferHelper.putDouble (this, value, order());
    return this;
  }
  
  final public double getDouble (int index)
  {
    return ByteBufferHelper.getDouble (this, index, order());
  }
  
  final public ByteBuffer putDouble (int index, double value)
  {
    ByteBufferHelper.putDouble (this, index, value, order());
    return this;
  }
}
