package com.doc360.apibridge.utility;

import java.io.IOException;

/**
 * Mapper interface.
 * 
 * @param <I>
 * @param <O>
 */
public interface Mapper<I, O> {
	public O map(I input) throws IOException;
}
