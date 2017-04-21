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

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// @cond FLATBUFFERS_INTERNAL

/**
 * All structs in the generated code derive from this class, and add their own accessors.
 */
public class Struct implements IFlatBuffer{
  /** Used to hold the position of the `bb` buffer. */
  protected int bb_pos;
  /** The underlying ByteBuffer to hold the data of the Struct. */
  protected ByteBuffer bb;
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
					  int iLength = (int) m.invoke(this, new Object[]{});
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
	@Override
	public int clone(FlatBufferBuilder builder, Map<String, Object> mutate) throws Exception{
		// TODO Auto-generated method stub
		return -1;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <Any> Any clone(Map<String, Object> mutate) throws Exception{
		// TODO Auto-generated method stub
		return (Any) this;
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
