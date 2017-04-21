/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.flatbuffers;

import static com.google.flatbuffers.Constants.*;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.WordUtils;

/// @cond FLATBUFFERS_INTERNAL

/**
 * All tables in the generated code derive from this class, and add their own accessors.
 */
public class Table implements IFlatBuffer {
  private final static ThreadLocal<CharsetDecoder> UTF8_DECODER = new ThreadLocal<CharsetDecoder>() {
    @Override
    protected CharsetDecoder initialValue() {
      return Charset.forName("UTF-8").newDecoder();
    }
  };
  private final static ThreadLocal<CharBuffer> CHAR_BUFFER = new ThreadLocal<CharBuffer>();
  /** Used to hold the position of the `bb` buffer. */
  protected int bb_pos;
  /** The underlying ByteBuffer to hold the data of the Table. */
  protected ByteBuffer bb;

  /**
   * Get the underlying ByteBuffer.
   *
   * @return Returns the Table's ByteBuffer.
   */
  public ByteBuffer getByteBuffer() { return bb; }

  /**
   * Look up a field in the vtable.
   *
   * @param vtable_offset An `int` offset to the vtable in the Table's ByteBuffer.
   * @return Returns an offset into the object, or `0` if the field is not present.
   */
  protected int __offset(int vtable_offset) {
    int vtable = bb_pos - bb.getInt(bb_pos);
    return vtable_offset < bb.getShort(vtable) ? bb.getShort(vtable + vtable_offset) : 0;
  }

  /**
   * Retrieve a relative offset.
   *
   * @param offset An `int` index into the Table's ByteBuffer containing the relative offset.
   * @return Returns the relative offset stored at `offset`.
   */
  protected int __indirect(int offset) {
    return offset + bb.getInt(offset);
  }

  /**
   * Create a Java `String` from UTF-8 data stored inside the FlatBuffer.
   *
   * This allocates a new string and converts to wide chars upon each access,
   * which is not very efficient. Instead, each FlatBuffer string also comes with an
   * accessor based on __vector_as_bytebuffer below, which is much more efficient,
   * assuming your Java program can handle UTF-8 data directly.
   *
   * @param offset An `int` index into the Table's ByteBuffer.
   * @return Returns a `String` from the data stored inside the FlatBuffer at `offset`.
   */
  protected String __string(int offset) {
    CharsetDecoder decoder = UTF8_DECODER.get();
    decoder.reset();

    offset += bb.getInt(offset);
    ByteBuffer src = bb.duplicate().order(ByteOrder.LITTLE_ENDIAN);
    int length = src.getInt(offset);
    src.position(offset + SIZEOF_INT);
    src.limit(offset + SIZEOF_INT + length);

    int required = (int)((float)length * decoder.maxCharsPerByte());
    CharBuffer dst = CHAR_BUFFER.get();
    if (dst == null || dst.capacity() < required) {
      dst = CharBuffer.allocate(required);
      CHAR_BUFFER.set(dst);
    }

    dst.clear();

    try {
      CoderResult cr = decoder.decode(src, dst, true);
      if (!cr.isUnderflow()) {
        cr.throwException();
      }
    } catch (CharacterCodingException x) {
      throw new Error(x);
    }

    return dst.flip().toString();
  }

  /**
   * Get the length of a vector.
   *
   * @param offset An `int` index into the Table's ByteBuffer.
   * @return Returns the length of the vector whose offset is stored at `offset`.
   */
  protected int __vector_len(int offset) {
    offset += bb_pos;
    offset += bb.getInt(offset);
    return bb.getInt(offset);
  }

  /**
   * Get the start data of a vector.
   *
   * @param offset An `int` index into the Table's ByteBuffer.
   * @return Returns the start of the vector data whose offset is stored at `offset`.
   */
  protected int __vector(int offset) {
    offset += bb_pos;
    return offset + bb.getInt(offset) + SIZEOF_INT;  // data starts after the length
  }

  /**
   * Get a whole vector as a ByteBuffer.
   *
   * This is efficient, since it only allocates a new {@link ByteBuffer} object,
   * but does not actually copy the data, it still refers to the same bytes
   * as the original ByteBuffer. Also useful with nested FlatBuffers, etc.
   *
   * @param vector_offset The position of the vector in the byte buffer
   * @param elem_size The size of each element in the array
   * @return The {@link ByteBuffer} for the array
   */
  protected ByteBuffer __vector_as_bytebuffer(int vector_offset, int elem_size) {
    int o = __offset(vector_offset);
    if (o == 0) return null;
    ByteBuffer bb = this.bb.duplicate().order(ByteOrder.LITTLE_ENDIAN);
    int vectorstart = __vector(o);
    bb.position(vectorstart);
    bb.limit(vectorstart + __vector_len(o) * elem_size);
    return bb;
  }

