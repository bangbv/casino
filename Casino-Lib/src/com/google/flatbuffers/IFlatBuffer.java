package com.google.flatbuffers;

import java.util.Map;

public interface IFlatBuffer {
	public int clone(FlatBufferBuilder builder, Map<String, Object> mutate) throws Exception;
	public <Any> Any clone(Map<String, Object> mutate) throws Exception;
}
