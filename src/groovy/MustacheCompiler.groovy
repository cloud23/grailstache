package com.gtunes

import java.io.Reader
import java.io.BufferedReader
import com.sampullara.util.FutureWriter
import com.sampullara.mustache.MustacheBuilder

class MustacheCompiler {
   static def compileMustache(def model, Reader reader) {
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream()
    FutureWriter writer = new FutureWriter(new OutputStreamWriter(baos))
    
    new MustacheBuilder()
            .build(reader, "mustacheOutput")
            .execute(writer, model as Map)
    writer.flush()

    return baos.toString()    
  }

  def test(){
    return "test"
  }
}