  /**
   * Initialize any Table-derived type to point to the union at the given `offset`.
   *
   * @param t A `Table`-derived type that should point to the union at `offset`.
   * @param offset An `int` index into the Table's ByteBuffer.
   * @return Returns the Table that points to the union at `offset`.
   */
  protected Table __union(Table t, int offset) {
    offset += bb_pos;
    t.bb_pos = offset + bb.getInt(offset);
    t.bb = bb;
    return t;
  }

  /**
   * Check if a {@link ByteBuffer} contains a file identifier.
   *
   * @param bb A {@code ByteBuffer} to check if it contains the identifier
   * `ident`.
   * @param ident A `String` identifier of the FlatBuffer file.
   * @return True if the buffer contains the file identifier
   */
  protected static boolean __has_identifier(ByteBuffer bb, String ident) {
    if (ident.length() != FILE_IDENTIFIER_LENGTH)
        throw new AssertionError("FlatBuffers: file identifier must be length " +
                                 FILE_IDENTIFIER_LENGTH);
    for (int i = 0; i < FILE_IDENTIFIER_LENGTH; i++) {
      if (ident.charAt(i) != (char)bb.get(bb.position() + SIZEOF_INT + i)) return false;
    }
    return true;
  }
  @Override
  public String toString()
  {
	  HashMap<String, Object> v = new HashMap<String, Object>();
	  try{
		  Method[] ms = this.getClass().getDeclaredMethods();
		  for(Method m : ms){
			  String sMethodName = m.getName();
			  if(m.getParameterTypes().length==0 && !sMethodName.endsWith("AsByteBuffer")){
				  if(sMethodName.endsWith("Length")){
					  int ii = sMethodName.lastIndexOf("Length");
					  String sMethodName1 = sMethodName.substring(0, ii);
					  List<Object> l = new ArrayList<>();
					  int iLength = 0;
					  try{
						  iLength = (int) m.invoke(this, new Object[]{});
					  }catch(Exception e){}
					  for(int i=0; i< iLength; i++){
						  Method m1 = this.getClass().getDeclaredMethod(sMethodName1,new Class<?>[]{Integer.TYPE});
						  Object oKq = m1.invoke(this, new Object[]{i});
						  l.add(oKq);
					  }
					  v.put(sMethodName1, l);
				  }else{
					  Object oKq = m.invoke(this, new Object[]{});
					  v.put(sMethodName, oKq);
				  }
			  }
		  }
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }
	  return v.toString();
  }
  @SuppressWarnings("unchecked")
@Override
  public int clone(FlatBufferBuilder builder, Map<String, Object> mutate) throws Exception{
	  int root_table = -1;
	  Class<?> cls = this.getClass();
	  HashMap<String, Object> v = new HashMap<String, Object>();
	  try{
		  //b0. phan loai method
		  List<Method> msAdd = new ArrayList<Method>();
		  List<Method> msGet = new ArrayList<Method>();
		  HashMap<String, Method> msCreateVector = new HashMap<String, Method>();
		  Method[] ms = this.getClass().getDeclaredMethods();
		  for(Method m : ms){
			  String sMethodName = m.getName();
			  if(m.getParameterTypes().length==0 && !sMethodName.endsWith("AsByteBuffer"))
				  msGet.add(m);
			  else if(m.getParameterTypes().length==2 && sMethodName.startsWith("add"))
				  msAdd.add(m);
			  else if(m.getParameterTypes().length==2 && sMethodName.startsWith("create") && sMethodName.endsWith("Vector"))
				  msCreateVector.put(sMethodName, m);
		  }
		  //b1. lay ds thuoc tinh va gia tri
		  for(Method m : msGet){
			  String sMethodName = m.getName();
			  if(sMethodName.endsWith("Length")){
				  int ii = sMethodName.lastIndexOf("Length");
				  String sMethodName1 = sMethodName.substring(0, ii);
				  int iLength = 0;
				  try{
					  iLength = (int) m.invoke(this, new Object[]{});
				  }catch(Exception e){}
				  List<Object> l = new ArrayList<>();
				  for(int i=0; i< iLength; i++){
					  Method m1 = this.getClass().getDeclaredMethod(sMethodName1,new Class<?>[]{Integer.TYPE});
					  Object oKq = m1.invoke(this, new Object[]{i});
					  l.add(oKq);
				  }
				  v.put(sMethodName1, l);
			  }else{
				  Object oKq = m.invoke(this, new Object[]{});
				  v.put(sMethodName, oKq);
			  }
		  }
		  //b2. khoi tao gia tri cho builder
		  for(Entry<String, Object> e : v.entrySet()){
			  String sKey = e.getKey();
			  Object oValue = e.getValue();
			  Object oNewValue = mutate!=null?mutate.get(sKey):null;
			  if(oValue instanceof String || oNewValue instanceof String){
				  int keyOffset = builder.createString(oNewValue==null?oValue.toString():oNewValue.toString());
				  v.put(sKey, keyOffset);
			  }
			  else if(oValue instanceof List  || oNewValue instanceof List){
				  List<?> oV = (List<?>) (oNewValue==null?oValue:oNewValue);
				  int iLen = ((List<?>) oV).size();
				  if(iLen <=0) 
					  v.put(sKey, null);
				  else{
					  Object obj = ((List<?>) oV).get(0);
					  if(obj instanceof Table || obj instanceof Struct){
						  int[] keyOffsetList = new int[iLen];
						  boolean isHasValue = false;
						  for(int i=0; i<iLen; i++){
							  obj = ((List<?>) oV).get(i);
							  int offset = ((IFlatBuffer)obj).clone(builder, null);
							  if(offset!=-1){
								  keyOffsetList[i] = offset;
								  isHasValue = true;
							  }
						  }
						  if(isHasValue){
							  int keyOffset = -1;
							  Method m = cls.getDeclaredMethod("create"+WordUtils.capitalize(sKey)+"Vector", new Class<?>[]{FlatBufferBuilder.class, int[].class});
							  keyOffset = (int) m.invoke(null, new Object[]{builder, keyOffsetList});
							  if(keyOffset != -1)
								  v.put(sKey, keyOffset);
						  }
					  }
					  else if(obj instanceof String){
						  int[] keyOffsetList = new int[iLen];
						  boolean isHasValue = false;
						  for(int i=0; i<iLen; i++){
							  obj = ((List<String>) oV).get(i);
							  int offset = builder.createString((CharSequence) obj);
							  if(offset!=-1){
								  keyOffsetList[i] = offset;
								  isHasValue = true;
							  }
						  }
						  if(isHasValue){
							  int keyOffset = -1;
							  Method m = cls.getDeclaredMethod("create"+WordUtils.capitalize(sKey)+"Vector", new Class<?>[]{FlatBufferBuilder.class, int[].class});
							  keyOffset = (int) m.invoke(null, new Object[]{builder, keyOffsetList});
							  if(keyOffset != -1)
								  v.put(sKey, keyOffset);
						  }
					  }
					  else {
						  int keyOffset = -1;
						  Method m = msCreateVector.get("create"+WordUtils.capitalize(sKey)+"Vector");
						  Class<?> subCls = Class.forName(m.getParameterTypes()[1].getName());
						  Class<?> objType = subCls.getComponentType();
						  String objTypeName = objType.getSimpleName();
						  Method mo = Number.class.getDeclaredMethod(objTypeName+"Value", new Class<?>[]{});
						  Object aObject = Array.newInstance(objType, iLen);
						  for (int i=0; i<iLen; i++)
					          Array.set(aObject, i, mo.invoke(((List<Number>) oV).get(i), new Object[]{}));
						  keyOffset = (int) m.invoke(null, new Object[]{builder, aObject});
						  if(keyOffset != -1)
							  v.put(sKey, keyOffset);
					  }
				  }
			  }
			  else if(oValue instanceof Table || oValue instanceof Struct || oNewValue instanceof Table || oNewValue instanceof Struct){
				  int keyOffset = -1;
				  if(oNewValue!=null)
					  keyOffset = ((IFlatBuffer) oNewValue).clone(builder, mutate);
				  else
					  keyOffset = ((IFlatBuffer) oValue).clone(builder, mutate);
				  if(keyOffset != -1)
					  v.put(sKey, keyOffset);
			  }
			  else{
				  if(oNewValue!=null)
					  v.put(sKey, oNewValue);
			  }
		  }
		  //b3. gan gia tri cho clone object
		  Method m = cls.getDeclaredMethod("start"+cls.getSimpleName(), new Class<?>[]{FlatBufferBuilder.class});
		  m.invoke(null, new Object[]{builder});
		  for(Method mAdd : msAdd){
			  String sFieldName = mAdd.getName().replace("add", "");
			  sFieldName = WordUtils.uncapitalize(sFieldName);
			  Object oFieldValue = v.get(sFieldName);
			  if(oFieldValue!=null && !(oFieldValue instanceof Table || oFieldValue instanceof Struct)){
				  mAdd.invoke(null, new Object[]{builder, oFieldValue});
			  }
		  }
		  m = cls.getDeclaredMethod("end"+cls.getSimpleName(), new Class<?>[]{FlatBufferBuilder.class});
		  root_table = (int) m.invoke(null, new Object[]{builder});
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw e;
	  }
	  return root_table;
  }
  @SuppressWarnings("unchecked")
  @Override
  public <Any> Any clone(Map<String, Object> mutate) throws Exception{
	  Object oData = null;
	  Class<?> cls = this.getClass();
	  try{
		  FlatBufferBuilder b = new FlatBufferBuilder();
		  int root_table = this.clone(b, mutate);
		  b.finish(root_table);
		  Method m = cls.getDeclaredMethod("getRootAs"+cls.getSimpleName(), new Class<?>[]{ByteBuffer.class});
		  oData = m.invoke(null, new Object[]{b.dataBuffer()});
	  }catch(Exception e){
		  e.printStackTrace();
		  throw e;
	  }
	  return (Any)cls.cast(oData);
  }
  @Override
  public Object clone(){
	  try {
		return clone(null);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
  }
}

/// @endcond
